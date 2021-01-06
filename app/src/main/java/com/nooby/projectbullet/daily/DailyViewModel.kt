package com.nooby.projectbullet.daily

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nooby.projectbullet.database.Bullet
import com.nooby.projectbullet.database.BulletDatabaseDao
import com.nooby.projectbullet.database.BulletType
import kotlinx.coroutines.*
import java.util.*

data class Day(
    val name: String,
    var bullets: MutableLiveData<List<Bullet>>,
    val dayStart: Date,
    val dayEnd: Date
)

//DailyViewModel keeps track of all data and updates live data
class DailyViewModel(
    private val database: BulletDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    //Creates the list of all jobs to be run and stops all jobs if view model is destroyed
    private var viewModelJob = Job()
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var currentWeek = MutableLiveData<List<Day>>()
    private var currentDay = 0
    var currentWeekNumber = 0

    var newBulletType = BulletType.NOTE

    init {
        GetWeek(0)
    }

    //GetWeek updates the list of current days to the new week
    fun GetWeek(numWeek: Int) {
        uiScope.launch {
            Log.i("DailyViewModel", "Changing week to $numWeek")
            //Gets the time for the start of the week
            val newCurrentDayStart = Calendar.getInstance()
            val newCurrentDayEnd = Calendar.getInstance()
            currentWeekNumber = numWeek
            newCurrentDayStart.set(Calendar.HOUR_OF_DAY, 0)
            newCurrentDayStart.set(Calendar.MINUTE, 0)
            newCurrentDayStart.set(Calendar.SECOND, 0)
            newCurrentDayStart.set(Calendar.MILLISECOND, 0)
            newCurrentDayStart.set(Calendar.DAY_OF_WEEK, newCurrentDayStart.firstDayOfWeek)
            newCurrentDayStart.add(Calendar.WEEK_OF_YEAR, currentWeekNumber)

            newCurrentDayEnd.time = newCurrentDayStart.time
            newCurrentDayEnd.set(Calendar.HOUR_OF_DAY, 23)
            newCurrentDayEnd.set(Calendar.MINUTE, 59)
            newCurrentDayEnd.set(Calendar.SECOND, 59)
            newCurrentDayEnd.set(Calendar.MILLISECOND, 99)

            val tmpList = mutableListOf<Day>()
            tmpList.add(
                Day(
                    name = "Start Day",
                    bullets = MutableLiveData<List<Bullet>>(),
                    dayStart = newCurrentDayStart.time,
                    dayEnd = newCurrentDayEnd.time
                )
            )
            //Goes through each day in the week and adds it to the current week list
            for (i in 1..7) {
                tmpList.add(
                    Day(
                        name = newCurrentDayStart.time.toString().dropLast(17),
                        bullets = MutableLiveData<List<Bullet>>(
                            getBullets(
                                newCurrentDayStart.time,
                                newCurrentDayEnd.time
                            )
                        ),
                        dayStart = newCurrentDayStart.time,
                        dayEnd = newCurrentDayEnd.time
                    )
                )
                newCurrentDayStart.add(Calendar.DATE, 1)
                newCurrentDayEnd.add(Calendar.DATE, 1)
            }

            tmpList.add(
                Day(
                    name = "Start Day",
                    bullets = MutableLiveData<List<Bullet>>(),
                    dayStart = newCurrentDayStart.time,
                    dayEnd = newCurrentDayEnd.time
                )
            )

            currentWeek.value = tmpList
        }
    }

    //UpdateDay gets the bullets for the current day from the database and updates the value
    fun updateDay() {
        uiScope.launch {
            //Updates the current days bullets
            if (currentWeek.value != null) {
                val tmpList = currentWeek.value!!
                val tmpDay = tmpList[currentDay]
                tmpDay.bullets.value = getBullets(tmpDay.dayStart, tmpDay.dayEnd)
                for (day in tmpList) {
                    Log.i("DailyViewModel", "$day tm: 1${day.bullets.value}")
                }
                currentWeek.value?.get(currentDay).let {
                    if (it != null) {
                        it.bullets.value = getBullets(it.dayStart, it.dayEnd)
                        Log.i("DailyViewModel", "Updating day ${it.bullets.value}")
                    }
                }
            }
        }
    }

    //getBullets gets all the bullets between the start and end of the current day
    private suspend fun getBullets(dayStart: Date, dayEnd: Date): List<Bullet> {
        return withContext(Dispatchers.IO) {
            database.get(dayStart, dayEnd)
        }
    }

    private fun getBulletDay(bullet: Bullet): Pair<Date, Date> {
        val newCal = Calendar.getInstance()
        newCal.time = bullet.bulletDate
        newCal.set(Calendar.HOUR_OF_DAY, 23)
        newCal.set(Calendar.MINUTE, 59)
        newCal.set(Calendar.SECOND, 59)
        newCal.set(Calendar.MILLISECOND, 99)

        val newDate = newCal.time
        Log.i("DailyViewModel","Got bullet day ${bullet.bulletDate}, $newDate")
        return Pair(bullet.bulletDate, newDate)
    }

    //Adds the bullet to the database and resets the current bullets
    private suspend fun addBullet(bullet: Bullet): List<Bullet> {
        return withContext(Dispatchers.IO) {
            database.insert(bullet)
            val (dayStart, dayEnd) = getBulletDay(bullet)
            database.get(dayStart, dayEnd)
        }
    }

    //updateBullet updates the bullet in the database
    private suspend fun updateBullet(bullet: Bullet): List<Bullet> {
        return withContext(Dispatchers.IO) {
            database.update(bullet)
            val (dayStart, dayEnd) = getBulletDay(bullet)
            database.get(dayStart, dayEnd)
        }
    }

    //removeBullet deletes the given bullet from the database using its id
    private suspend fun removeBullet(bullet: Bullet): List<Bullet> {
        return withContext(Dispatchers.IO) {
            database.deleteBullet(bullet)
            val (dayStart, dayEnd) = getBulletDay(bullet)
            database.get(dayStart, dayEnd)
        }
    }

    //Creates a new bullet with the given message,date and type
    fun createBullet(message: String, day: Int) {
        uiScope.launch {
            val tmpDay = currentWeek.value?.get(day)
            Log.i("DailyViewModel", "Creating bullet for day $tmpDay")
            if (tmpDay != null) {
                val newBullet = Bullet(
                    message = message,
                    bulletDate = tmpDay.dayStart,
                    BulletType = newBulletType
                )
                currentWeek.value!![day].bullets.value = addBullet(newBullet)
                Log.i("DailyViewModel", "Successfully added bullet")
            }
        }
    }

    //changeBullet updates the bullet and refreshes the list
    fun changeBullet(bullet: Bullet, day: Int) {
        uiScope.launch {
            currentWeek.value?.get(day)?.bullets?.value = updateBullet(bullet)
            Log.i("DailyViewModel", "Successfully updated bullet")
        }
    }

    //DeleteBullet removes the bullet from the database and refreshes the list
    fun deleteBullet(bullet: Bullet, day: Int) {
        uiScope.launch {
            currentWeek.value?.get(day)?.bullets?.value = removeBullet(bullet)
            Log.i("DailyViewModel", "Successfully deleted bullet")
        }
    }
}
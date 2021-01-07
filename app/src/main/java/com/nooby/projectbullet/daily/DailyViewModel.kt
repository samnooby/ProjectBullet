package com.nooby.projectbullet.daily

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.nooby.projectbullet.database.Bullet
import com.nooby.projectbullet.database.BulletDatabaseDao
import com.nooby.projectbullet.database.BulletType
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

data class Day(
    val name: String,
    var bullets: MutableLiveData<List<Bullet>>,
    val dayStart: LocalDateTime,
    val dayEnd: LocalDateTime
)

const val PAGE_LIMIT = 151

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
    private var currentDay = LocalDate.now()
    var currentWeekNumber = 0
    private val bulletFormatter = DateTimeFormatter.ofPattern("EEEE LLLL dd, yyyy")

    var newBulletType = BulletType.NOTE

    init {
        getWeek()
    }

    //GetWeek updates the list of current days to the new week
    fun getWeek(numWeek: Int = 0, newCurrentDay: LocalDate? = null) {
        uiScope.launch {
            Log.i("DailyViewModel", "Changing week to $numWeek")
            if (newCurrentDay != null) {
                currentDay = newCurrentDay
            }
            //Gets the time for the start and end of first page
            var dayStart = currentDay.atStartOfDay()
            dayStart = dayStart.plusDays((numWeek* PAGE_LIMIT - PAGE_LIMIT/2).toLong())
            currentWeekNumber = numWeek
            var dayEnd = dayStart.plusMinutes((11*60) + 59)
            Log.i("DailyViewModel", "Start is $dayStart, end is $dayEnd")

            //Adds all the days in the page list and get their bullets from the database
            var daysList = mutableListOf<Day>()

            for (day in 1..(PAGE_LIMIT+2)) {
                daysList.add(Day(
                    dayStart.format(bulletFormatter),
                    MutableLiveData(getBullets(dayStart, dayEnd)),
                    dayStart,
                    dayEnd
                ))
                dayStart = dayStart.plusDays(1)
                dayEnd = dayEnd.plusDays(1)
            }
            Log.i("DailyViewModel", "Start is $dayStart, end is $dayEnd")

            currentWeek.value = daysList
        }
    }
    //getBullets gets all the bullets between the start and end of the current day
    private suspend fun getBullets(dayStart: LocalDateTime, dayEnd: LocalDateTime): List<Bullet> {
        return withContext(Dispatchers.IO) {
            database.get(dayStart, dayEnd)
        }
    }

    private fun getBulletDay(bullet: Bullet): Pair<LocalDateTime, LocalDateTime> {
        val newDate = bullet.bulletDate.plusMinutes((11*60) + 59)
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
                    bulletType = newBulletType,
                    bulletNotes = listOf("This is a random note")
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
package com.nooby.projectbullet.daily

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.nooby.projectbullet.database.*
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

const val PAGE_LIMIT = 101

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

    //The scope of the main thread of the dailyviewmodel
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var currentDays = MutableLiveData<List<Day>>()
    private var currentDay = LocalDate.now()

    //The number of pages away from the current day
    var currentPageNumber = 0
    private val bulletFormatter = DateTimeFormatter.ofPattern("EEEE LLLL dd, yyyy")

    var newBulletType = BulletType.NOTE

    init {
        getDays(0)
    }

    //getDays returns PAGE_LIMIT days with the index of currentday at PAGE_LIMIT/2. Change
    //currentDay with newCurrentDay or newCurrentDayNumber, where newCurrentDayNumber references a
    //index in the currentDays array.
    fun getDays(
        numPages: Int = currentPageNumber,
        newCurrentDay: LocalDate? = null,
        newCurrentDayNumber: Int = 0
    ) {
        uiScope.launch {
            Log.i("DailyViewModel", "Changing week to $numPages")
            //Sets the new current day if any was passed in
            if (newCurrentDay != null) {
                currentDay = newCurrentDay
            } else if (newCurrentDayNumber != 0) {
                val day = currentDays.value?.get(newCurrentDayNumber)
                if (day != null) {
                    currentDay = day.dayStart.toLocalDate()
                }
            }
            //Gets the time for the start and end of the first day
            var dayStart = currentDay.atStartOfDay()
            dayStart = dayStart.plusDays((numPages * PAGE_LIMIT - PAGE_LIMIT / 2).toLong())
            currentPageNumber = numPages
            var dayEnd = dayStart.plusMinutes((11 * 60) + 59)
            Log.i("DailyViewModel", "Start is $dayStart, end is $dayEnd")

            //For each day it tries to retrieve it from the database, if the day does not exist a
            //new day is created
            var daysList = mutableListOf<Day>()

            for (day in 1..(PAGE_LIMIT + 2)) {
                var currentDay = getDay(dayStart)
                if (currentDay == null) {
                    currentDay = Day(
                        name = dayStart.format(bulletFormatter),
                        dayStart = dayStart,
                        dayEnd = dayEnd,
                        bulletOrder = emptyList()
                    )
                } else {
                    Log.i("DailyViewModel", "Got day $currentDay")
                    currentDay.bullets.value = getBullets(currentDay)
                }
                daysList.add(currentDay)
                dayStart = dayStart.plusDays(1)
                dayEnd = dayEnd.plusDays(1)
            }
            Log.i("DailyViewModel", "Start is $dayStart, end is $dayEnd")

            currentDays.value = daysList
        }
    }

    //getBullets gets all the bullets between the start and end of the current day
    private suspend fun getBullets(day: Day): List<BulletWithTag> {
        return withContext(Dispatchers.IO) {
            var bullets = database.getBullets(day.dayStart, day.dayEnd)
            //Orders the list by the order saved in the day
            bullets = bullets.sortedBy { bullet ->
                day.bulletOrder.indexOfFirst {
                    bullet.bullet.bulletId == it
                }
            }
            bullets
        }
    }

    //getDay gets the day with the given start time from the database
    private suspend fun getDay(dayStart: LocalDateTime): Day {
        return withContext(Dispatchers.IO) {
            database.getDay(dayStart)
        }
    }

    private suspend fun updateDayValue(day: Day) {
        return withContext(Dispatchers.IO) {
            database.updateDay(day)
        }
    }


    private fun getBulletDay(bullet: Bullet): Pair<LocalDateTime, LocalDateTime> {
        val newDate = bullet.bulletDate.plusMinutes((11 * 60) + 59)
        Log.i("DailyViewModel", "Got bullet day ${bullet.bulletDate}, $newDate")
        return Pair(bullet.bulletDate, newDate)
    }

    //Adds the bullet to the database and resets the current bullets
    private suspend fun addBullet(bullet: Bullet, day: Day, insertPosition: Int): List<BulletWithTag> {
        return withContext(Dispatchers.IO) {
            Log.i(
                "DailyViewModel",
                "Adding to list ${day.bulletOrder} into postition $insertPosition"
            )
            val newBulletOrder = if (day.bulletOrder.isNotEmpty()) day.bulletOrder as MutableList<Long> else mutableListOf()
            newBulletOrder.add(insertPosition, database.insert(bullet))
            day.bulletOrder = newBulletOrder
            Log.i("DailyViewModel", "Added new id to list got ${day.bulletOrder}")
            database.updateDay(day)
            getBullets(day)
        }
    }

    //updateBullet updates the bullet in the database
    private suspend fun updateBullet(bullet: Bullet, day: Day): List<BulletWithTag> {
        return withContext(Dispatchers.IO) {
            database.update(bullet)
            val days = getBullets(day)
            //Deletes the day to save space if it has no bullets
            if (days.isEmpty()) {
                database.deleteDay(day)
            }
            //Adds a day if there is no day in the day transfering to
            val (start, end) = getBulletDay(bullet)
            if (start != day.dayStart && end != day.dayEnd) {
                val toDays = database.getDay(start)
                Log.i("DailyViewModel", "got day $toDays")
                if (toDays == null) {
                    database.updateDay(
                        Day(
                            name = start.format(bulletFormatter),
                            dayStart = start,
                            dayEnd = end,
                            bulletOrder = listOf(bullet.bulletId)
                        )
                    )
                }
            }
            days
        }
    }

    //removeBullet deletes the given bullet from the database using its id
    private suspend fun removeBullet(bullet: Bullet, day: Day): List<BulletWithTag> {
        return withContext(Dispatchers.IO) {
            //Deletes the bullet and refreshes the bullets from the database
            database.deleteBullet(bullet)
            val bullet = getBullets(day)
            //Deletes the day to save space if it has no bullets
            if (bullet.isEmpty()) {
                Log.i("DailyViewModel", "Deleting day")
                database.deleteDay(day)
            }
            bullet
        }
    }

    //Creates a new bullet with the given message,date and type
    fun createBullet(message: String, day: Int, insertPosition: Int) {
        uiScope.launch {
            val tmpDay = currentDays.value?.get(day)
            Log.i("DailyViewModel", "Creating bullet for day $tmpDay")
            if (tmpDay != null) {
                val newBullet = Bullet(
                    message = message,
                    bulletDate = tmpDay.dayStart,
                    bulletType = newBulletType
                )
                currentDays.value!![day].bullets.value =
                    addBullet(newBullet, tmpDay, insertPosition)
                Log.i("DailyViewModel", "Successfully added bullet")
            }
        }
    }

    //changeBullet updates the bullet and refreshes the list
    fun changeBullet(bullet: Bullet, day: Int) {
        uiScope.launch {
            val bulletDay = currentDays.value?.get(day)
            if (bulletDay != null) {
                bulletDay.bullets?.value = updateBullet(bullet, bulletDay)
            }
            Log.i("DailyViewModel", "Successfully updated bullet")
        }
    }

    //DeleteBullet removes the bullet from the database and refreshes the list
    fun deleteBullet(bullet: Bullet, day: Int) {
        uiScope.launch {
            val bulletDay = currentDays.value?.get(day)
            if (bulletDay != null) {
                bulletDay.bullets?.value = removeBullet(bullet, bulletDay)
            }
            Log.i("DailyViewModel", "Successfully deleted bullet")
        }
    }

    fun addBulletOrder(order: List<Long>, day: Day) {
        uiScope.launch {
            day.bulletOrder = order
            updateDayValue(day)
        }
    }

}
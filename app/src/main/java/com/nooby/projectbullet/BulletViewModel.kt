package com.nooby.projectbullet

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.nooby.projectbullet.database.Day
import com.nooby.projectbullet.database.DayDao
import kotlinx.coroutines.*
import java.util.*

class BulletViewModel(
    val database: DayDao,
    application: Application
) : AndroidViewModel(application) {

    private var viewModelJob = Job()

    //When this view model is destroyed stop all jobs that are currently pending
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    //The scope that all the threads will use
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    //Prepares the currentday and gets all the days from the database
    private var currentDay = MutableLiveData<Day?>()
    private var currentDayIndex = 0
    private val days = database.getAllDays()

    init {
        initializeToday()
    }

    //Sets the value of the current day to the one retrieved from the database
    private fun initializeToday() {
        //Launches a new thread to not block other processes
        uiScope.launch {
            currentDay.value = getTodayFromDatabase()
        }
    }

    //Gets the current day from the database
    private suspend fun getTodayFromDatabase(): Day {
        //Blocks the other threads from accessing the database to avoid conflict
        return withContext(Dispatchers.IO) {
            val now = Calendar.getInstance()
            now.set(Calendar.HOUR_OF_DAY, 0)
            now.set(Calendar.MINUTE, 0)
            now.set(Calendar.SECOND, 0)
            now.set(Calendar.MILLISECOND, 0)

            var tmpDay = database.get(now.time.time)
            if (tmpDay == null) {
                tmpDay = Day(now.time)
                database.insert(tmpDay)
            }
            tmpDay
        }
    }
}
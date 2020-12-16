package com.nooby.projectbullet.daily

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nooby.projectbullet.database.Bullet
import com.nooby.projectbullet.database.BulletDatabaseDao
import kotlinx.coroutines.*
import java.util.*

//DailyViewModel keeps track of all data and updates live data
class DailyViewModel(
        val database: BulletDatabaseDao,
        application: Application
) : AndroidViewModel(application) {

    //Creates the list of all jobs to be run and stops all jobs if view model is destroyed
    private var viewModelJob = Job()
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    //Current day and its bullets
    private var currentDayStart = Calendar.getInstance()
    private var currentDayEnd = Calendar.getInstance()
    var bullets : LiveData<List<Bullet>> = MutableLiveData<List<Bullet>>()
    var dayName = MutableLiveData<String>()

    init {
        initializeFirstDay()
    }

    //initializeFirstDay sets the current day to today and gets the first bullets
    private fun initializeFirstDay() {
        uiScope.launch {
            //Sets the start and end of the day
            currentDayStart.set(Calendar.HOUR_OF_DAY, 0)
            currentDayStart.set(Calendar.MINUTE, 0)
            currentDayStart.set(Calendar.SECOND, 0)
            currentDayStart.set(Calendar.MILLISECOND, 0)
            currentDayEnd.set(Calendar.HOUR_OF_DAY, 0)
            currentDayEnd.set(Calendar.MINUTE, 0)
            currentDayEnd.set(Calendar.SECOND, 0)
            currentDayEnd.set(Calendar.MILLISECOND, 0)
            currentDayEnd.add(Calendar.DATE, 1)
            dayName.value = currentDayStart.time.toString().dropLast(17)

            //Gets the bullets for the first day
            bullets = getBullets()
            Log.i("DailyViewModel", "Initialized day")
        }
    }

    //getBullets gets all the bullets between the start and end of the current day
    private suspend fun getBullets(): LiveData<List<Bullet>> {
        return withContext(Dispatchers.IO) {
            database.get(currentDayStart.time, currentDayEnd.time)
        }
    }

    //moveDay moves the current day numDays forward (Can be negative)
    fun moveDay(numDays: Int) {
        uiScope.launch {
            currentDayStart.add(Calendar.DATE, numDays)
            currentDayEnd.add(Calendar.DATE, numDays)
            dayName.value = currentDayStart.time.toString().dropLast(17)

            bullets = getBullets()
            Log.i("DailyViewModel", "Moved $numDays days")
        }
    }
}
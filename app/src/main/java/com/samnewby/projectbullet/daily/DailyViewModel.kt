package com.samnewby.projectbullet.daily

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.samnewby.projectbullet.database.BulletDao
import com.samnewby.projectbullet.database.Day
import com.samnewby.projectbullet.database.DayDao
import com.samnewby.projectbullet.database.DayWithBulletsAndTags
import kotlinx.coroutines.*
import java.time.LocalDate

const val PAGE_LIMIT = 51

class DailyViewModel(private val dailyDatabase: DayDao, private val bulletDatabase: BulletDao, application: Application): AndroidViewModel(application) {

    private var viewModelJob = Job()
    private var uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var days = MutableLiveData<List<DayWithBulletsAndTags>>()
    var periodStart = LocalDate.now().plusDays((PAGE_LIMIT / 2).toLong())
    var periodEnd = LocalDate.now().minusDays((PAGE_LIMIT / 2).toLong())

    init {
        refreshDays()
    }

    //Sets the days to the new list from the database
    fun refreshDays() {
        uiScope.launch {
            days = getDaysFromDatabase() as MutableLiveData<List<DayWithBulletsAndTags>>
        }
    }

    private suspend fun getDaysFromDatabase(): LiveData<List<DayWithBulletsAndTags>> {
        return withContext(Dispatchers.IO) {
            dailyDatabase.getDays(periodStart, periodEnd)
        }
    }

    fun updateOrInsertDay(day: Day) {
        uiScope.launch {
            val currentDays = days.value!!
            
        }
    }

    private suspend fun insertDayToDatabase(day: Day) {
        return withContext(Dispatchers.IO) {
            dailyDatabase.insertDay(day)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
package com.samnewby.projectbullet.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.samnewby.projectbullet.database.BulletDao
import com.samnewby.projectbullet.database.BulletsDatabase.Companion.getDatabase
import com.samnewby.projectbullet.database.DayDao
import com.samnewby.projectbullet.repository.DailyRepository
import kotlinx.coroutines.*

const val PAGE_LIMIT = 51

class DailyViewModel(private val dailyDatabase: DayDao, private val bulletDatabase: BulletDao, application: Application): AndroidViewModel(application) {

    private var viewModelJob = Job()
    private var uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val database = getDatabase(application)
    private val dailyRepository = DailyRepository(database)

    init {
        uiScope.launch {
        }
    }

    val days = dailyRepository.days

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
package com.samnewby.projectbullet.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.samnewby.projectbullet.database.BulletsDatabase
import com.samnewby.projectbullet.domain.Day

const val DAY_LIMIT = 100

class DailyRepository(private val database: BulletsDatabase) {

    //The currently shown days
    private val _days: MutableLiveData<List<Day>> = MutableLiveData()
    val days: LiveData<List<Day>>
        get() {
            return _days
        }

    
}
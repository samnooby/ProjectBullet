package com.nooby.projectbullet

import android.util.Log
import androidx.lifecycle.ViewModel
import java.util.*
import kotlin.collections.HashMap

class BulletViewModel: ViewModel() {

    var days: HashMap<Long, Day> = HashMap()
    var currentDay: Day

    init {
        Log.i("BulletViewModel", "BulletViewModel created")
        var now = Calendar.getInstance()
        now.set(Calendar.HOUR_OF_DAY, 0)
        now.set(Calendar.SECOND, 0)
        now.set(Calendar.MILLISECOND, 0)
        currentDay = Day(now.time.time, now.time, null, 0)
        days[now.time.time] = currentDay
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("BulletViewModel", "BulletViewModel destroyed")
    }
}
package com.nooby.projectbullet

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nooby.projectbullet.database.DayDao
import java.lang.IllegalArgumentException

class BulletViewModelFactory (
    private val dataSource: DayDao,
    private val application: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BulletViewModel::class.java)) {
            return BulletViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
    }
package com.nooby.projectbullet.bullet

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nooby.projectbullet.daily.DailyViewModel
import com.nooby.projectbullet.database.BulletDatabaseDao
import java.lang.IllegalArgumentException

//Creates a new view model with the given datasource and application, allows for multiple viewmodels
//to use the same context
class BulletViewModelFactory(
        private val dataSource: BulletDatabaseDao,
        private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DailyViewModel::class.java)) {
                return DailyViewModel(dataSource, application) as T
                }
        throw IllegalArgumentException("Unknown View Model class")
            }
        }
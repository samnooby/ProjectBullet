package com.samnewby.projectbullet.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.samnewby.projectbullet.database.BulletDao
import com.samnewby.projectbullet.database.DayDao
import com.samnewby.projectbullet.database.TagDao
import java.lang.IllegalArgumentException

//Creates a new view model with the given datasource and application, allows for multiple viewmodels
//to use the same context
class BulletViewModelFactory(
    private val application: Application,
    private val bulletAccess: BulletDao,
    private val tagAccess: TagDao? = null,
    private val dayAccess: DayDao? = null) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DailyViewModel::class.java)) {
            return DailyViewModel(
                dayAccess!!,
                bulletAccess,
                application
            ) as T
        }
        if (modelClass.isAssignableFrom(TagViewModel::class.java)) {
            return TagViewModel(
                tagAccess!!,
                bulletAccess,
                application
            ) as T
        }

        throw IllegalArgumentException("Unknown View Model class")
    }
}
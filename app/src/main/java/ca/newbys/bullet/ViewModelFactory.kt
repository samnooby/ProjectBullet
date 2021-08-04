package ca.newbys.bullet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ca.newbys.bullet.daily.DailyViewModel
import ca.newbys.bullet.database.BulletDao

class ViewModelFactory(private val database: BulletDao): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DailyViewModel(database) as T
    }
}
package ca.newbys.bullet.daily

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ca.newbys.bullet.database.Bullet
import ca.newbys.bullet.database.BulletDao
import ca.newbys.bullet.database.Day
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.*

class DailyViewModel(private val database: BulletDao) : ViewModel() {

    //Every Daily View Model runs on the same coroutine scope and if it is cleared all jobs are cancelled
    private var viewModelJob = Job()
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    //The list of days which is shown on the page
    var Days = MutableLiveData<List<Day>>()

    init {
        //Gets all the days from the database and adds the bullets to each day
        uiScope.launch {
            val currentDays = database.getAllDays()
            currentDays.forEach {
                it.bullets.value =
                    database.getBullets(it.date)
            }
            Days.value = currentDays
        }
    }

}
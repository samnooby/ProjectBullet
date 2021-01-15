package com.samnewby.projectbullet.bullet

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.samnewby.projectbullet.database.BulletsDatabase

class BulletViewModel(val database: BulletsDatabase, application: Application): AndroidViewModel(application) {

}
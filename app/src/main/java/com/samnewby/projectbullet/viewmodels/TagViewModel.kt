package com.samnewby.projectbullet.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.samnewby.projectbullet.database.BulletDao
import com.samnewby.projectbullet.database.TagDao

class TagViewModel(tagDatabase: TagDao, bulletDatabase: BulletDao, application: Application): AndroidViewModel(application) {
}
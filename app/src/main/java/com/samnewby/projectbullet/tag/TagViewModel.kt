package com.samnewby.projectbullet.tag

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.samnewby.projectbullet.database.BulletDao
import com.samnewby.projectbullet.database.TagDao

class TagViewModel(tagDatabase: TagDao, bulletDatabase: BulletDao, application: Application): AndroidViewModel(application) {
}
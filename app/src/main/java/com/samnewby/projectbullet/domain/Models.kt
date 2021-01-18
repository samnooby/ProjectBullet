package com.samnewby.projectbullet.domain

import androidx.lifecycle.MutableLiveData
import com.samnewby.projectbullet.database.BulletType
import java.time.LocalDate

data class Bullet(
    var title: String,
    val createDate: LocalDate,
    var bulletType: BulletType,
    var notes: List<String>,
    var day: LocalDate,
    var tags: List<Tag>
)

data class Day(
    val date: LocalDate,
    val name: String,
    var bullets: List<Bullet>
)

data class Tag(
    var name: String,
    var color: Long
)

data class TagWithBullets(
    var name: String,
    var color: Long,
    var bullets: List<Bullet>
)
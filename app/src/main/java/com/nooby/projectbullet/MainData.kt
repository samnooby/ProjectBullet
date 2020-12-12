package com.nooby.projectbullet

import java.util.*

//The data class used for general information shown on each screen
data class MainData(
        var title: String
)

//BulletType is the different types of bullets the user can create
enum class BulletType {
    NOTE, INCOMPLETETASK, COMPLETETASK, EVENT
}

//BulletTag is the different types of tags the user can add to a bullet
enum class BulletTag {
    URGENT, COOLIDEA, PRIORITY, DELEGATED
}

//Bullet are notes, tasks or events the user can create in their bullet journal.
class Bullet(
        val id: Int,
        var dayId: Int,
        var title: String,
        var message: String,
        var type: BulletType,
        var tag: BulletTag,
        var subBullets: Bullet,
        var nextBullet: Bullet,
        var prevBullet: Bullet
){
    val createDate = Calendar.getInstance().time
}



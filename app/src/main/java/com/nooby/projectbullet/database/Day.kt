package com.nooby.projectbullet.database

import androidx.lifecycle.MutableLiveData
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "day_table")
data class Day(

    @PrimaryKey(autoGenerate = true)
    var dayId: Long = 0,

    @ColumnInfo(name = "name")
    var name: String = "",

    @ColumnInfo(name = "day_start")
    var dayStart: LocalDateTime = LocalDateTime.now(),

    @ColumnInfo(name = "day_end")
    var dayEnd: LocalDateTime = LocalDateTime.now(),

    @ColumnInfo(name = "bullet_order")
    var bulletOrder: List<Long>
) {
    @Ignore
    var bullets: MutableLiveData<List<BulletWithTag>> = MutableLiveData()
}


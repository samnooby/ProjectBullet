package com.nooby.projectbullet.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "tag_table")
data class Tag(
    @PrimaryKey(autoGenerate = true)
    var tagId: Long = 0,

    @ColumnInfo(name = "name")
    var name: String = "",

    @ColumnInfo(name = "create_date")
    val createDate: LocalDateTime = LocalDateTime.now(),

    @ColumnInfo(name = "bullet_order")
    var bulletOrder: List<Long>,

    @ColumnInfo(name = "color")
    var color: String

)
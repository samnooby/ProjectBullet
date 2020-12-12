package com.nooby.projectbullet.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

//Day is the class that contains bullets and is what is shown to the user, every bullet must have a
//day
@Entity(tableName = "days_table")
data class Day (
        @ColumnInfo(name = "date")
        val date: Date
        ){
        @PrimaryKey()
        var id = date.time
        @ColumnInfo(name = "name")
        val name = date.toString().dropLast(17)
        @ColumnInfo(name = "num_bullets")
        var numBullets = 0
}
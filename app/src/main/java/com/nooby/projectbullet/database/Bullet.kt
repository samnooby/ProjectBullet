package com.nooby.projectbullet.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.util.*

//BulletType is the different types of bullets the user can create
enum class BulletType {
    NOTE, INCOMPLETETASK, COMPLETETASK, EVENT
}

@Entity(tableName = "bullet_table")
data class Bullet (
        @PrimaryKey(autoGenerate = true)
        var bulledId: Long = 0,

        @ColumnInfo(name = "create_date")
        val createDate: Date = Calendar.getInstance().time,

        @ColumnInfo(name = "bullet_date")
        var bulletDate: Date = Calendar.getInstance().time,

        @ColumnInfo(name = "message")
        var message: String = "",

        @ColumnInfo(name = "bullet_icon")
        var BulletType: BulletType = com.nooby.projectbullet.database.BulletType.NOTE
)

//Allows for storage of complex data types by converting them to primary types
class Converters {
        //Converts a long to a date
        @TypeConverter
        fun fromTimeStamp(value: Long?): Date? {
                return value?.let { Date(it)}
        }
        //Converts day to long
        @TypeConverter
        fun toTimeStamp(date: Date?): Long? {
                return date?.time?.toLong()
        }

        //Converts bullet type to int
        @TypeConverter
        fun typeToInt(bulletType: BulletType?): Int? {
                return when(bulletType){
                        BulletType.NOTE -> 0
                        BulletType.INCOMPLETETASK -> 1
                        BulletType.COMPLETETASK -> 2
                        BulletType.EVENT -> 3
                        else -> null
                }
        }
        //Converts int to bullet type
        @TypeConverter
        fun intToType(value: Int?): BulletType? {
                return when(value){
                        0 -> BulletType.NOTE
                        1 -> BulletType.INCOMPLETETASK
                        2 -> BulletType.COMPLETETASK
                        3 -> BulletType.EVENT
                        else -> null
                }
        }
}
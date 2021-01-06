package com.nooby.projectbullet.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

//BulletType is the different types of bullets the user can create
enum class BulletType {
    NOTE, INCOMPLETETASK, COMPLETETASK, EVENT
}

@Entity(tableName = "bullet_table")
data class Bullet(
        @PrimaryKey(autoGenerate = true)
        var bulletId: Long = 0,

        @ColumnInfo(name = "create_date")
        val createDate: LocalDateTime = LocalDateTime.now(),

        @ColumnInfo(name = "bullet_date")
        var bulletDate: LocalDateTime = LocalDateTime.now(),

        @ColumnInfo(name = "message")
        var message: String = "",

        @ColumnInfo(name = "bullet_icon")
        var bulletType: BulletType = com.nooby.projectbullet.database.BulletType.NOTE
)

//Allows for storage of complex data types by converting them to primary types
class Converters {
        private val formatter = DateTimeFormatter.ofPattern("EEEE LLLL dd, yyyy HH:mm:ss")
        //Converts a long to a date
        @TypeConverter
        fun fromTimeStamp(value: String?): LocalDateTime? {
                return value?.let { LocalDateTime.parse(value, formatter) }
        }
        //Converts day to long
        @TypeConverter
        fun toTimeStamp(date: LocalDateTime?): String? {
                return date?.format(formatter)
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
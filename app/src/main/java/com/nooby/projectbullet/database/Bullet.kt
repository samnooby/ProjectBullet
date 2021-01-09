package com.nooby.projectbullet.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.beust.klaxon.Klaxon
import java.time.LocalDate
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
        var bulletType: BulletType = BulletType.NOTE,

        @ColumnInfo(name = "bullet_notes")
        var bulletNotes: List<String> = listOf()
)

//Allows for storage of complex data types by converting them to primary types
class Converters {
        private val formatter = DateTimeFormatter.ofPattern("EEEE LLLL dd, yyyy HH:mm:ss")
        //Converts a long to a datetime
        @TypeConverter
        fun fromTimeStamp(value: String?): LocalDateTime? {
                return value?.let { LocalDateTime.parse(value, formatter) }
        }
        //Converts daytime to long
        @TypeConverter
        fun toTimeStamp(date: LocalDateTime?): String? {
                return date?.format(formatter)
        }

        //Converts bullet type to int
        @TypeConverter
        fun typeToInt(bulletType: BulletType?): Int? {
                return when(bulletType){
                        BulletType.EVENT -> 0
                        BulletType.INCOMPLETETASK -> 1
                        BulletType.NOTE -> 2
                        BulletType.COMPLETETASK -> 3
                        else -> null
                }
        }
        //Converts int to bullet type
        @TypeConverter
        fun intToType(value: Int?): BulletType? {
                return when(value){
                        0 -> BulletType.EVENT
                        1 -> BulletType.INCOMPLETETASK
                        2 -> BulletType.NOTE
                        3 -> BulletType.COMPLETETASK
                        else -> null
                }
        }

        //Converts list of notes to string
        @TypeConverter
        fun listToString(value: List<String>): String {
                return Klaxon().toJsonString(value)
        }

        //Converts string to list of notes
        @TypeConverter
        fun stringToList(value: String): List<String>? {
                return Klaxon().parseArray(value)
        }

        //Converts a list of long to string
        @TypeConverter
        fun idListToString(value: List<Long>): String {
                return Klaxon().toJsonString(value)
        }

        //Converts string to list of notes
        @TypeConverter
        fun stringToIdList(value: String): List<Long>? {
                return Klaxon().parseArray(value)
        }
}
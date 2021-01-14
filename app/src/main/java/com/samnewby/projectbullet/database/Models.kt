package com.samnewby.projectbullet.database

import androidx.room.*
import com.beust.klaxon.Klaxon
import java.time.LocalDate
import java.time.format.DateTimeFormatter

enum class BulletType {
    EVENT, INCOMPLETE_TASK, NOTE, COMPLETE_TASK
}

//The entity columns holding the models and the bullet tag relationships
@Entity(tableName = "bullets")
data class Bullet(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "bullet_id")
    val bulletId: Long,
    var title: String,
    @ColumnInfo(name = "create_date")
    val createDate: LocalDate,
    @ColumnInfo(name = "bullet_type")
    var bulletType: BulletType,
    var notes: List<String>,
    var day: LocalDate
)

@Entity(tableName = "days")
data class Day(
    @PrimaryKey()
    val date: LocalDate,
    val name: String
)

@Entity(tableName = "tags")
data class Tag(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "tag_id")
    val tagId: Long,
    var name: String,
    var color: Long
)

@Entity(primaryKeys = ["tag_id", "bullet_id"])
data class BulletTagCrossRef(
    @ColumnInfo(name = "tag_id")
    val tagId: Long,
    @ColumnInfo(name = "bullet_id")
    val bulletId: Long
)

//The classes retrieved from the database defining the relationships
data class TagWithBullets(
    @Embedded val tag: Tag,
    @Relation(
        parentColumn = "tag_id",
        entityColumn = "bullet_id",
        associateBy = Junction(BulletTagCrossRef::class)
    )
    val bullets: List<Bullet>
)

data class BulletWithTags(
    @Embedded val bullet: Bullet,
    @Relation(
        parentColumn = "bullet_id",
        entityColumn = "tag_id",
        associateBy = Junction(BulletTagCrossRef::class)
    )
    val tags: List<Tag>
)

data class DayWithBulletsAndTags(
    @Embedded val day: Day,
    @Relation(
        entity = Bullet::class,
        parentColumn = "date",
        entityColumn = "day"
    )
    var bullets: List<BulletWithTags>
)

class Converters {
    private val dateFormatter = DateTimeFormatter.ofPattern("EEEE LLLL dd, yyyy")

    //Date to string converters
    @TypeConverter
    fun localDateToString(date: LocalDate): String {
        return date.format(dateFormatter)
    }

    @TypeConverter
    fun stringToLocalDate(strDate: String): LocalDate {
        return LocalDate.parse(strDate, dateFormatter)
    }

    //BulletType to int converter
    @TypeConverter
    fun bulletTypeToInt(type: BulletType): Int {
        return when (type) {
            BulletType.EVENT -> 0
            BulletType.INCOMPLETE_TASK -> 1
            BulletType.NOTE -> 2
            BulletType.COMPLETE_TASK -> 3
        }
    }

    @TypeConverter
    fun intToBulletType(intType: Int): BulletType {
        return when (intType) {
            0 -> BulletType.EVENT
            1 -> BulletType.INCOMPLETE_TASK
            2 -> BulletType.NOTE
            else -> BulletType.COMPLETE_TASK
        }
    }

    //List of strings to string
    @TypeConverter
    fun listToString(list: List<String>): String {
        return Klaxon().toJsonString(list)
    }

    @TypeConverter
    fun stringToList(strList: String): List<String>? {
        return Klaxon().parseArray(strList)
    }
}
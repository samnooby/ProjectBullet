package ca.newbys.bullet.database

import android.content.Context
import androidx.room.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

//The database class which gives access to the actual room database
@Database(entities = [Bullet::class, Day::class], version = 0, exportSchema = false)
@TypeConverters(Converters::class)
abstract class BulletDatabase: RoomDatabase() {

    abstract val bulletDao: BulletDao

    //Gives access to the database without creating an instance of the class
    companion object {

        //Creates an instance of the object, volatile ensures only one instance is made
        @Volatile
        private var INSTANCE: BulletDatabase? = null

        fun getInstance(context: Context): BulletDatabase {
            //Get the current instance or create a new one if it does not exist
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    //Use the database builder to create a new instance
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        BulletDatabase::class.java,
                        "bullet_database"
                    ).fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}

//The converters that convert complex data types to simple data types to allow for storage in the database
class Converters {
    private val formatter = DateTimeFormatter.ofPattern("EEEE LLLL dd, yyyy HH:mm:ss")

    //Converters between long and datetime
    @TypeConverter
    fun fromTimeStamp(value: String): LocalDateTime {
        return LocalDateTime.parse(value, formatter)
    }

    @TypeConverter
    fun toTimeStamp(date: LocalDateTime): String {
        return date.format(formatter)
    }

    //Converts bullet type to int
    @TypeConverter
    fun fromType(type: BulletType): Int {
        return when(type) {
            BulletType.NOTE -> 0
            BulletType.INCOMPLETE -> 1
            BulletType.COMPLETE -> 2
        }
    }

    @TypeConverter
    fun toType(value: Int): BulletType {
        return when(value) {
            0 -> BulletType.NOTE
            1 -> BulletType.INCOMPLETE
            else -> BulletType.COMPLETE
        }
    }

    //Converts between a list of strings and strings
    @TypeConverter
    fun toList(value: String): List<String> {
        return value.split(",").map { it.trim() }
    }

    @TypeConverter
    fun fromList(list: List<String>): String {
        return list.joinToString()
    }

    //Converts datetime to long and vice versa
    @TypeConverter
    fun fromDate(date: LocalDate): String {
        return date.format(formatter)
    }

    @TypeConverter
    fun toDate(value: String): LocalDate {
        return LocalDate.parse(value, formatter)
    }
}
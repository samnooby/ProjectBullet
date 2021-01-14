package com.samnewby.projectbullet.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Bullet::class, Day::class, Tag::class, BulletTagCrossRef::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class BulletsDatabase : RoomDatabase() {
    //The objects used to access the data
    abstract val bulletDao: BulletDao
    abstract val dayDao: DayDao
    abstract val tagDao: TagDao

    //Singleton instance
    private lateinit var INSTANCE: BulletsDatabase

    //Returns the singleton instance or create one if one does not exist
    fun getDatabase(context: Context): BulletsDatabase {
        synchronized(BulletsDatabase::class.java) {
            //If instance class is not initialized
            if (!::INSTANCE.isInitialized) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    BulletsDatabase::class.java,
                    "bullets"
                ).build()
            }
            return INSTANCE
        }
    }
}
package com.samnewby.projectbullet.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [DatabaseBullet::class, DatabaseDay::class, DatabaseTag::class, BulletTagCrossRef::class],
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

    //Returns the singleton instance or create one if one does not exist
    companion object {

        @Volatile
        private var INSTANCE: BulletsDatabase? = null

        fun getDatabase(context: Context): BulletsDatabase {
            synchronized(BulletsDatabase::class.java) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        BulletsDatabase::class.java,
                        "bullets_database"
                    ).fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
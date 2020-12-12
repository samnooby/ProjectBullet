package com.nooby.projectbullet.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Day::class], version = 1, exportSchema = false)
abstract class BulletDatabase : RoomDatabase() {

    abstract val dayDao: DayDao

    companion object {

        //Volatile is never cached and all read and writes are done to main memory to
        //make sure all threads update same object
        @Volatile
        private var INSTANCE: BulletDatabase? = null

        fun getInstance(context: Context) : BulletDatabase {
            //Confirms only one thread at a time enters this code
            synchronized(this) {
                var instance = INSTANCE

                //Checks to see if instance has been created before or if it is the first
                //thread accessing the database
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        BulletDatabase::class.java,
                        "bullet_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
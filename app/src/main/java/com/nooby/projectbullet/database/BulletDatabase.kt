package com.nooby.projectbullet.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

//The class for the database containing the bullets
@Database(entities = [Bullet::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class BulletDatabase : RoomDatabase() {

    abstract val bulletDatabaseDao: BulletDatabaseDao

    //Allows for retrieval of the database without creating the class
    companion object {

        //Initializes the instance
        @Volatile
        private var INSTANCE: BulletDatabase? = null

        fun getInstance(context: Context) : BulletDatabase {
            //Get the current instance or create a new one if none exist
            synchronized(this) {
                var instance = INSTANCE
                //If database instance doesn't exist create a new database with the builder
                if (instance == null) {
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
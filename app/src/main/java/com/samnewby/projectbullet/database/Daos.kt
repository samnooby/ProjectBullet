package com.samnewby.projectbullet.database

import androidx.lifecycle.LiveData
import androidx.room.*
import java.time.LocalDate

interface Daos

@Dao
interface DayDao: Daos {
    //DayDao is used to update the days table in the database

    //Get days between startDate and endDate
    @Transaction
    @Query("select * from days WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun getDays(startDate: LocalDate, endDate: LocalDate): List<DayWithBulletsAndTags>

    @Transaction
    @Query("select * from days where date = :date")
    fun getDay(date: LocalDate): DayWithBulletsAndTags

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDay(databaseDay: DatabaseDay)

    @Delete
    fun deleteDay(databaseDay: DatabaseDay)
}

@Dao
interface BulletDao: Daos {
    //BulletDao is used to update the bullets table in the database

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBullet(databaseBullet: DatabaseBullet)

    //Get the bullets for a certain day
    @Transaction
    @Query("select * from bullets WHERE day = :day")
    fun getBullets(day: LocalDate): List<BulletWithTags>

    @Update
    fun updateBullet(databaseBullet: DatabaseBullet)

    @Delete
    fun deleteBullet(databaseBullet: DatabaseBullet)
}

@Dao
interface TagDao: Daos {
    //TagDao is used to update the tags and tag relationship table in the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTag(databaseTag: DatabaseTag)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBulletTagRelation(crossRef: BulletTagCrossRef)

    @Transaction
    @Query("SELECT * FROM tags")
    fun getAllTags(): List<TagWithBullets>

    @Update
    fun updateTag(databaseTag: DatabaseTag)

    @Delete
    fun deleteBulletTagRelation(crossRef: BulletTagCrossRef)

    @Delete
    fun deleteTag(databaseTag: DatabaseTag)
}
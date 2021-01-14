package com.samnewby.projectbullet.database

import androidx.lifecycle.LiveData
import androidx.room.*
import java.time.LocalDate

@Dao
interface DayDao {
    //DayDao is used to update the days table in the database

    //Get days between startDate and endDate
    @Query("select * from days WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun getDays(startDate: LocalDate, endDate: LocalDate): LiveData<List<DayWithBulletsAndTags>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDay(day: Day)

    @Delete
    fun deleteDay(day: Day)
}

@Dao
interface BulletDao {
    //BulletDao is used to update the bullets table in the database

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBullet(bullet: Bullet)

    //Get the bullets for a certain day
    @Query("select * from bullets WHERE day = :day")
    fun getBullets(day: LocalDate): LiveData<List<BulletWithTags>>

    @Update
    fun updateBullet(bullet: Bullet)

    @Delete
    fun deleteBullet(bullet: Bullet)
}

@Dao
interface TagDao {
    //TagDao is used to update the tags and tag relationship table in the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTag(tag: Tag)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBulletTagRelation(crossRef: BulletTagCrossRef)

    @Query("SELECT * FROM tags")
    fun getAllTags(): LiveData<List<TagWithBullets>>

    @Update
    fun updateTag(tag: Tag)

    @Delete
    fun deleteBulletTagRelation(crossRef: BulletTagCrossRef)

    @Delete
    fun deleteTag(tag: Tag)
}
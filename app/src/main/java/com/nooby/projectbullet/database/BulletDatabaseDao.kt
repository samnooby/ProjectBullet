package com.nooby.projectbullet.database

import androidx.lifecycle.LiveData
import androidx.room.*
import java.time.LocalDateTime
import java.util.*

@Dao
interface BulletDatabaseDao {

    @Insert
    fun insert(bullet: Bullet)

    @Insert
    fun insertAll(bulletList: List<Bullet>) : List<Long>

    @Update
    fun update(bullet: Bullet)

    @Query("SELECT * FROM bullet_table WHERE bullet_date BETWEEN :startDay AND :endDay ORDER BY bullet_icon ASC")
    fun get(startDay: LocalDateTime, endDay: LocalDateTime): List<Bullet>

    @Query("SELECT * FROM bullet_table WHERE bullet_icon = :type AND bullet_date BETWEEN :startDay AND :endDay")
    fun getByType(startDay: LocalDateTime, endDay: LocalDateTime, type: BulletType): List<Bullet>

    @Delete
    fun deleteBullet(bullet: Bullet)

    @Query("SELECT * FROM bullet_table")
    fun getAllBullets() : List<Bullet>

    @Query("DELETE FROM bullet_table")
    fun clear()
}
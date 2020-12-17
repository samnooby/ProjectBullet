package com.nooby.projectbullet.database

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.*

@Dao
interface BulletDatabaseDao {

    @Insert
    fun insert(bullet: Bullet)

    @Insert
    fun insertAll(bulletList: List<Bullet>) : List<Long>

    @Update
    fun update(bullet: Bullet)

    @Query("SELECT * FROM bullet_table WHERE bullet_date BETWEEN :startDay AND :endDay")
    fun get(startDay: Date, endDay: Date): List<Bullet>

    @Delete
    fun deleteBullet(bullet: Bullet)

    @Query("SELECT * FROM bullet_table")
    fun getAllBullets() : List<Bullet>
}
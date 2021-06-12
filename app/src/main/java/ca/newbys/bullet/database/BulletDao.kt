package ca.newbys.bullet.database

import androidx.room.*
import java.time.LocalDateTime

@Dao
interface BulletDao {
    @Insert
    fun insert(bullet: Bullet): Long

    @Update
    fun update(bullet: Bullet)

    @Delete
    fun delete(bullet: Bullet)

    @Transaction
    @Query("SELECT * FROM bullets WHERE bullet_date BETWEEN :start_day AND :end_day")
    fun getBullets(start_day: LocalDateTime, end_day: LocalDateTime): List<Bullet>

    @Query("SELECT * FROM bullets")
    fun getAllBullets(): List<Bullet>

    @Query("SELECT * FROM days")
    fun getAllDays(): List<Day>
}
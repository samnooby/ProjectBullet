package ca.newbys.bullet.database

import androidx.room.*
import java.time.LocalDate
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
    @Query("SELECT * FROM bullets WHERE bullet_date = :date")
    fun getBullets(date: LocalDate): List<Bullet>

    @Query("SELECT * FROM bullets")
    fun getAllBullets(): List<Bullet>

    @Query("SELECT * FROM days")
    fun getAllDays(): List<Day>
}
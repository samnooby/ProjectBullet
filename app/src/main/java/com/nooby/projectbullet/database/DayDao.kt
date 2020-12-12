package com.nooby.projectbullet.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DayDao {

    @Insert
    fun insert(day: Day)

    @Update
    fun update(day: Day)

    @Query("SELECT * from days_table WHERE Id = :key")
    fun get(key: Long): Day

    @Query("SELECT * FROM days_table ORDER BY Id Desc")
    fun getAllDays(): LiveData<List<Day>>

    @Delete
    fun delete(day: Day)

    @Query("DELETE FROM days_table")
    fun clear()

}
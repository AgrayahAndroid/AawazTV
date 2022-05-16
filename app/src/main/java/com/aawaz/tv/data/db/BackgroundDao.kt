package com.aawaz.tv.data.db

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * Created by Rinav Gangar <rinav.dev> on 17/5/20.
 * Agrahyah Technologies Pvt Ltd
 * rinav4all@gmail.com
 *
 * Data access object for the [Background] class
 **/

@Dao
interface BackgroundDao {

    @Query("SELECT * FROM background")
    suspend fun findAll(): List<Background>

    @Query("SELECT * FROM background")
    fun findAlll(): LiveData<List<Background>>

    @Query("SELECT * FROM background WHERE id IN (SELECT id FROM background ORDER BY RANDOM() LIMIT 1)")
    fun random(): LiveData<Background>

    @Query("SELECT * FROM background WHERE id IN (SELECT id FROM background ORDER BY RANDOM() LIMIT 1)")
    suspend fun randomBackground(): Background

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg item: Background)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(items: List<Background>)

    @Update
    fun update(item: Background)

    @Delete
    fun delete(item: Background)

    @Query("DELETE FROM background")
    fun truncate()
}
package com.aawaz.tv.data.db

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * Created by Rinav Gangar <rinav.dev> on 16/5/20.
 * Agrahyah Technologies Pvt Ltd
 * rinav4all@gmail.com
 */

@Dao
interface CategoryDao {

    @Query("SELECT * FROM category")
    fun findAll(): List<Category>

    @Query("SELECT * FROM category")
    fun findAlll(): LiveData<List<Category>>

    @Query("SELECT * FROM category where id LIKE :id LIMIT 1")
    suspend fun findById(id: String): Category

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg item: Category)

    @Update
    fun update(item: Category)

    @Delete
    fun delete(item: Category)

    @Query("DELETE FROM category")
    fun truncate()
}


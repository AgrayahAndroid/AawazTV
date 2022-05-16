package com.aawaz.tv.data.db

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * Created by Rinav Gangar <rinav.dev> on 17/5/20.
 * Agrahyah Technologies Pvt Ltd
 * rinav4all@gmail.com
 */

@Dao
interface AlbumDao {

//    @Query("SELECT " +
//            "id as ${BaseColumns._ID}, " +
//            "id as ${SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID}, " +
//            "title as ${SearchManager.SUGGEST_COLUMN_TEXT_1}, " +
//            "description as ${SearchManager.SUGGEST_COLUMN_TEXT_2}, " +
//            "artUri as ${SearchManager.SUGGEST_COLUMN_RESULT_CARD_IMAGE}, " +
//            "year as ${SearchManager.SUGGEST_COLUMN_PRODUCTION_YEAR}, " +
//            "playbackDurationMillis as ${SearchManager.SUGGEST_COLUMN_DURATION} " +
//            "FROM tvmediametadata WHERE :title LIKE '%' || searchableTitle || '%'")
//    fun contentProviderQuery(title: String): Cursor?

    @Query("SELECT * FROM album")
    fun findAll(): List<Album>

    @Query("SELECT * FROM album")
    fun findAlll(): LiveData<List<Album>>

    @Query("SELECT * FROM album WHERE id = :id LIMIT 1")
    fun findById(id: String): Album?

    @Query("SELECT * FROM album WHERE categoryId = :categoryId")
    fun findByCategory(categoryId: String): List<Album>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg album: Album)

    @Update
    fun update(album: Album)

    @Delete
    fun delete(album: Album)

    @Query("DELETE FROM album")
    fun truncate()
}
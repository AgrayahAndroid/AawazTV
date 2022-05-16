package com.aawaz.tv.data.db

import android.net.Uri
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

/**
 * Created by Rinav Gangar <rinav.dev> on 30/4/20.
 * Agrahyah Technologies Pvt Ltd
 * rinav4all@gmail.com
 */

/** Custom converters used to store types not natively supported by Room in our database */
class TvMediaConverters {

    @TypeConverter
    fun uriToString(value: Uri?): String? = value?.toString()

    @TypeConverter
    fun stringToUri(value: String?): Uri? = value?.let { Uri.parse(it) }

//    @TypeConverter
//    fun stringToStringList(value: String?): List<String> = value?.let {
//        gson.fromJson<List<String>>(it, object : TypeToken<List<String>>() {}.type)
//    } ?: listOf()
//
//    @TypeConverter
//    fun stringListToString(value: List<String>?): String? = value?.let { gson.toJson(it) }
//
//    @TypeConverter
//    fun fromAlbumList(value: List<Album>): String {
//        val type = object : TypeToken<List<Album>>() {}.type
//        return gson.toJson(value, type)
//    }
//
//    @TypeConverter
//    fun toAlbumList(value: String): List<Album> {
//        val type = object : TypeToken<List<Album>>() {}.type
//        return gson.fromJson(value, type)
//    }
}

/** Room database implementation */
@TypeConverters(TvMediaConverters::class)
@Database(
    version = 2, entities = [
        Background::class,
        Category::class,
        Album::class
    ],
    exportSchema = false
)
abstract class TvDatabase : RoomDatabase() {

    abstract fun backgrounds(): BackgroundDao
    abstract fun categories(): CategoryDao
    abstract fun albums(): AlbumDao
}

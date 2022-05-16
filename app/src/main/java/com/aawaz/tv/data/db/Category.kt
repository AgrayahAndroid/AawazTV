package com.aawaz.tv.data.db

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * Created by Rinav Gangar <rinav.dev> on 17/5/20.
 * Agrahyah Technologies Pvt Ltd
 * rinav4all@gmail.com
 */
@Entity(tableName = "category")
@Parcelize
data class Category(

    @PrimaryKey(autoGenerate = false)
    val id: String,
    val title: String,
    val image: String? = null,
    val url: String,
    val description: String? = null,
    //val albums: List<Album>,
    val language: String? = null

) : Parcelable
package com.aawaz.tv.data.db

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * Created by Rinav Gangar <rinav.dev> on 14/5/20.
 * Agrahyah Technologies Pvt Ltd
 * rinav4all@gmail.com
 */
@Parcelize
@Entity(
    tableName = "Episode",
    primaryKeys = ["album_id", "t_id"]
)
data class Episode(

    @PrimaryKey
    val tId: String,
    var albumId: String,
    val tDescription: String = "",
    val tArt: String? = null,
    val tLength: String? = null,
    val tLastPlayTime: String? = null,
    val tNo: Int? = null,
    val tUrl: String? = null,
    val tArtists: String? = null,
    val tTitle: String? = null
) : Parcelable
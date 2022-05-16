package com.aawaz.tv.data.remote

import android.os.Parcelable
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

/**
 * Created by Rinav Gangar <rinav.dev> on 17/5/20.
 * Agrahyah Technologies Pvt Ltd
 * rinav4all@gmail.com
 */
@Parcelize
@JsonClass(generateAdapter = true)
data class NetworkEpisode(

    @Transient
    @field:Json(name = "collectionId")
    var collectionId: String? = null,

    @PrimaryKey
    @field:Json(name = "t_id")
    val tId: String,

    @field:Json(name = "t_description")
    val tDescription: String = "",

    @field:Json(name = "t_art")
    val tArt: String? = null,

    @field:Json(name = "t_length")
    val tLength: String? = null,

    @field:Json(name = "t_last_play_time")
    val tLastPlayTime: String? = null,

    @field:Json(name = "t_no")
    val tNo: Int? = null,

    @field:Json(name = "t_url")
    val tUrl: String? = null,

    @field:Json(name = "t_artists")
    val tArtists: String? = null,

    @field:Json(name = "t_title")
    val tTitle: String? = null
) : Parcelable
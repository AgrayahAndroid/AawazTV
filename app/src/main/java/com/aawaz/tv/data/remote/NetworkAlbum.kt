package com.aawaz.tv.data.remote

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

/**
 * Created by Rinav Gangar <rinav.dev> on 16/5/20.
 * Agrahyah Technologies Pvt Ltd
 * rinav4all@gmail.com
 */
@Parcelize
@JsonClass(generateAdapter = true)
data class NetworkAlbum(

    @field:Json(name = "id")
    val id: String,

    @field:Json(name = "title")
    val title: String,

    @field:Json(name = "art")
    val art: String? = null,

    @field:Json(name = "description")
    val description: String? = null,

    @field:Json(name = "url")
    val url: String,

    @field:Json(name = "pubDate")
    val pubDate: String? = null,

    @field:Json(name = "lang")
    val lang: String? = "",

    @field:Json(name = "tracks")
    val tracks: List<NetworkEpisode> = listOf()
) : Parcelable
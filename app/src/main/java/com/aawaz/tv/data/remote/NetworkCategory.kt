package com.aawaz.tv.data.remote

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class NetworkCategory(

    @field:Json(name = "id")
    val id: String?,

    @field:Json(name = "title")
    val title: String?,

    @field:Json(name = "image")
    val image: String? = null,

    @field:Json(name = "url")
    val url: String?,

    @field:Json(name = "description")
    val description: String? = null,

    @field:Json(name = "album")
    val albums: List<NetworkAlbum>?,

    @field:Json(name = "lang")
    val language: String? = null

) : Parcelable
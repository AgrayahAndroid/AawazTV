package com.aawaz.tv.data.remote

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class AlbumDetailsResponse(

    @field:Json(name = "album")
    val album: NetworkAlbum

) : Parcelable

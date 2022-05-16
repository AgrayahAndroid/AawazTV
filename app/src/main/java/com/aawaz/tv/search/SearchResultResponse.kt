package com.aawaz.tv.search

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class NetworkSearchResult1(

    @field:Json(name = "id") val id: String,
    @field:Json(name = "shortUrl") val shortUrl: String,
    @field:Json(name = "type") val type: String,
    @field:Json(name = "featuredImageUrl") val featuredImageUrl: String,
    @field:Json(name = "title") val title: String,
    @field:Json(name = "language") val language: String,
    @field:Json(name = "uniqueSlug") val uniqueSlug: String,
    @field:Json(name = "description") val description: String,
    @field:Json(name = "publishedOn") val publishedOn: String,


) : Parcelable
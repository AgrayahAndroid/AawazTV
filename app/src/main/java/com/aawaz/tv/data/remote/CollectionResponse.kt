package com.aawaz.tv.data.remote

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class CollectionResponse(

    @field:Json(name = "category")
    val networkCategory: List<NetworkCategory> = listOf(),

    @field:Json(name = "backgrounds")
    val backgrounds: List<String> = listOf()

) : Parcelable
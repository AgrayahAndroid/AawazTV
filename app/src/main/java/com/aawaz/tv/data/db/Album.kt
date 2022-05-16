package com.aawaz.tv.data.db

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(
    tableName = "album",
    primaryKeys = ["id", "categoryId"]
)
data class Album(
    val id: String,
    val categoryId: String,
    val categoryName: String,
    val title: String,
    val art: String? = null,
    val description: String? = null,
    val url: String,
    val pubDate: String? = null,
    val lang: String = ""

) : Parcelable {

//    @Ignore
//    var tracks: List<Episode> = listOf()
}
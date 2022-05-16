package com.aawaz.tv.data.db

import android.net.Uri
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * Created by Rinav Gangar <rinav.dev> on 17/5/20.
 * Agrahyah Technologies Pvt Ltd
 * rinav4all@gmail.com
 *
 * Data class representing a background image displayed in the browsing page
 */
@Parcelize
@Entity(tableName = "background")
data class Background(
    /** User-provided identifier. Do not set autoGenerate to true*/
    @PrimaryKey
    var id: Int,

    /** URI for the image */
    val uri: Uri
) : Parcelable
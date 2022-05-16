package com.aawaz.tv.analytics

import android.os.Bundle
import com.aawaz.tv.data.db.Album

/**
 * Created by Rinav Gangar <rinav.dev> on 12/6/20.
 * Agrahyah Technologies Pvt Ltd
 * rinav4all@gmail.com
 */
object AlbumEvent {

    fun clicked(origin: String, album: Album): Event {

        val bundle = Bundle().apply {

            putString(Analytics.KEY_ALBUM_ID, album.id)
            putString(Analytics.KEY_ALBUM_TITLE, album.title)
            putString(Analytics.KEY_CATEGORY_ID, album.categoryId)
            putString(Analytics.KEY_EVENT_Source, origin)
        }

        return Event(Analytics.EVENT_VIEW_ALBUM, bundle)
    }
}
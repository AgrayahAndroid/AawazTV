package com.aawaz.tv.analytics

import androidx.annotation.NonNull
import com.aawaz.tv.BuildConfig
import com.google.firebase.analytics.FirebaseAnalytics

/**
 * Created by Rinav Gangar <rinav.dev> on 12/6/20.
 * Agrahyah Technologies Pvt Ltd
 * rinav4all@gmail.com
 */
class Analytics(private val analytics: FirebaseAnalytics) {

    fun updateUserProperty(@NonNull userId: String, @NonNull isAnonymous: Boolean) {

        analytics.apply {
            setUserId(userId)
            setUserProperty(KEY_FBASE_IS_ANONYMOUS, isAnonymous.toString())
        }
    }

    fun logEvent(@NonNull event: Event) {

        if (!BuildConfig.DEBUG || !BuildConfig.FLAVOR.contains("dev", true)) {
            analytics.logEvent(event.name, event.bundle)
        }
    }

    companion object {

        const val EVENT_VIEW_ALBUM = "view_album"
        const val EVENT_SIGN_UP = "sign_up"
        const val EVENT_LOGIN = "login"

        const val EVENT_PLAY = "play"
        const val EVENT_PAUSE = "pause"
        const val EVENT_STOP = "stop"
        const val EVENT_NEXT = "next"
        const val EVENT_PREVIOUS = "previous"
        const val EVENT_FAST_FORWARD = "fast_forward"
        const val EVENT_REWIND = "rewind"

        const val KEY_TIMESTAMP = "event_timestamp"
        const val KEY_VERSION_NAME = "version_name"
        const val KEY_VERSION_CODE = "version_code"
        const val KEY_BUILD_TYPE = "build_type"
        const val KEY_FLAVOUR = "flavour"
        const val KEY_FLAVOUR_MARKET_AND_ENV = "market_env"
        const val KEY_FBASE_USER_ID = "fbase_user_id"
        const val KEY_FBASE_IS_ANONYMOUS = "fbase_is_anonymous"
        const val KEY_EVENT_ORIGIN = "android_tv"
        const val KEY_EVENT_Source = "event_source"

        const val KEY_STATUS = "status"
        const val KEY_MESSAGE = "message"

        const val KEY_ALBUM_ID = "album_id"
        const val KEY_ALBUM_TITLE = "album_title"
        const val KEY_CATEGORY_ID = "album_category_id"

        const val KEY_EPISODE_ID = "episode_id"
        const val KEY_EPISODE_NAME = "episode_name"
        const val KEY_ARTIST = "artist"

        const val KEY_PLAYBACK_STATE = "playback_state"
        const val KEY_DURATION = "duration"
        const val KEY_CONTENT_DURATION = "content_duration"
        const val KEY_BUFFERED_DURATION = "buffered_duration"
        const val KEY_IS_LOADING = "is_loading"
        const val KEY_IS_PLAYING = "is_playing"
    }
}
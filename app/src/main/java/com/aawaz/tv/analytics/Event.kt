package com.aawaz.tv.analytics

import android.os.Bundle
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.aawaz.tv.BuildConfig
import com.aawaz.tv.utils.Utils
import com.google.firebase.analytics.FirebaseAnalytics

/**
 * Created by Rinav Gangar <rinav.dev> on 12/6/20.
 * Agrahyah Technologies Pvt Ltd
 * rinav4all@gmail.com
 */
class Event(
    @param:NonNull
    @field:NonNull
    val name: String,

    @param:Nullable
    @field:Nullable
    val bundle: Bundle
) {

    init {
        bundle.apply {
            putString(Analytics.KEY_TIMESTAMP, Utils.getCurrentTimeStamp())
            putString(FirebaseAnalytics.Param.ORIGIN, Analytics.KEY_EVENT_ORIGIN)
            putString(Analytics.KEY_VERSION_NAME, BuildConfig.VERSION_NAME)
            putInt(Analytics.KEY_VERSION_CODE, BuildConfig.VERSION_CODE)
            putString(Analytics.KEY_BUILD_TYPE, BuildConfig.BUILD_TYPE)
            putString(Analytics.KEY_FLAVOUR, BuildConfig.FLAVOR)
            putString(
                Analytics.KEY_FLAVOUR_MARKET_AND_ENV,
                "${BuildConfig.FLAVOR_market}-${BuildConfig.FLAVOR_environment}"
            )
        }
    }
}
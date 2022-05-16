package com.aawaz.tv.analytics

import android.os.Bundle

/**
 * Created by Rinav Gangar <rinav.dev> on 12/6/20.
 * Agrahyah Technologies Pvt Ltd
 * rinav4all@gmail.com
 */
object AuthEvent {

    fun signUp(
        origin: String,
        status: String,
        uid: String,
        isAnonymous: Boolean,
        message: String
    ): Event {

        val bundle = Bundle().apply {

            putString(Analytics.KEY_STATUS, status)
            putString(Analytics.KEY_FBASE_USER_ID, uid)
            putBoolean(Analytics.KEY_FBASE_IS_ANONYMOUS, isAnonymous)
            putString(Analytics.KEY_MESSAGE, message)
            putString(Analytics.KEY_EVENT_Source, origin)
        }

        return Event(Analytics.EVENT_SIGN_UP, bundle)
    }

    fun login(origin: String, uid: String, isAnonymous: Boolean): Event {

        val bundle = Bundle().apply {

            putString(Analytics.KEY_FBASE_USER_ID, uid)
            putBoolean(Analytics.KEY_FBASE_IS_ANONYMOUS, isAnonymous)
            putString(Analytics.KEY_EVENT_Source, origin)
        }

        return Event(Analytics.EVENT_LOGIN, bundle)
    }
}
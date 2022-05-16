package com.aawaz.tv.app

import android.app.Application
import coil.Coil
import coil.ImageLoader
import com.aawaz.tv.BuildConfig
import com.aawaz.tv.analytics.Analytics
import com.aawaz.tv.di.*
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

/**
 * Created by Rinav Gangar <rinav.dev> on 30/4/20.
 * Agrahyah Technologies Pvt Ltd
 * rinav4all@gmail.com
 */
class AawazTVApp : Application() {

    private val imageLoader: ImageLoader by inject<ImageLoader>()

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG || BuildConfig.FLAVOR.contains("dev", true)) {
            Timber.plant(Timber.DebugTree())
        }

        FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(true)
        setupCrashlytics()
        setupAnalytics()
        setupDI()

        Coil.setImageLoader(imageLoader)
    }

    private fun setupCrashlytics() {

        FirebaseCrashlytics.getInstance().apply {
            setCrashlyticsCollectionEnabled(true)

            setCustomKey(Analytics.KEY_VERSION_NAME, BuildConfig.VERSION_NAME)
            setCustomKey(Analytics.KEY_BUILD_TYPE, BuildConfig.BUILD_TYPE)
            setCustomKey(Analytics.KEY_FLAVOUR, BuildConfig.FLAVOR)
            setCustomKey(
                Analytics.KEY_FLAVOUR_MARKET_AND_ENV,
                "${BuildConfig.FLAVOR_market}-${BuildConfig.FLAVOR_environment}"
            )

//            Timber.d("${Analytics.KEY_VERSION_NAME} ## ${BuildConfig.VERSION_NAME}")
//            Timber.d("APPLICATION_ID ## ${BuildConfig.APPLICATION_ID}")
//            Timber.d("${Analytics.KEY_BUILD_TYPE} ## ${BuildConfig.BUILD_TYPE}")
//            Timber.d("${Analytics.KEY_FLAVOUR} ## ${BuildConfig.FLAVOR}")
//            Timber.d("${Analytics.KEY_FLAVOUR_MARKET_AND_ENV} ## ${BuildConfig.FLAVOR_market}-${BuildConfig.FLAVOR_environment}")
        }
    }

    private fun setupAnalytics() {

        Firebase.analytics.apply {

            setUserProperty(Analytics.KEY_VERSION_NAME, BuildConfig.VERSION_NAME)
            setUserProperty(Analytics.KEY_BUILD_TYPE, BuildConfig.BUILD_TYPE)
            setUserProperty(Analytics.KEY_FLAVOUR, BuildConfig.FLAVOR)
            setUserProperty(
                Analytics.KEY_FLAVOUR_MARKET_AND_ENV,
                "${BuildConfig.FLAVOR_market}-${BuildConfig.FLAVOR_environment}"
            )

            setAnalyticsCollectionEnabled(true)
        }
    }

    private fun setupDI() {

        startKoin {

            if (BuildConfig.DEBUG) {
                //androidLogger()
            }

            androidContext(this@AawazTVApp)

            modules(
                listOf(
                    netModule,
                    apiModule,
                    databaseModule,
                    appModule,
                    collectionModule,
                    playerViewModule,
                    albumDetailsModule,
                    SearchModule
                )
            )
        }
    }
}
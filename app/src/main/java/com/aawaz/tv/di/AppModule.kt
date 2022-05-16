package com.aawaz.tv.di

import android.content.Context
import android.util.Log
import coil.ImageLoader
import coil.request.CachePolicy
import coil.size.Precision
import coil.util.DebugLogger
import com.aawaz.tv.BuildConfig
import com.aawaz.tv.analytics.Analytics
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Created by Rinav Gangar <rinav.dev> on 12/6/20.
 * Agrahyah Technologies Pvt Ltd
 * rinav4all@gmail.com
 */

val appModule = module {

    fun provideAnalytics(firebaseAnalytics: FirebaseAnalytics): Analytics {
        return Analytics(firebaseAnalytics)
    }

    fun provideImageLoader(context: Context): ImageLoader {

        /**
         * Setting availableMemoryPercentage to 0.30 and
         * bitmapPoolPercentage to 0.6 allows this ImageLoader to use
         * 60% of the app's total memory and splits that
         * memory 50/50 between the bitmap pool and memory cache.
         */
        return ImageLoader.Builder(context.applicationContext)
            .allowHardware(false)
            .availableMemoryPercentage(0.30)
            .bitmapPoolPercentage(0.6)
            .crossfade(true)
            .allowHardware(false)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .networkCachePolicy(CachePolicy.ENABLED)
            .precision(Precision.AUTOMATIC)
            //.logger(DebugLogger(if (BuildConfig.DEBUG) Log.VERBOSE else Log.ERROR))
            .build()
    }

    single { provideAnalytics(FirebaseAnalytics.getInstance(androidContext())) }

    single { provideImageLoader(context = get()) }
}
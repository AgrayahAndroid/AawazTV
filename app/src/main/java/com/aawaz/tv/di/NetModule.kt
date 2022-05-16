package com.aawaz.tv.di

import com.aawaz.tv.BuildConfig
import com.skydoves.sandwich.DataSourceCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Created by Rinav Gangar <rinav.dev> on 25/5/20.
 * Agrahyah Technologies Pvt Ltd
 * rinav4all@gmail.com
 */

//https://preprod.aawaz.com/search/android/tv/
private const val BASE_URL = "https://prod.aawaz.com/"

val netModule = module {

    fun provideHttpClient(interceptor: HttpLoggingInterceptor): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .connectTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES)


        return okHttpClientBuilder.build()
    }

    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create().withNullSerialization())
            //.addConverterFactory(GsonConverterFactory.create(factory))
            .addCallAdapterFactory(DataSourceCallAdapterFactory())
            .client(client)
            .build()
    }

    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {

        val loggingInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {

            override fun log(message: String) {
                if (BuildConfig.DEBUG) Timber.d(message)
            }
        })

        loggingInterceptor.apply {
            level = if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.HEADERS
            else
                HttpLoggingInterceptor.Level.NONE

            redactHeader("Authorization")
            redactHeader("Cookie")
        }

        return loggingInterceptor
    }


    single { provideHttpClient(interceptor = get()) }
    single { provideRetrofit(get()) }
    single { provideHttpLoggingInterceptor() }
}

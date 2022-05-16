package com.aawaz.tv.di

import com.aawaz.tv.network.AawazApiService
import org.koin.dsl.module
import retrofit2.Retrofit

/**
 * Created by Rinav Gangar <rinav.dev> on 25/5/20.
 * Agrahyah Technologies Pvt Ltd
 * rinav4all@gmail.com
 */
val apiModule = module {

    fun provideApi(retrofit: Retrofit): AawazApiService {
        return retrofit.create(AawazApiService::class.java)
    }

    single { provideApi(retrofit = get()) }
}
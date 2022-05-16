package com.aawaz.tv.di

import android.app.Application
import androidx.room.Room
import com.aawaz.tv.data.db.AlbumDao
import com.aawaz.tv.data.db.BackgroundDao
import com.aawaz.tv.data.db.CategoryDao
import com.aawaz.tv.data.db.TvDatabase
import com.aawaz.tv.utils.C
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

/**
 * Created by Rinav Gangar <rinav.dev> on 25/5/20.
 * Agrahyah Technologies Pvt Ltd
 * rinav4all@gmail.com
 */
val databaseModule = module {

    fun provideDatabase(application: Application): TvDatabase {

        return Room.databaseBuilder(application, TvDatabase::class.java, C.DATABASE_NAME)
            .fallbackToDestructiveMigrationOnDowngrade()
            .fallbackToDestructiveMigration()
            .build()
    }


//    fun provideCollectionsDao(database: TvDatabase): TvMediaCollectionDAO {
//        return database.collection()
//    }

    fun provideBackgroundDao(database: TvDatabase): BackgroundDao {
        return database.backgrounds()
    }

    fun provideCategoryDao(database: TvDatabase): CategoryDao {
        return database.categories()
    }

    fun provideAlbumDao(database: TvDatabase): AlbumDao {
        return database.albums()
    }

    single { provideDatabase(androidApplication()) }
    // single { provideCollectionsDao(database = get()) }
    single { provideBackgroundDao(database = get()) }
    single { provideCategoryDao(database = get()) }
    single { provideAlbumDao(database = get()) }
}
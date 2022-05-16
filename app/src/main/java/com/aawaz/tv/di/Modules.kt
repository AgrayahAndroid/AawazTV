package com.aawaz.tv.di

import com.aawaz.tv.search.SearchViewModel
import com.aawaz.tv.data.db.AlbumDao
import com.aawaz.tv.data.db.BackgroundDao
import com.aawaz.tv.data.db.CategoryDao
import com.aawaz.tv.data.repo.CollectionRepository
import com.aawaz.tv.network.AawazApiService
import com.aawaz.tv.ui.browser.MediaBrowserViewModel
import com.aawaz.tv.ui.details.AlbumDetailsViewModel
import com.aawaz.tv.ui.player.ExoPlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Created by Rinav Gangar <rinav.dev> on 9/5/20.
 * Agrahyah Technologies Pvt Ltd
 * rinav4all@gmail.com
 */


val collectionModule = module {

    fun provideCollectionRepository(
        api: AawazApiService,
        categoryDao: CategoryDao,
        albumDao: AlbumDao,
        backgroundDao: BackgroundDao
    ) = CollectionRepository(api, categoryDao, albumDao, backgroundDao)

    single {
        provideCollectionRepository(
            api = get(),
            categoryDao = get(),
            albumDao = get(),
            backgroundDao = get()
        )
    }

    viewModel {
        MediaBrowserViewModel(
            repo = get(),
            database = get()
        )
    }
}

val playerViewModule = module {
    viewModel { ExoPlayerViewModel(api = get()) }
}

val albumDetailsModule = module {
    viewModel { AlbumDetailsViewModel(api = get()) }
}

val SearchModule = module {
    viewModel { SearchViewModel(api = get()) }
}

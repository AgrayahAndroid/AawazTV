package com.aawaz.tv.ui.browser

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.aawaz.tv.data.db.Album
import com.aawaz.tv.data.db.Background
import com.aawaz.tv.data.db.Category
import com.aawaz.tv.data.db.TvDatabase
import com.aawaz.tv.data.repo.CollectionRepository
import kotlinx.coroutines.launch

/**
 * Created by Rinav Gangar <rinav.dev> on 10/5/20.
 * Agrahyah Technologies Pvt Ltd
 * rinav4all@gmail.com
 */
class MediaBrowserViewModel(
    private val database: TvDatabase,
    private val repo: CollectionRepository
) : ViewModel() {

    private var categories: LiveData<List<Category>>
    private var albums: LiveData<List<Album>>

    init {
        fetchCollection()
        categories = database.categories().findAlll()
        albums = database.albums().findAlll()

    }
//    fun getCollection() = liveData(Dispatchers.IO) {
//
//        emit(repo.getCollection())
//    }

    fun getCategories() = categories

    fun getAlbums() = albums

    val randomBackground: LiveData<Background> = liveData {
        val data = database.backgrounds().randomBackground() // loadUser is a suspend function.
        emit(data)
    }


    suspend fun backgroundUrl() = database.backgrounds().findAll().shuffled().firstOrNull()?.uri

    private fun fetchCollection() {

        viewModelScope.launch {
            repo.fetchCollection()
        }
    }
}
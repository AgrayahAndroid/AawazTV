package com.aawaz.tv.data.repo

import android.net.Uri
import android.util.Log
import com.aawaz.tv.data.db.*
import com.aawaz.tv.data.mapper.toAlbum
import com.aawaz.tv.data.mapper.toCategory
import com.aawaz.tv.data.remote.CollectionResponse
import com.aawaz.tv.network.AawazApiService
import com.rollbar.android.Rollbar
import com.skydoves.sandwich.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Created by Rinav Gangar <rinav.dev> on 10/5/20.
 * Agrahyah Technologies Pvt Ltd
 * rinav4all@gmail.com
 */
class CollectionRepository(
    private val api: AawazApiService,
    private val categoryDao: CategoryDao,
    private val albumDao: AlbumDao,
    private val backgroundDao: BackgroundDao
) {
    suspend fun fetchCollection() {

        // Initializes an empty list to populate
        val categories: MutableList<Category> = mutableListOf()
        val albums: MutableList<Album> = mutableListOf()

        CoroutineScope(Dispatchers.IO).launch {

            api.getCollections()
                .retry(3, 5000L)
                .observeResponse(object : ResponseObserver<CollectionResponse> {

                    override fun observe(response: ApiResponse<CollectionResponse>) {
                        // handle the case when the API request gets a success response.

//                        Log.d("CheckResponse","CollectionRepositry  :     $response")
                        response.onSuccess {

                            //Timber.d("XXXXXXXXX: $data")
//                            Log.d("CheckResponse","CollectionRepositry  :     $data")
                            data?.let {

                                val bs = it.backgrounds?.orEmpty()
                                val backs = (bs.indices).map { idx ->
                                    Background(idx, Uri.parse(bs[idx]))
                                }
                                CoroutineScope(Dispatchers.IO).launch {
                                    if (!backs.isNullOrEmpty()) {
                                        // save the result in the local database
//                                        database.bbackgrounds().findAll()
//                                            .filter { !backs.contains(it) }
//                                            .forEach { database.bbackgrounds().delete(it) }

                                        backgroundDao.apply {
                                            findAll() // select all
                                                .filter { all -> !backs.contains(all) } // filter records not in list from api call
                                                .forEach { toDelete ->
                                                    delete(toDelete) // delete those rows
                                                }
                                            insert(*backs.toTypedArray()) // insert/replace
                                        }
                                    }
                                }

                                val nc = it.networkCategory?.orEmpty()
                                categories.addAll(nc.map { cats -> cats.toCategory() })

                                CoroutineScope(Dispatchers.IO).launch {
                                    if (!categories.isNullOrEmpty()) {
                                        // save the result in the local database
                                        categoryDao.apply {
                                            findAll()
                                                .filter { all -> !categories.contains(all) }
                                                .forEach { toDelete ->
                                                    delete(toDelete)
                                                }
                                            insert(*categories.toTypedArray())
                                        }
                                    }
                                }

                                nc.map { cats ->
                                    cats.albums?.map { al ->
                                        al.toAlbum(
                                            cats.id.orEmpty(),
                                            cats.title.orEmpty()
                                        )
                                    }
                                }

                                nc.forEach { cat ->
                                    val albumsx = cat.albums?.let { album ->
                                        album.map { al ->
                                            al.toAlbum(
                                                cat.id.orEmpty(),
                                                cat.title.orEmpty()
                                            )
                                        }
                                    }.orEmpty()
                                    albums.addAll(albumsx)
                                }

                                CoroutineScope(Dispatchers.IO).launch {
                                    if (!albums.isNullOrEmpty()) {
                                        // save the result in the local database
                                        albumDao.apply {
                                            findAll()
                                                .filter { all -> !albums.contains(all) }
                                                .forEach { toDelete ->
                                                    delete(toDelete)
                                                }
                                            insert(*albums.toTypedArray())
                                        }
                                    }
                                }
                            }

//                            CoroutineScope(Dispatchers.IO).launch {
//
//                                if (!backgrounds.isNullOrEmpty()) {
//                                    database.bbackgrounds().insert(*backgrounds.toTypedArray())
//                                }
//
//                                if (!categories.isNullOrEmpty()) {
//                                    database.categories().insert(*categories.toTypedArray())
//                                }
//
//                                if (!albums.isNullOrEmpty()) {
//                                    database.albums().insert(*albums.toTypedArray())
//                                }
//                            }
                        }

                        response.onError {
                            Rollbar.instance().error("$statusCode(${statusCode.code}): ${message()}")
                            Timber.d("XXXXXXXXX: err: $statusCode(${statusCode.code}): ${message()}")
                        }

                        // handle the case when the API request gets a exception response.
                        // e.g. network connection error.
                        response.onException {
                            Rollbar.instance().error("${message()}")
                        }
                    }
                })
                .request()
        }
    }
}

package com.aawaz.tv.workers

import android.content.Context
import android.net.Uri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.aawaz.tv.data.db.Album
import com.aawaz.tv.data.db.Background
import com.aawaz.tv.data.db.Category
import com.aawaz.tv.data.db.TvDatabase
import com.aawaz.tv.data.mapper.toAlbum
import com.aawaz.tv.data.mapper.toCategory
import com.aawaz.tv.data.remote.CollectionResponse
import com.aawaz.tv.network.AawazApiService
import com.rollbar.android.Rollbar
import com.skydoves.sandwich.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

import org.koin.java.KoinJavaComponent.inject
import timber.log.Timber

/**
 * Created by Rinav Gangar <rinav.dev> on 15/5/20.
 * Agrahyah Technologies Pvt Ltd
 * rinav4all@gmail.com
 */
class CollectionSynchronizer(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val api: AawazApiService by inject()

    override suspend fun doWork(): Result = try {
        synchronize(api)
        Result.success()
    } catch (exc: Exception) {
        Rollbar.instance().error(exc)
        Timber.e(exc, "WorkManager failed to synchronize CollectionSynchronizer")
        Result.failure()
    }

    companion object {

        private val database by inject<TvDatabase>(TvDatabase::class.java)

        private suspend fun parseMediaFeed(api: AawazApiService) {

            // Initializes an empty list to populate
            val categories: MutableList<Category> = mutableListOf()
            val albums: MutableList<Album> = mutableListOf()
            val backgrounds: MutableList<Background> = mutableListOf()

            val deferred = CoroutineScope(Dispatchers.IO).async {

                api.getCollections()
                    .retry(3, 5000L)
                    .observeResponse(object : ResponseObserver<CollectionResponse> {

                        override fun observe(response: ApiResponse<CollectionResponse>) {
                            // handle the case when the API request gets a success response.
                            response.onSuccess {

                                //Timber.d("XXXXXXXXX: $data")

                                data?.let {

                                    val bs = it.backgrounds?.orEmpty()
                                    val backs = (bs.indices).map { idx ->
                                        Background(idx, Uri.parse(bs[idx]))
                                    }

                                    backgrounds.clear()
                                    backgrounds.addAll(backs)

//                                    backgrounds.forEach {
//                                        Timber.d("XXXXXXXXXXb: ${it.id} - ${it.uri}")
//                                    }

                                    val nc = it.networkCategory?.orEmpty()
                                    categories.clear()
                                    categories.addAll(nc.map { cats -> cats.toCategory() })


                                    nc.map { cats ->
                                        cats.albums?.map { al ->
                                            al.toAlbum(
                                                cats.id.orEmpty(),
                                                cats.title.orEmpty()
                                            )
                                        }
                                    }

                                    albums.clear()
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
                                }

                                CoroutineScope(Dispatchers.IO).launch {

                                    if (!backgrounds.isNullOrEmpty()) {
                                        // database.backgrounds().insert(*backgrounds.toTypedArray())

                                        database.backgrounds().apply {
                                            findAll() // select all
                                                .filter { all -> !backgrounds.contains(all) } // filter records not in list from api call
                                                .forEach { toDelete ->
                                                    delete(toDelete) // delete those rows
                                                }
                                            insert(*backgrounds.toTypedArray()) // insert/replace
                                        }
                                    }

                                    if (!categories.isNullOrEmpty()) {
                                        //database.categories().insert(*categories.toTypedArray())
                                        database.categories().apply {
                                            findAll()
                                                .filter { all -> !categories.contains(all) }
                                                .forEach { toDelete ->
                                                    delete(toDelete)
                                                }
                                            insert(*categories.toTypedArray())
                                        }
                                    }

                                    if (!albums.isNullOrEmpty()) {
                                        //database.albums().insert(*albums.toTypedArray())
                                        database.albums().apply {
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

                            response.onError {
                                Rollbar.instance().error(" $statusCode(${statusCode.code}): ${message()}")
                                Timber.d("XXXXXXXXX: err: $statusCode(${statusCode.code}): ${message()}")
                            }

                            // handle the case when the API request gets a exception response.
                            // e.g. network connection error.
                            response.onException {
                                Timber.d("XXXXXXXXX: exec: ${message()}")
                            }
                        }
                    })
                    .request()
            }


            deferred.join()
            val await = deferred.await()

            //Timber.d("Returning XXXXXXXXXXb: size: ${backgrounds.size} - ${albums.size} - ${categories.size}")
        }

        @Synchronized
        fun synchronize(api: AawazApiService) {

            Timber.d("Starting synchronization work...")

            CoroutineScope(Dispatchers.IO).launch {
                parseMediaFeed(api)
            }
        }
    }
}

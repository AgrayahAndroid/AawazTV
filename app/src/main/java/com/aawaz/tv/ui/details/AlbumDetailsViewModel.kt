package com.aawaz.tv.ui.details

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aawaz.tv.data.ResultData
import com.aawaz.tv.data.remote.AlbumDetailsResponse
import com.aawaz.tv.network.AawazApiService
import com.rollbar.android.Rollbar
import com.skydoves.sandwich.*
import timber.log.Timber

/**
 * Created by Rinav Gangar <rinav.dev> on 21/6/20.
 * Agrahyah Technologies Pvt Ltd
 * rinav4all@gmail.com
 */
class AlbumDetailsViewModel constructor(private val api: AawazApiService) : ViewModel() {

    val album: MutableLiveData<ResultData<AlbumDetailsResponse?>> = MutableLiveData()

    fun getEpisodesByAlbumId(uid: String, id: String, lang: String) {

        album.postValue(ResultData.loading(null))

        api.getAlbumDetails( id, lang)
            .retry(2, 5000L)
            .observeResponse(object : ResponseObserver<AlbumDetailsResponse> {

                override fun observe(response: ApiResponse<AlbumDetailsResponse>) {
                    // handle the case when the API request gets a success response.
                    response.onSuccess {
                        //Timber.d("XXXXXXXXX: alDetails: success")
                        album.postValue(ResultData.success(this.data))
                    }

                    response.onError {
                        Rollbar.instance().error("XXXXXXXXX: alDetails: err: $statusCode(${statusCode.code}): ${message()}")
                        Timber.e("XXXXXXXXX: alDetails: err: $statusCode(${statusCode.code}): ${message()}")
                        //Timber.e("XXXXXXXXX: alDetails: err: ${this.raw}")
                        album.postValue(ResultData.failure("${statusCode.code}): ${message()}"))
                    }

                    // handle the case when the API request gets a exception response.
                    // e.g. network connection error.
                    response.onException {
                        Rollbar.instance().error("${this.exception}, XXXXXXXXX: alDetails: error ${message()}")
                        Timber.e(this.exception, "XXXXXXXXX: alDetails: error ${message()}")
                        album.postValue(ResultData.failure(this.message()))
                    }
                }
            }).request()
    }
}
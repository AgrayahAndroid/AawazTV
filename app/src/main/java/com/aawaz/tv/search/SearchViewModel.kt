package com.aawaz.tv.search

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aawaz.tv.data.ResultData
import com.aawaz.tv.network.AawazApiService
import com.rollbar.android.Rollbar
import com.skydoves.sandwich.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject


class SearchViewModel constructor(private val api: AawazApiService): ViewModel() {
    val album: MutableLiveData<ResultData<List<NetworkSearchResult1>?>> = MutableLiveData()


    fun getData2(search : String){
        album.postValue(ResultData.loading(null))
        val jsonObject = JSONObject()
        try {
            jsonObject.put("search", search)
            /*jsonObject.put("page", 1)
            jsonObject.put("page_size", 15)*/
        } catch (e: JSONException) {
            Rollbar.instance().error(e)
            e.printStackTrace()
        }
        val body = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        api.requestSearchResult(body)
            .retry(2, 5000L)
            .observeResponse(object : ResponseObserver<List<NetworkSearchResult1>> {

                override fun observe(response: ApiResponse<List<NetworkSearchResult1>>) {

//                    Log.d("CheckResponse","response  :     $response")
                    // handle the case when the API request gets a success response.
                    response.onSuccess {
//                        Log.d("CheckResponse","onSuccess  :     $data")
                        album.postValue(ResultData.success(this.data))
                    }

                    response.onError {
//                        Log.v("CheckResponse"," onError   ${message()}")
                        Rollbar.instance().error(message())
                        album.postValue(ResultData.failure("${statusCode.code}): ${message()}"))

                    }

                    // handle the case when the API request gets a exception response.
                    // e.g. network connection error.
                    response.onException {
                        Rollbar.instance().error(message())
//                        Log.v("CheckResponse"," onException   ${message()}")
                        album.postValue(ResultData.failure(": ${message()}"))
                    }
                }
            }).request()
    }
}
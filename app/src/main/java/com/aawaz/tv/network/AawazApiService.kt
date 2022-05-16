package com.aawaz.tv.network


import com.aawaz.tv.data.remote.AlbumDetailsResponse
import com.aawaz.tv.data.remote.CollectionResponse
import com.aawaz.tv.search.NetworkSearchResult1
import com.skydoves.sandwich.DataSource
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * Created by Rinav Gangar <rinav.dev> on 7/5/20.
 * Agrahyah Technologies Pvt Ltd
 * rinav4all@gmail.com
 */
interface AawazApiService {

//    https://prod.aawaz.com/web/tv/getCollection/

    @GET("web/tv/getCollection")
    fun getCollections(): DataSource<CollectionResponse>

    @GET("web/tv/getAlbumDetails?")
//    @FormUrlEncoded
    fun getAlbumDetails(
        /*@Field("uid") userId: String,*/
        @Query("albumId") articleId: String,
        @Query("lang") lang: String
    ): DataSource<AlbumDetailsResponse>


//    @GET("https://gist.githubusercontent.com/rinav/133b7729009765945191c6c3043d004d/raw/3c3ac3697ba0f0b052cf7af05a9a09f67e05c874/aawaz-album-details.json")
//    //@FormUrlEncoded
//    suspend fun getAlbumDetailsFake(): Response<AlbumDetailsResponse>


    @POST("search/android/tv/")
    fun requestSearchResult(@Body data: RequestBody):DataSource<List<NetworkSearchResult1>>
}
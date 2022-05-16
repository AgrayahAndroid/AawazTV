package com.aawaz.tv.data.mapper

import android.net.Uri
import com.aawaz.tv.data.db.Album
import com.aawaz.tv.data.db.Background
import com.aawaz.tv.data.db.Category
import com.aawaz.tv.data.db.Episode
import com.aawaz.tv.data.remote.NetworkAlbum
import com.aawaz.tv.data.remote.NetworkCategory
import com.aawaz.tv.data.remote.NetworkEpisode
import com.aawaz.tv.search.NetworkSearchResult1


/**
 * Created by Rinav Gangar <rinav.dev> on 26/5/20.
 * Agrahyah Technologies Pvt Ltd
 * rinav4all@gmail.com
 */

fun NetworkCategory.toCategory(): Category {

    return Category(
        this.id.orEmpty(),
        this.title.orEmpty(),
        this.image.orEmpty(),
        this.url.orEmpty(),
        this.description.orEmpty(),
        this.language.orEmpty()
    )
}

fun NetworkAlbum.toAlbum(categoryId: String, categoryName: String): Album {

    return Album(
        this.id.orEmpty(),
        categoryId,
        categoryName,
        this.title.orEmpty(),
        this.art.orEmpty(),
        this.description.orEmpty(),
        this.url.orEmpty(),
        this.pubDate.orEmpty(),
        this.lang.orEmpty()
        //this.tracks.orEmpty()
    )//.apply {
    //this.tracks.orEmpty()
    //}
}

fun NetworkEpisode.toEpisode(album: NetworkAlbum): Episode {

    return Episode(
        this.tId,
        album.id,
        this.tDescription,
        this.tArt,
        this.tLength,
        this.tLastPlayTime,
        this.tNo,
        this.tUrl,
        this.tArtists,
        this.tTitle
    )
}

fun getBackgroundsFromStrings(list: List<String>): List<Background> {

    return (list.indices).map { idx ->
        Background(idx, Uri.parse(list[idx]))
    }
}

//fun NetworkSearchResult1.toSearchEpisode(album: NetworkSearchResult1): SearchResult {
fun NetworkSearchResult1.toSearchEpisode( ): Album {

    return Album(
        this.id,
        "",
        "",
        this.title,
        this.featuredImageUrl,
        this.description,
        this.shortUrl,
        this.publishedOn,
        this.language,
    )
}
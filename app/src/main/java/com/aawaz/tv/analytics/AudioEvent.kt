package com.aawaz.tv.analytics

import android.os.Bundle
import com.aawaz.tv.data.db.Album
import com.aawaz.tv.data.db.Episode

/**
 * Created by Rinav Gangar <rinav.dev> on 12/6/20.
 * Agrahyah Technologies Pvt Ltd
 * rinav4all@gmail.com
 */
object AudioEvent {

    fun playClicked(
        origin: String,
        album: Album,
        episode: Episode,
        state: Int,
        duration: String,
        contentDuration: String,
        bufferedDuration: String,
        isLoading: Boolean,
        isPlaying: Boolean
    ): Event {

        val bundle = Bundle().apply {

            putString(Analytics.KEY_ALBUM_ID, album.id)
            putString(Analytics.KEY_ALBUM_TITLE, album.title)
            putString(Analytics.KEY_CATEGORY_ID, album.categoryId)
            putString(Analytics.KEY_EPISODE_ID, episode.tId)
            putString(Analytics.KEY_EPISODE_NAME, episode.tTitle)
            putString(Analytics.KEY_ARTIST, episode.tArtists)
            putString(Analytics.KEY_EVENT_Source, origin)
            putInt(Analytics.KEY_PLAYBACK_STATE, state)
            putString(Analytics.KEY_DURATION, duration)
            putString(Analytics.KEY_CONTENT_DURATION, contentDuration)
            putString(Analytics.KEY_BUFFERED_DURATION, bufferedDuration)
            putBoolean(Analytics.KEY_IS_LOADING, isLoading)
            putBoolean(Analytics.KEY_IS_PLAYING, isPlaying)
        }

        return Event(Analytics.EVENT_PLAY, bundle)
    }

    fun pauseClicked(
        origin: String,
        album: Album,
        episode: Episode,
        state: Int,
        duration: String,
        contentDuration: String,
        bufferedDuration: String,
        isLoading: Boolean,
        isPlaying: Boolean
    ): Event {

        val bundle = Bundle().apply {

            putString(Analytics.KEY_ALBUM_ID, album.id)
            putString(Analytics.KEY_ALBUM_TITLE, album.title)
            putString(Analytics.KEY_CATEGORY_ID, album.categoryId)
            putString(Analytics.KEY_EPISODE_ID, episode.tId)
            putString(Analytics.KEY_EPISODE_NAME, episode.tTitle)
            putString(Analytics.KEY_ARTIST, episode.tArtists)
            putString(Analytics.KEY_EVENT_Source, origin)
            putInt(Analytics.KEY_PLAYBACK_STATE, state)
            putString(Analytics.KEY_DURATION, duration)
            putString(Analytics.KEY_CONTENT_DURATION, contentDuration)
            putString(Analytics.KEY_BUFFERED_DURATION, bufferedDuration)
            putBoolean(Analytics.KEY_IS_LOADING, isLoading)
            putBoolean(Analytics.KEY_IS_PLAYING, isPlaying)
        }

        return Event(Analytics.EVENT_PAUSE, bundle)
    }

    fun skipPreviousClicked(
        origin: String,
        album: Album,
        episode: Episode,
        state: Int,
        duration: String,
        contentDuration: String,
        bufferedDuration: String,
        isLoading: Boolean,
        isPlaying: Boolean
    ): Event {

        val bundle = Bundle().apply {

            putString(Analytics.KEY_ALBUM_ID, album.id)
            putString(Analytics.KEY_ALBUM_TITLE, album.title)
            putString(Analytics.KEY_CATEGORY_ID, album.categoryId)
            putString(Analytics.KEY_EPISODE_ID, episode.tId)
            putString(Analytics.KEY_EPISODE_NAME, episode.tTitle)
            putString(Analytics.KEY_ARTIST, episode.tArtists)
            putString(Analytics.KEY_EVENT_Source, origin)
            putInt(Analytics.KEY_PLAYBACK_STATE, state)
            putString(Analytics.KEY_DURATION, duration)
            putString(Analytics.KEY_CONTENT_DURATION, contentDuration)
            putString(Analytics.KEY_BUFFERED_DURATION, bufferedDuration)
            putBoolean(Analytics.KEY_IS_LOADING, isLoading)
            putBoolean(Analytics.KEY_IS_PLAYING, isPlaying)
        }

        return Event(Analytics.EVENT_PREVIOUS, bundle)
    }

    fun skipNextClicked(
        origin: String,
        album: Album,
        episode: Episode,
        state: Int,
        duration: String,
        contentDuration: String,
        bufferedDuration: String,
        isLoading: Boolean,
        isPlaying: Boolean
    ): Event {

        val bundle = Bundle().apply {

            putString(Analytics.KEY_ALBUM_ID, album.id)
            putString(Analytics.KEY_ALBUM_TITLE, album.title)
            putString(Analytics.KEY_CATEGORY_ID, album.categoryId)
            putString(Analytics.KEY_EPISODE_ID, episode.tId)
            putString(Analytics.KEY_EPISODE_NAME, episode.tTitle)
            putString(Analytics.KEY_ARTIST, episode.tArtists)
            putString(Analytics.KEY_EVENT_Source, origin)
            putInt(Analytics.KEY_PLAYBACK_STATE, state)
            putString(Analytics.KEY_DURATION, duration)
            putString(Analytics.KEY_CONTENT_DURATION, contentDuration)
            putString(Analytics.KEY_BUFFERED_DURATION, bufferedDuration)
            putBoolean(Analytics.KEY_IS_LOADING, isLoading)
            putBoolean(Analytics.KEY_IS_PLAYING, isPlaying)
        }

        return Event(Analytics.EVENT_NEXT, bundle)
    }

    fun skipRewindClicked(
        origin: String,
        album: Album,
        episode: Episode,
        state: Int,
        duration: String,
        contentDuration: String,
        bufferedDuration: String,
        isLoading: Boolean,
        isPlaying: Boolean
    ): Event {

        val bundle = Bundle().apply {

            putString(Analytics.KEY_ALBUM_ID, album.id)
            putString(Analytics.KEY_ALBUM_TITLE, album.title)
            putString(Analytics.KEY_CATEGORY_ID, album.categoryId)
            putString(Analytics.KEY_EPISODE_ID, episode.tId)
            putString(Analytics.KEY_EPISODE_NAME, episode.tTitle)
            putString(Analytics.KEY_ARTIST, episode.tArtists)
            putString(Analytics.KEY_EVENT_Source, origin)
            putInt(Analytics.KEY_PLAYBACK_STATE, state)
            putString(Analytics.KEY_DURATION, duration)
            putString(Analytics.KEY_CONTENT_DURATION, contentDuration)
            putString(Analytics.KEY_BUFFERED_DURATION, bufferedDuration)
            putBoolean(Analytics.KEY_IS_LOADING, isLoading)
            putBoolean(Analytics.KEY_IS_PLAYING, isPlaying)
        }

        return Event(Analytics.EVENT_REWIND, bundle)
    }

    fun skipFastForwardClicked(
        origin: String,
        album: Album,
        episode: Episode,
        state: Int,
        duration: String,
        contentDuration: String,
        bufferedDuration: String,
        isLoading: Boolean,
        isPlaying: Boolean
    ): Event {

        val bundle = Bundle().apply {

            putString(Analytics.KEY_ALBUM_ID, album.id)
            putString(Analytics.KEY_ALBUM_TITLE, album.title)
            putString(Analytics.KEY_CATEGORY_ID, album.categoryId)
            putString(Analytics.KEY_EPISODE_ID, episode.tId)
            putString(Analytics.KEY_EPISODE_NAME, episode.tTitle)
            putString(Analytics.KEY_ARTIST, episode.tArtists)
            putString(Analytics.KEY_EVENT_Source, origin)
            putInt(Analytics.KEY_PLAYBACK_STATE, state)
            putString(Analytics.KEY_DURATION, duration)
            putString(Analytics.KEY_CONTENT_DURATION, contentDuration)
            putString(Analytics.KEY_BUFFERED_DURATION, bufferedDuration)
            putBoolean(Analytics.KEY_IS_LOADING, isLoading)
            putBoolean(Analytics.KEY_IS_PLAYING, isPlaying)
        }

        return Event(Analytics.EVENT_FAST_FORWARD, bundle)
    }
}
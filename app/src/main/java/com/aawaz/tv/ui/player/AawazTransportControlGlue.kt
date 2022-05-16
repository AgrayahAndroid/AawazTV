package com.aawaz.tv.ui.player

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.core.widget.TextViewCompat
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.*
import coil.ImageLoader
import coil.request.LoadRequest
import coil.size.Scale
import coil.transform.RoundedCornersTransformation
import com.aawaz.tv.R
import com.aawaz.tv.data.db.Episode
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter
import org.koin.java.KoinJavaComponent.inject
import timber.log.Timber
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.min

/**
 * Created by Rinav Gangar <rinav.dev> on 13/5/20.
 * Agrahyah Technologies Pvt Ltd
 * rinav4all@gmail.com
 */
class AawazTransportControlGlue(
    context: Context?,
    playerAdapter: LeanbackPlayerAdapter?,
    private val player: SimpleExoPlayer
) : PlaybackTransportControlGlue<LeanbackPlayerAdapter>(context, playerAdapter) {

    private val imageLoader: ImageLoader by inject(ImageLoader::class.java)

    companion object {
        /** Default time used when skipping playback in milliseconds */
        private val SKIP_PLAYBACK_MILLIS: Long = TimeUnit.SECONDS.toMillis(10)
    }

    private val actionRewind = PlaybackControlsRow.RewindAction(context)
    private val actionFastForward = PlaybackControlsRow.FastForwardAction(context)

    private val actionNext = PlaybackControlsRow.SkipNextAction(context)
    private val actionPrevious = PlaybackControlsRow.SkipPreviousAction(context)

    fun skipForward(millis: Long = SKIP_PLAYBACK_MILLIS) =
        // Ensures we don't advance past the content duration (if set)
        player.seekTo(
            if (player.contentDuration > 0) {
                min(player.contentDuration, player.currentPosition + millis)
            } else {
                player.currentPosition + millis
            }
        )

    fun skipBackward(millis: Long = SKIP_PLAYBACK_MILLIS) =
        // Ensures we don't go below zero position
        player.seekTo(max(0, player.currentPosition - millis))

    private fun skipPrevious() {
        player.previous()
    }

    private fun skipNext() {
        player.next()
    }

    override fun onCreatePrimaryActions(adapter: ArrayObjectAdapter?) {
        super.onCreatePrimaryActions(adapter)

        // Append rewind and fast forward actions to our player, keeping the play/pause actions
        // created by default by the glue
        adapter?.add(actionPrevious)
        adapter?.add(actionRewind)
        adapter?.add(actionFastForward)
        adapter?.add(actionNext)
    }

    override fun onActionClicked(action: Action) = when (action) {

        actionRewind -> skipBackward()
        actionFastForward -> skipForward()
        actionNext -> skipNext()
        actionPrevious -> skipPrevious()
        else -> {
            Timber.d("some unknown action: ${action.id} :: ${action.label1}")
            super.onActionClicked(action)
        }
    }

    override fun onKey(
        v: View?,
        keyCode: Int,
        event: KeyEvent
    ): Boolean { // to handle fast forward and other keys, we overrided this!

        Timber.d("GGGXX: keyCode: $keyCode :: event: ${event.toString()}")

        return when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP,
            KeyEvent.KEYCODE_DPAD_DOWN,
            KeyEvent.KEYCODE_DPAD_RIGHT,
            KeyEvent.KEYCODE_DPAD_LEFT,
            KeyEvent.KEYCODE_BACK,
            KeyEvent.KEYCODE_ESCAPE -> false
            else -> {
                val primaryActionsAdapter =
                    controlsRow.primaryActionsAdapter
                var action =
                    controlsRow.getActionForKeyCode(primaryActionsAdapter, keyCode)
                if (action == null) {
                    action = controlsRow.getActionForKeyCode(
                        controlsRow.secondaryActionsAdapter,
                        keyCode
                    )
                }
                if (action != null) {
                    if (event.action == 0) {
                        onActionClicked(action)
                    }
                    true
                } else {
                    false
                }
            }
        }
    }

    fun updateArt(url: String?) {

        val request = LoadRequest.Builder(context)
            .data(url)
            .size(400, 400)
            .scale(Scale.FILL)
            .error(R.drawable.logo_grey_sm_en)
            .target { drawable ->
                // Handle the result.
                art = drawable
            }
            .transformations(RoundedCornersTransformation(10F))
            .build()

        imageLoader.execute(request)
    }

    override fun onCreateRowPresenter(): PlaybackRowPresenter {
        return super.onCreateRowPresenter().apply {
            (this as? PlaybackTransportRowPresenter)?.setDescriptionPresenter(DescriptionPresenter())
        }
    }

    // Customize Title and body of player
    private class DescriptionPresenter : AbstractDetailsDescriptionPresenter() {

        override fun onBindDescription(viewHolder: ViewHolder, item: Any) {

//            Timber.d("XXXXXXXXXXX: item :: $item ${item.javaClass}")

            val glue: AawazTransportControlGlue = item as AawazTransportControlGlue

            viewHolder.title.apply {
                setPadding(0, 16, 0, 0)
                text = glue.title ?: ""
            }

            viewHolder.body.apply {
                isSingleLine = false
                ellipsize = TextUtils.TruncateAt.END
                TextViewCompat.setAutoSizeTextTypeWithDefaults(
                    this,
                    TextViewCompat.AUTO_SIZE_TEXT_TYPE_NONE
                )
                text = glue.subtitle // this is actually description
                maxLines = 5
                minLines = 5
            }

            // set artist
            val tag = glue.player.currentTag
            Log.v("CheckResponse","artist  :  $tag")
            val artist = if (tag == null) "" else (tag as Episode).tArtists


            viewHolder.subtitle.apply {
                if (artist.isNullOrBlank()) {
                    visibility = View.GONE
                } else {
                    visibility = View.VISIBLE
                    text = artist
                }
            }
        }
    }
}
package com.aawaz.tv.ui

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import androidx.core.widget.TextViewCompat
import androidx.leanback.app.PlaybackSupportFragment
import androidx.leanback.widget.*
import androidx.navigation.fragment.navArgs
import coil.Coil
import coil.request.LoadRequest
import timber.log.Timber

/**
 * Created by Rinav Gangar <rinav.dev> on 12/5/20.
 * Agrahyah Technologies Pvt Ltd
 * rinav4all@gmail.com
 */
class TvNowPlayingFragment : PlaybackSupportFragment() {

    private val args: TvNowPlayingFragmentArgs by navArgs()

    private lateinit var rowsAdapter: ArrayObjectAdapter
    private var primaryActionsAdapter: ArrayObjectAdapter? = null
    protected var playPauseAction: PlaybackControlsRow.PlayPauseAction? = null
    private var skipNextAction: PlaybackControlsRow.SkipNextAction? = null
    private var skipPreviousAction: PlaybackControlsRow.SkipPreviousAction? = null
    private var playbackControlsRow: PlaybackControlsRow? = null

    private lateinit var listRowAdapter: ArrayObjectAdapter
    private val listRow: ListRow? = null

    private lateinit var presenterSelector: ClassPresenterSelector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.d("onCreate")

        listRowAdapter = ArrayObjectAdapter()
        presenterSelector = ClassPresenterSelector()
        rowsAdapter = ArrayObjectAdapter(presenterSelector)

        val album = args.metadata

        val metadata: MediaMetadataCompat =
            MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, album.id)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, album.title)
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, album.description)
                //.putString(MediaMetadataCompat.METADATA_KEY_YEAR, album.year)
                .putString(
                    MediaMetadataCompat.METADATA_KEY_MEDIA_URI,
                    "https://storage.googleapis.com/aawaz-stateless/2019/01/Akbar-Birbal-Episode-2.mp3"
                )
                .putString(
                    MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI,
                    album.art.toString()
                )
                .putString(MediaMetadataCompat.METADATA_KEY_GENRE, album.categoryId)
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, album.categoryName)
                .build()

        //Timber.d("XXXXXXXX: metadata: ${metadata.toString()}")

        updateMetadata(metadata)
    }

    protected fun updateMetadata(metadata: MediaMetadataCompat) {

        if (playbackControlsRow == null) {
            initializePlaybackControls(metadata)
        }

        (playbackControlsRow?.item as MutableMediaMetadataHolder).metadata = metadata

        rowsAdapter.notifyArrayItemRangeChanged(
            rowsAdapter.indexOf(playbackControlsRow), 1
        )

        val request = LoadRequest.Builder(requireContext())
            .data(metadata.description.iconUri)
            .target { drawable ->
                // Handle the result.
                playbackControlsRow?.imageDrawable = drawable
            }
            .build()
        Coil.execute(request)

//        lifecycleScope.launch(Dispatchers.IO) {
//            metadata.description.iconUri?.let {
//                val bitmap = Coil.get(it)
//
//
//                playbackControlsRow?.imageDrawable = bitmap
//
//            }
//        }
    }

    private fun initializePlaybackControls(metadata: MediaMetadataCompat) {
        setupRows()
        addPlaybackControlsRow(metadata)

        adapter = rowsAdapter
    }

    private fun setupRows() {

        val playbackControlsRowPresenter = PlaybackControlsRowPresenter(DescriptionPresenter())

        presenterSelector.addClassPresenter(
            PlaybackControlsRow::class.java,
            playbackControlsRowPresenter
        )
    }

    private fun addPlaybackControlsRow(metadata: MediaMetadataCompat) {

        playbackControlsRow = PlaybackControlsRow(
            MutableMediaMetadataHolder(
                metadata
            )
        )
        rowsAdapter.add(playbackControlsRow)


        //resetPlaybackRow()

        val presenterSelector = ControlButtonPresenterSelector()
        primaryActionsAdapter = ArrayObjectAdapter(presenterSelector)
        playbackControlsRow?.primaryActionsAdapter = primaryActionsAdapter

        playPauseAction = PlaybackControlsRow.PlayPauseAction(activity)
        skipNextAction = PlaybackControlsRow.SkipNextAction(activity)
        skipPreviousAction = PlaybackControlsRow.SkipPreviousAction(activity)

        primaryActionsAdapter?.add(skipPreviousAction)
        primaryActionsAdapter?.add(playPauseAction)
        primaryActionsAdapter?.add(skipNextAction)
    }

    private class DescriptionPresenter : AbstractDetailsDescriptionPresenter() {


        override fun onBindViewHolder(
            viewHolder: Presenter.ViewHolder?,
            item: Any?,
            payloads: MutableList<Any>?
        ) {
            super.onBindViewHolder(viewHolder, item, payloads)
        }

        override fun onBindDescription(viewHolder: ViewHolder, item: Any) {
            val data: MutableMediaMetadataHolder = item as MutableMediaMetadataHolder
            viewHolder.title.text = data.metadata.description.title
            //viewHolder.subtitle.text = data.metadata.getText(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION)
            viewHolder.body.isSingleLine = false

            TextViewCompat.setAutoSizeTextTypeWithDefaults(
                viewHolder.body,
                TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM
            );



            viewHolder.body.text =
                data.metadata.getText(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION)
        }
    }

    private class MutableMediaMetadataHolder(var metadata: MediaMetadataCompat)
}
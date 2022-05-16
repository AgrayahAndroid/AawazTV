package com.aawaz.tv.ui.player

import android.net.Uri
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.leanback.app.PlaybackSupportFragment
import androidx.leanback.app.PlaybackSupportFragmentGlueHost
import androidx.leanback.media.PlaybackGlue
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.Coil
import coil.request.LoadRequest
import coil.size.Scale
import coil.transform.BlurTransformation
import com.aawaz.tv.R
import com.aawaz.tv.data.ResultData
import com.aawaz.tv.data.db.Episode
import com.aawaz.tv.data.mapper.toEpisode
import com.aawaz.tv.data.remote.AlbumDetailsResponse
import com.aawaz.tv.data.remote.NetworkAlbum
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.firebase.auth.FirebaseAuth
import com.rollbar.android.Rollbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

/**
 * Created by Rinav Gangar <rinav.dev> on 12/5/20.
 * Agrahyah Technologies Pvt Ltd
 * rinav4all@gmail.com
 */
class ExoPlayerFragment : PlaybackSupportFragment() {

    companion object {
        const val UPDATE_DELAY = 100
    }

    private val args: ExoPlayerFragmentArgs by navArgs()
    private val vm: ExoPlayerViewModel by viewModel()

    private var dataSourceFactory: DataSource.Factory? = null

    /** Glue layer between the player and our UI */
    private lateinit var transportControlGlue: AawazTransportControlGlue

    private var album: NetworkAlbum? = null
    private var exo: SimpleExoPlayer? = null

    /** Allows interaction with transport controls, volume keys, media buttons  */
    private lateinit var mediaSession: MediaSessionCompat

    /**
     * Connects a [MediaSessionCompat] to a [Player] so transport controls are handled automatically
     */
    private lateinit var mediaSessionConnector: MediaSessionConnector

    private lateinit var metrics: DisplayMetrics

    override fun onCreate(savedInstanceState: Bundle?) {
        //Timber.d("EXO-EXO: onCreate :: ${vm.toPlayIndex}#${vm.toSeekPosition}")
        super.onCreate(savedInstanceState)
        Rollbar.init(context);
        metrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(metrics)

        //setOnKeyInterceptListener(keyListener)

        dataSourceFactory = DefaultDataSourceFactory(requireContext(), getString(R.string.app_name))
        setupPlayer()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Timber.d("EXO-EXO: onCreateView :: ${vm.toPlayIndex}#${vm.toSeekPosition}")

        backgroundType = BG_LIGHT

        vm.getEpisodesByAlbumId(
            FirebaseAuth.getInstance().currentUser?.uid ?: "",
            args.album.id,
            args.album.lang

        )
        progressBarManager.show()
        setupData()

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onResume() {
        //Timber.d("EXO-EXO: onResume :: ${vm.toPlayIndex}#${vm.toSeekPosition}")
        super.onResume()

        mediaSessionConnector.setPlayer(exo)
        mediaSession.isActive = true

        // Kick off metadata update task which runs periodically in the main thread
        //view?.postDelayed(updateMetadataTask, METADATA_UPDATE_INTERVAL_MILLIS)
    }

    /**
     * Deactivates and removes callbacks from [MediaSessionCompat] since the [Player] instance is
     * destroyed in onStop and required metadata could be missing.
     */
    override fun onPause() {
        //Timber.d("EXO-EXO: onPause :: ${vm.toPlayIndex}#${vm.toSeekPosition}")
        super.onPause()

        //transportControlGlue.pause()
        mediaSession.isActive = false
        //mediaSessionConnector.setPlayer(null)
    }

    override fun onStart() {
        //Timber.d("EXO-EXO: onStart :: ${vm.toPlayIndex}#${vm.toSeekPosition}")
        transportControlGlue.play()
        super.onStart()
    }

    override fun onStop() {
        //Timber.d("EXO-EXO: onStop :: ${vm.toPlayIndex}#${vm.toSeekPosition}")

        exo?.let {
            vm.toPlayIndex = it.currentWindowIndex
            vm.toSeekPosition = it.currentPosition
        }
        transportControlGlue.pause()
        mediaSession.isActive = false
        super.onStop()
    }

    override fun onDestroyView() {
        //Timber.d("EXO-EXO: onDestroyView :: ${vm.toPlayIndex}#${vm.toSeekPosition}")
        super.onDestroyView()
    }

    /** Do all final cleanup in onDestroy */
    override fun onDestroy() {
        //Timber.d("EXO-EXO onDestroy called, releasing media session :: ${vm.toPlayIndex}#${vm.toSeekPosition}")
        super.onDestroy()

        mediaSessionConnector.setPlayer(null)
        mediaSession.release()
    }

    private fun setupData() {

        //Timber.d("XXXXXXXXX: args: ${args.album} with initial play episode ${args.initialTrackToPlay}")

        vm.album.observe(viewLifecycleOwner, Observer {

            //Timber.d("XXXXXXXXX: 111 :: ${it.toString()}")

            when (val result = it as ResultData<AlbumDetailsResponse>) {
                is ResultData.Loading -> {
                    Timber.d("XXXXXXXXX loading")
                    progressBarManager.show()
                }

                is ResultData.Success -> {
                    Timber.d("XXXXXXXXX success")
                    progressBarManager.hide()

                    result.value?.let {
                        it.album?.let { alb ->
                            album = alb

                            //Timber.d("XXXXXXXXX ${alb.id} :: ${alb.title}")

                            alb.tracks?.let { eps ->
//                                eps.forEach { ep ->
//                                    Timber.d("XXXXXXXXX alb---- ${ep.collectionId} :: ${ep.tId} :: ${ep.tTitle}")
//                                }
//
                                val tracks = eps.mapIndexed { index, networkEpisode ->
                                    networkEpisode.toEpisode(alb)
                                }
                                vm.toPlayIndex = args.initialTrackToPlay?.let { ep ->
                                    tracks.indexOf(ep)
                                } ?: 0

                                //Timber.d("XXXXXXXXX: args: ${args.album} with initial play index ${vm.toPlayIndex}")
                                setupExo(tracks, vm.toPlayIndex)
                            }
                        }
                    }
                }

                is ResultData.Failure -> {
                    progressBarManager.hide()

                    Timber.d("XXXXXXXXX failure ${result.message}")
                }
            }
        })
    }

    private fun setupPlayer() {

        val trackSelector = DefaultTrackSelector(requireContext())
        val audioAttributes: AudioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.CONTENT_TYPE_MUSIC)
            .build()

        exo = SimpleExoPlayer.Builder(requireContext())
            .setTrackSelector(trackSelector)
            .build()

        exo!!.setAudioAttributes(audioAttributes, true)

        // exo?.addAnalyticsListener(EventLogger(trackSelector))

        val playerAdapter = LeanbackPlayerAdapter(
            requireContext(), exo!!,
            UPDATE_DELAY
        )

        playerAdapter.setProgressUpdatingEnabled(true)

        transportControlGlue = AawazTransportControlGlue(
            requireContext(),
            playerAdapter,
            exo!!
        )

        transportControlGlue.apply {
            host = PlaybackSupportFragmentGlueHost(this@ExoPlayerFragment)
            host.setOnKeyInterceptListener(keyListener)

            isControlsOverlayAutoHideEnabled = false

            addPlayerCallback(object : PlaybackGlue.PlayerCallback() {
                override fun onPlayStateChanged(glue: PlaybackGlue?) {
                    super.onPlayStateChanged(glue)
                    Timber.d("XXXXXXXXX callback: onPlayStateChanged")
                }

                override fun onPreparedStateChanged(glue: PlaybackGlue?) {
                    super.onPreparedStateChanged(glue)
                    Timber.d("XXXXXXXXX callback: onPreparedStateChanged")
                }

                override fun onPlayCompleted(glue: PlaybackGlue?) {
                    super.onPlayCompleted(glue)
                    Timber.d("XXXXXXXXX callback: onPlayCompleted")
                }
            })

            playWhenPrepared()
        }

        mediaSession = MediaSessionCompat(requireContext(), getString(R.string.app_name))
        mediaSessionConnector = MediaSessionConnector(mediaSession)
    }

    private fun setupExo(tracks: List<Episode>, initialIndex: Int) {
        val mediaSources = buildMediaSource(tracks)

        val concatenatingMediaSource = ConcatenatingMediaSource()
        concatenatingMediaSource.addMediaSources(mediaSources)
        //loopingMediaSource = LoopingMediaSource(concatenatingMediaSource)

        //exo?.seekToDefaultPosition()
        //exo?.seekTo(toPlayIndex - 1, 0L)

        exo?.prepare(concatenatingMediaSource, false, true)

        exo?.seekTo(vm.toPlayIndex, vm.toSeekPosition)

        val epToPlay = tracks[vm.toPlayIndex]

        transportControlGlue.title = epToPlay.tTitle
        transportControlGlue.subtitle = epToPlay.tDescription

        transportControlGlue.updateArt(epToPlay.tArt)
        updateBackground(epToPlay.tArt)

        exo?.addListener(object : Player.EventListener {

            override fun onTracksChanged(
                trackGroups: TrackGroupArray,
                trackSelections: TrackSelectionArray
            ) {
                Timber.d("XXXXXXXXX: listener: Track Changed")
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                Timber.d("XXXXXXXXX: listener: The player state is $playbackState and playWhenReady : $playWhenReady")
            }

            override fun onPositionDiscontinuity(reason: Int) {
                //Timber.d("XXXXXXXXX: listener: Discontinuity: Reason: $reason")

                if (reason == Player.DISCONTINUITY_REASON_PERIOD_TRANSITION
                    || reason == Player.DISCONTINUITY_REASON_SEEK_ADJUSTMENT
                ) {
                    updatePlayerData()
                }
            }
        })
    }

    private fun updatePlayerData() {

        exo?.let {
            val window = Timeline.Window()
            val currentEpisode =
                it.currentTimeline
                    .getWindow(it.currentWindowIndex, window).tag
                        as Episode?

            vm.toPlayIndex = it.currentWindowIndex
            currentEpisode?.let { ep ->
                //Timber.d("XXXXXXXXX: listener: $ep")

                transportControlGlue.title = ep.tTitle
                transportControlGlue.subtitle = ep.tDescription
                transportControlGlue.updateArt(ep.tArt)
                updateBackground(ep.tArt)
            }
        }
    }

    private fun buildMediaSource(metadatas: List<Episode>): List<MediaSource> {

        return metadatas.mapIndexed { index, episode ->
            //Timber.d("buildMediaSource: $index :: ${episode.tId} :: ${episode.tUrl}")
            ProgressiveMediaSource.Factory(dataSourceFactory)
                .setTag(episode)
                .createMediaSource(Uri.parse(episode.tUrl))
        }
    }

    private fun updateBackground(uri: String?) {
        val width = metrics.widthPixels
        val height = metrics.heightPixels

        val request = LoadRequest.Builder(requireContext())
            .data(uri)
            .scale(Scale.FILL)
            .size(width, height)
            .crossfade(true)
            .transformations(BlurTransformation(requireContext(), 20F, 4F))
            .error(R.drawable.aawaz_banner_android_tv)
            .target { drawable ->
                view?.background = drawable
            }
            .build()
        Coil.execute(request)
    }

    private val keyListener = object : View.OnKeyListener {

        override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
            Timber.d("GGG: keyCode: $keyCode :: event: ${event.toString()}")

            if (event?.action == KeyEvent.ACTION_DOWN) {

                when (keyCode) {

                    KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> {
                        Timber.d("GGG: on DPAD FAST-FORWARD")
                        transportControlGlue.skipForward()
                        return true
                    }

                    KeyEvent.KEYCODE_MEDIA_REWIND -> {
                        Timber.d("GGG: on DPAD REWIND")
                        transportControlGlue.skipBackward()
                        return true
                    }
                }
            }

            if (keyCode == KeyEvent.KEYCODE_BACK && event?.action == KeyEvent.ACTION_UP) {

                Timber.d("GGG: on backPressed in exo frag UP")
                //NavHostFragment.findNavController(this@ExoPlayerFragment).popBackStack()
                findNavController()
                    .navigate(ExoPlayerFragmentDirections.actionExoPlayerFragmentToExitFragment(com.aawaz.tv.utils.C.EXIT_EXO_PLAYER))

                return true
            }

            return false
        }
    }
}
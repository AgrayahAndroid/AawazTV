package com.aawaz.tv.ui.details

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.ImageLoader
import coil.request.LoadRequest
import com.aawaz.tv.R
import com.aawaz.tv.data.ResultData
import com.aawaz.tv.data.db.Album
import com.aawaz.tv.data.db.Episode
import com.aawaz.tv.data.mapper.toEpisode
import com.aawaz.tv.data.remote.AlbumDetailsResponse
import com.aawaz.tv.presenters.EpisodePresenter
import com.aawaz.tv.utils.Utils
import com.aawaz.tv.utils.Utils.albumColors
import com.google.firebase.auth.FirebaseAuth
import com.rollbar.android.Rollbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.java.KoinJavaComponent
import timber.log.Timber

/**
 * Created by Rinav Gangar <rinav.dev> on 1/5/20.
 * Agrahyah Technologies Pvt Ltd
 * rinav4all@gmail.com
 *
 */
class AlbumDetailsFragment : DetailsSupportFragment(), OnItemViewSelectedListener,
    OnItemViewClickedListener {

    private val imageLoader: ImageLoader by KoinJavaComponent.inject(ImageLoader::class.java)

    /** AndroidX navigation arguments */
    private val args: AlbumDetailsFragmentArgs by navArgs()
    private val vm: AlbumDetailsViewModel by viewModel()

    private lateinit var selectedAlbum: Album

    private lateinit var adapter: ArrayObjectAdapter
    private lateinit var presenterSelector: ClassPresenterSelector

    private lateinit var episodeHeader: HeaderItem
    private lateinit var episodeListRow: ListRow
    private lateinit var episodeRowAdapter: ArrayObjectAdapter
    private lateinit var metrics: DisplayMetrics


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Rollbar.init(context);

        selectedAlbum = args.album
        metrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(metrics)

        vm.getEpisodesByAlbumId(
            FirebaseAuth.getInstance().currentUser?.uid ?: "",
            args.album.id,
            args.album.lang
        )
        episodeHeader = HeaderItem(0, getString(R.string.episodes))
        episodeRowAdapter = ArrayObjectAdapter(EpisodePresenter(300))

        // Each time an item is selected, change the background
        setOnItemViewSelectedListener(this)

        // When user clicks on an item, navigate to the now playing screen
        onItemViewClickedListener = this

        setupAdapter()
        setupDetailsOverviewRow()

        // When a Related Video item is clicked.
        //onItemViewClickedListener =
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fetchAlbumDetails()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val colors = albumColors[selectedAlbum.id]
        if (colors != null) {
            val alpha: Int? = ColorUtils.setAlphaComponent(colors.toolbarBackgroundColor, 75)
            alpha?.let {
                view.setBackgroundColor(it)
            }
        }
    }

    override fun onItemSelected(
        itemViewHolder: Presenter.ViewHolder?,
        item: Any?,
        rowViewHolder: RowPresenter.ViewHolder?,
        row: Row?
    ) {
        if (item == null) return

        if (item is Episode) {

            //Timber.d("onItemSelected: ${row?.id} :: tid: ${item.tId}")

            val paletteColors = Utils.episodeColors[item.tId]
            paletteColors?.let { p ->

                val card = itemViewHolder?.view as ImageCardView?
                card?.let { vh ->

                    vh.setInfoAreaBackgroundColor(p.toolbarBackgroundColor)

                    val titleView = vh.findViewById<TextView>(R.id.title_text)
                    titleView?.setTextColor(p.textColor)
                    //titleView.maxLines = 3
                }
            }
        }
    }

    override fun onItemClicked(
        itemViewHolder: Presenter.ViewHolder?,
        item: Any?,
        rowViewHolder: RowPresenter.ViewHolder?,
        row: Row?
    ) {

        if (item == null) return
        //Timber.d("onItem clicked... ${item ?: ""}")

        if (item is Episode) {

            val direction =
                AlbumDetailsFragmentDirections.actionAlbumDetailsFragmentToExoPlayerFragment(
                    selectedAlbum, item
                )

            findNavController().navigate(direction)
        }
    }

    private val actionItemClickedListener =
        OnActionClickedListener { action ->
            //Timber.d("OnActionClicked ${action?.id} :: ${action?.label1}")

            val direction =
                AlbumDetailsFragmentDirections.actionAlbumDetailsFragmentToExoPlayerFragment(
                    selectedAlbum, null
                )

            findNavController().navigate(direction)
        }

    private fun setupAdapter() {

        // Set detail background and style.
        val detailsPresenter = FullWidthDetailsOverviewRowPresenter(DetailsDescriptionPresenter())
        // detailsPresenter.initialState = FullWidthDetailsOverviewRowPresenter.STATE_HALF

        val colors = albumColors[selectedAlbum.id]
        if (colors != null) {
            detailsPresenter.actionsBackgroundColor = colors.statusBarColor

            val alpha = ColorUtils.setAlphaComponent(colors.statusBarColor, 95)
            detailsPresenter.backgroundColor = alpha
        }

        detailsPresenter.onActionClickedListener = actionItemClickedListener

        presenterSelector = ClassPresenterSelector().apply {

            addClassPresenter(DetailsOverviewRow::class.java, detailsPresenter)
            addClassPresenter(ListRow::class.java, ListRowPresenter())
        }

        adapter = ArrayObjectAdapter(presenterSelector)
        setAdapter(adapter)
    }

    private fun setupDetailsOverviewRow() {

        val detailsOverRow = DetailsOverviewRow(selectedAlbum)

        val request = LoadRequest.Builder(requireContext())
            .data(selectedAlbum.art ?: R.drawable.logo_grey_sm_en)
            .crossfade(true)
            .error(R.drawable.logo_grey_sm_en)
            .target { drawable -> detailsOverRow.imageDrawable = drawable }
            .build()

        imageLoader.execute(request)

        val actionAdapter = SparseArrayObjectAdapter()

        val playNowAction = Action(
            ACTION_PLAY_NOW.toLong(),
            resources.getString(R.string.play_now_1), "",
            //resources.getString(R.string.play_now_2),
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_audiotrack_24)
        )

        actionAdapter[ACTION_PLAY_NOW] = playNowAction

        detailsOverRow.actionsAdapter = actionAdapter
        detailsOverRow.isImageScaleUpAllowed = true

        adapter.add(detailsOverRow)
    }

    private fun setupAlbumListRow(tracks: List<Episode>) {

        // Setup related row.
        //val listRowAdapter = ArrayObjectAdapter(CardPresenterSelector(requireContext()))
//
//        tracks.forEachIndexed { index, episode ->
//
//            val card = Card()
//            card.setTitle(getString(R.string.episode_title, episode.tNo?:"", episode.tTitle ?: ""))
//            card.setFooterColor("#3d3d3d")
//            //card.setDescription(episode.tDescription)
//            card.setImageUrl(episode.tArt)
//            card.setType(Card.Type.SQUARE_SMALL)
//            listRowAdapter.add(card)
//        }

        try {
            if (::episodeListRow.isInitialized) {
                //Timber.d("sssssss: removing ${episodeListRow.id}")
                adapter.remove(episodeListRow)
            }
        } catch (e: Exception) {
            Rollbar.instance().error(e)
            Timber.e(e, "sssssss: Unable to remove episodeListRow")
        }
        episodeRowAdapter.setItems(tracks, null)


        //Timber.d("ZZZZ: Adding episode row")
        //adapter.remove(episodeHeader)
        //adapter.remove(episodeListRow)

        episodeListRow = ListRow(episodeHeader, episodeRowAdapter)
        adapter.add(episodeListRow)
        setAdapter(adapter)
    }

    private fun fetchAlbumDetails() {
        //Timber.d("XXXXXXXXX: args: ${args.album}")

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
                            //album = alb

                            //Timber.d("XXXXXXXXX ${alb.id} :: ${alb.title}")

                            alb.tracks?.let { eps ->

                                val tracks = eps.mapIndexed { index, networkEpisode ->
                                    networkEpisode.toEpisode(alb)
                                }

                                setupAlbumListRow(tracks)
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

    companion object {
        private const val ACTION_PLAY_NOW = 1
    }
}
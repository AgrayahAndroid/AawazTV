package com.aawaz.tv.ui.browser

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Layout
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.ImageLoader
import coil.request.LoadRequest
import com.aawaz.tv.R
import com.aawaz.tv.analytics.AlbumEvent
import com.aawaz.tv.analytics.Analytics
import com.aawaz.tv.data.db.Album
import com.aawaz.tv.presenters.AlbumPresenter
import com.aawaz.tv.presenters.GridItemPresenter
import com.aawaz.tv.ui.player.ExoPlayerFragmentDirections
import com.aawaz.tv.utils.C
import com.aawaz.tv.utils.Utils
import com.rollbar.android.Rollbar
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.java.KoinJavaComponent
import timber.log.Timber
import java.util.concurrent.TimeUnit


/**
 * Created by Rinav Gangar <rinav.dev> on 30/4/20.
 * Agrahyah Technologies Pvt Ltd
 * rinav4all@gmail.com
 *
 * A fragment that lets user browse our collection of metadata.
 */
class MediaBrowserFragment : BrowseSupportFragment(), OnItemViewSelectedListener,
    OnItemViewClickedListener {

    // Lazy Inject ViewModel
    private val vm: MediaBrowserViewModel by viewModel()
    private val imageLoader: ImageLoader by KoinJavaComponent.inject(ImageLoader::class.java)

    private val analytics: Analytics by inject()

    //private lateinit var database: TvDatabase
    //private val database: TvDatabase by inject()

    /** Tint applied to the background, default to dark grey */
    private var currentTintColor: Int = ColorUtils.setAlphaComponent(
        Color.parseColor("#000000"),
        BACKGROUND_TINT_ALPHA
    )

    /** Animation task used to update the background tint */
    private var backgroundAnimation: Runnable? = null

    /** Background for our fragment, selected randomly at runtime */
    //private lateinit var backgroundDrawable: Deferred<Drawable>

    /** Job used to synchronize our media database */
    private lateinit var synchronizeJob: Job

    /** List row used exclusively to display "credits" and no media content */
    private lateinit var creditsRow: ListRow
    private lateinit var legalRow: ListRow


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Rollbar.init(context);
        title = getString(R.string.app_name)
        headersState = HEADERS_ENABLED
        headersState
        isHeadersTransitionOnBackEnabled = true

        badgeDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.aawaz_logo_nw_256)

        brandColor = ContextCompat.getColor(requireContext(), R.color.black_background)
        /*val permissionCheck =
            ContextCompat.checkSelfPermission(activity!!, Manifest.permission.READ_EXTERNAL_STORAGE)*/
        // set search icon color
        /*searchAffordanceColor =
            ContextCompat.getColor(requireContext(), R.color.black_color)*/
        setOnSearchClickedListener {
            findNavController().navigate(

                MediaBrowserFragmentDirections
                            .actionMediaBrowserFragmentToSearchFragment()
            )
        }
        // handle exit app via back pressed key event
        activity?.onBackPressedDispatcher?.addCallback(requireActivity(), onBackPressedCallback)

        // Setup this browser fragment's adapter
        adapter = ArrayObjectAdapter(ListRowPresenter())

        // Each time an item is selected, change the background
        onItemViewSelectedListener = this

        // When user clicks on an item, navigate to the now playing screen
        onItemViewClickedListener = this

        initLegalRow()
        initCreditsRow()

        // Keep track of the synchronization work so we can join it later
        synchronizeJob = lifecycleScope.launch(Dispatchers.IO) {

            // Now that the fragment has been created, we can populate our adapter
            populateAdapter(adapter as ArrayObjectAdapter)

            // Start a one-off synchronization job when fragment is shown, bypassing work manager
            //CollectionSynchronizer.synchronize(requireContext(), api)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Because we will be handling the database, we must do it from a different context
        // But we also need the view to be completely inflated to know its size, which is why we
        // use `view.post` here.

        view.post {

            lifecycleScope.launch(Dispatchers.IO) {

                val backgroundUrl = vm.backgroundUrl()

                //Timber.d("RRRRRRR: ${view.width}, ${view.height}")
                // Coil (suspends the current coroutine; non-blocking and thread safe)

                val request = LoadRequest.Builder(requireContext())
                    .data(backgroundUrl ?: R.mipmap.bg_browse_fallback)
                    .crossfade(true)
                    .size(view.width, view.height)
                    .error(R.mipmap.bg_browse_fallback)
                    .target(
                        onSuccess = { view.background = it },
                        onError = { R.mipmap.bg_browse_fallback }
                    )
                    .build()

                imageLoader.execute(request)
            }
        }
    }

    /** Convenience function used to update the background tint to match the selected item */
    private fun updateBackgroundTint(targetColor: Int) = view?.let { view ->

        // We update the background tint using a smooth animation
        ValueAnimator.ofObject(ArgbEvaluator(), currentTintColor, targetColor).apply {

            duration =
                BACKGROUND_ANIMATION_MILLIS

            // As the animation progresses, we update the background tint
            addUpdateListener {
                view.backgroundTintList = ColorStateList.valueOf(it.animatedValue as Int)
            }

            // Remote previously scheduled animation, if any
            backgroundAnimation?.let { view.removeCallbacks(it) }

            // Schedule a new animation, to let the user settle on an item
            backgroundAnimation = Runnable {
                start()
                currentTintColor = targetColor
                // Use the same time as animation to prevent overlap, since we only stop scheduled
                // animations but we don't stop ongoing animations
            }.also {
                view.postDelayed(
                    it,
                    BACKGROUND_ANIMATION_MILLIS
                )
            }
        }
    }

    /**
     * Convenience function used to populate the main screen's adapter with all media collections.
     * Since this function makes use of the database, it cannot be run from the main thread.
     */
    private fun populateAdapter(adapter: ArrayObjectAdapter) {
        //Timber.d("LLL : populateAdapter called")
        //val rowCount = adapter.size()

        lifecycleScope.launch(Dispatchers.Main) {

            vm.getCategories().observe(viewLifecycleOwner, Observer { collections ->

                //Timber.d("LLL : ${collections.size}")
                if (collections.isNotEmpty()) {
                    vm.getAlbums().observe(viewLifecycleOwner, Observer { albums ->

                        val categoryRows = mutableListOf<ListRow>()
                        collections.forEachIndexed { idx, category ->

                            //Timber.d("DIFF: CC: $idx, $category")

                            // Create header for each album
                            val header = HeaderItem(idx.toLong(), category.title)
                            header.description = category.description
                            //header.contentDescription = collection.description

                            val albumsByCategory =
                                albums.filter { it.categoryId == category.id }

                            //Timber.d("DIFF: si: albumsByCategory > ${category.title} is [${albumsByCategory.size}] size")

                            val listRowAdapter = ArrayObjectAdapter(AlbumPresenter()).apply {
                                setItems(albumsByCategory, listAlbumDiffCallback)
                            }

                            // Add a list row for the <header, row adapter> pair
                            //val listRow = ListRow(header, null)
                            val listRow = ListRow(idx.toLong(), header, listRowAdapter)
                            categoryRows.add(idx, listRow)
                        }

                        // Add all new rows at once using our diff callback for a smooth animation
                        setDataItems(adapter, categoryRows)
                    })
                }
            })
        }
    }

    @Synchronized
    fun setDataItems(adapter: ArrayObjectAdapter, categoryRows: List<ListRow>) {
        //Timber.d("DIFF: categoryRows: ${categoryRows.size}")
        adapter.setItems(categoryRows + legalRow + creditsRow, null)
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {

        override fun handleOnBackPressed() {
            Timber.d("onBackPressed captured in mediaBrowser...")

            try {
                findNavController()
                    .navigate(
                        MediaBrowserFragmentDirections.actionMediaBrowserFragmentToExitFragment(
                            C.EXIT_APP
                        )
                    )
            } catch (e: Exception) {
                Rollbar.instance().error(e)
                // when user presses back button twice
                findNavController().popBackStack()
            }
        }
    }

    private fun navigateToWebContent(item: GridItemPresenter.GridItem) {

        when (item.id) {
            C.TNC_ID, C.PRIVACY_POLICY_ID, C.DISCLAIMER_ID -> {
                findNavController().navigate(
                    MediaBrowserFragmentDirections.actionMediaBrowserFragmentToWebFragment(item.url)
                )
            }
        }
    }

    override fun onItemSelected(
        itemViewHolder: Presenter.ViewHolder?,
        item: Any?,
        rowViewHolder: RowPresenter.ViewHolder?,
        row: Row?
    ) {
        //Timber.d("onItemSelected: ${row?.id}")
        if (item == null) return

        if (item is Album) {

//            val colors = Utils.colorMap[item.id] //= ExtractedColors(m, d)
//            colors?.let {
//                colors.backgroundColor?.let { bg ->
//
//                    val card = itemViewHolder?.view as ImageCardView?
//                    card?.let { vh ->
//                        vh.setInfoAreaBackgroundColor(bg)
//                    }
//
//                    updateBackgroundTint(bg)
//
//                    // Set background in main thread
//                    CoroutineScope(Dispatchers.Main).launch {
//                        view?.backgroundTintMode = PorterDuff.Mode.OVERLAY
//                        view?.backgroundTintList = ColorStateList.valueOf(currentTintColor)
//                    }
//                }
//            }

            val paletteColors = Utils.albumColors[item.id]
            paletteColors?.let { p ->

                val card = itemViewHolder?.view as ImageCardView?
                card?.let { vh ->
                    vh.setInfoAreaBackgroundColor(p.toolbarBackgroundColor)

                    val titleView = vh.findViewById<TextView>(R.id.title_text)
                    titleView?.setTextColor(p.textColor)
                    titleView?.maxLines = 2

                    val contentView = vh.findViewById<TextView>(R.id.content_text)
                    contentView?.setTextColor(p.titleColor)
                    contentView?.maxLines = 3

                    updateBackgroundTint(p.toolbarBackgroundColor)

                    // Set background in main thread
                    CoroutineScope(Dispatchers.Main).launch {
                        view?.backgroundTintMode = PorterDuff.Mode.OVERLAY
                        view?.backgroundTintList = ColorStateList.valueOf(currentTintColor)
                    }
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
        //Timber.d("onItemClicked: ${row?.id} :: ${itemViewHolder?.javaClass} :: ${rowViewHolder?.javaClass} :: ${row?.javaClass}")

        when (item) {

            is GridItemPresenter.GridItem -> {
                //Timber.d("Grid Item clicked: ${item.id}-${item.title}-${item.url}")
                navigateToWebContent(item)
            }

            is Album -> {
                val metadata = item as Album?
                //Timber.d("onItemClicked: ${row?.id} :: ${metadata?.id} :: ${metadata?.title}")

                metadata?.let { album ->

                    analytics.logEvent(AlbumEvent.clicked("browserFragment", album))
//                    findNavController().navigate(
//                        MediaBrowserFragmentDirections
//                            .actionMediaBrowserFragmentToExoPlayerFragment(album)
//                    )

                    val action =
                        MediaBrowserFragmentDirections.actionMediaBrowserFragmentToAlbumDetailsFragment(
                            album
                        )

                    findNavController().navigate(action)
                    //MediaBrowserFragmentDirections.actionMediaBrowserFragmentToNowPlayingFragment(album)
                    //MediaBrowserFragmentDirections.actionMediaBrowserFragmentToTvNowPlayingFragment(album)
                }
            }

            else -> {
                Timber.d("Unknown Card Clicked")
            }
        }
    }

    private fun initLegalRow() {
        // Instantiate the legal row, which will be added to the adapter inside [populateAdapter]
        val legalHeader = HeaderItem(C.HEADER_LEGAL)
        val legalRowAdapter = ArrayObjectAdapter(GridItemPresenter())
        legalRowAdapter.add(C.ITEM_TNC)
        legalRowAdapter.add(C.ITEM_PRIVACY_POLICY)
        legalRowAdapter.add(C.ITEM_DISCLAIMER)

        legalRow = ListRow(legalHeader, legalRowAdapter)
    }

    private fun initCreditsRow() {
        // Instantiate the credits row, which will be added to the adapter inside [populateAdapter]
        creditsRow = ListRow(
            HeaderItem("Made in India with \u2764\uFE0F by Aawaz.com"), object : ObjectAdapter() {
                override fun size(): Int = 0
                override fun get(position: Int): Any? = null
            })
    }

    /** Used to efficiently add items to our array adapter for display */
    private val listRowDiffCallback = object : DiffCallback<ListRow>() {

        override fun areContentsTheSame(
            oldItem: ListRow,
            newItem: ListRow
        ): Boolean {
            // Timber.d("DIFF: areContentsTheSame: ${oldItem.hashCode() == newItem.hashCode()}")
            return oldItem.hashCode() == newItem.hashCode()
        }


        override fun areItemsTheSame(
            oldItem: ListRow,
            newItem: ListRow
        ): Boolean {

            return oldItem.headerItem == newItem.headerItem &&
                    oldItem.adapter.size() == newItem.adapter.size() &&
                    (0 until adapter.size()).all { idx ->

//                        Timber.d("DIFF: 3: idx: $idx")
                        return try {

                            oldItem.adapter.get(idx) == newItem.adapter.get(idx)
                        } catch (e: java.lang.Exception) {
                            Timber.e(e, "Error in diff")
                            false
                        }
                    }
        }
    }

    /** Used to efficiently add items to our array adapter for display */
    private val listAlbumDiffCallback = object : DiffCallback<Album>() {

        override fun areContentsTheSame(
            oldItem: Album,
            newItem: Album
        ): Boolean {
//            Timber.d("DIFF: XX areContentsTheSame: ${oldItem.hashCode() == newItem.hashCode()}")
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areItemsTheSame(
            oldItem: Album,
            newItem: Album
        ): Boolean {

            return oldItem == newItem &&
                    oldItem.id == newItem.id &&
                    oldItem.title == newItem.title &&
                    oldItem.categoryId == newItem.categoryId &&
                    oldItem.url == newItem.url &&
                    oldItem.art == newItem.art &&
                    oldItem.description == newItem.description
        }
    }

    companion object {
        /** Animation time in milliseconds for background changes */
        private val BACKGROUND_ANIMATION_MILLIS: Long = TimeUnit.SECONDS.toMillis(1)

        /** Alpha component (0-255) of the background color tint */
        const val BACKGROUND_TINT_ALPHA: Int = 150
    }

}
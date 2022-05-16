package com.aawaz.tv.presenters

import android.graphics.Color
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.leanback.widget.BaseCardView
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import androidx.tvprovider.media.tv.TvContractCompat
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.request.LoadRequest
import coil.transition.CrossfadeTransition
import com.aawaz.tv.R
import com.aawaz.tv.data.db.Album
import com.aawaz.tv.utils.PaletteTransition
import com.aawaz.tv.utils.PaletteUtils
import com.aawaz.tv.utils.TvLauncherUtils
import com.aawaz.tv.utils.Utils
import org.koin.java.KoinJavaComponent
import kotlin.math.roundToInt

class SearchAlbumPresenter (private val cardHeight: Int = DEFAULT_CARD_HEIGHT) : Presenter() {

    private val imageLoader: ImageLoader by KoinJavaComponent.inject(ImageLoader::class.java)

    init {
        val cardWidth =
            TvLauncherUtils.parseAspectRatio(DEFAULT_ASPECT_RATIO)
                .let {
                    (cardHeight * it.numerator / it.denominator)
                }

        cWidth = (cardWidth / 1.2).roundToInt()
        cHeight = (cardHeight / 1.2).roundToInt()
    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {

        return ViewHolder(ImageCardView(parent.context).apply {
            isFocusable = true
            isFocusableInTouchMode = true
            // Set card background to dark gray while image loads
            setBackgroundColor(Color.DKGRAY)
            //setBackgroundColor(Color.TRANSPARENT)
            // Do not display text under the card image
            //infoVisibility = View.VISIBLE

            cardType = BaseCardView.CARD_TYPE_INFO_UNDER_WITH_EXTRA
            infoVisibility = BaseCardView.CARD_REGION_VISIBLE_SELECTED
        })
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {

        // Cast item as a MediaMetadataCompat and viewholder's view as TextView
        //val metadata = item as TvMediaMetadata
        val album = item as Album
        val card = viewHolder.view as ImageCardView

        // Computes the card width from the given height and metadata aspect ratio
//        val cardWidth =
//            TvLauncherUtils.parseAspectRatio(DEFAULT_ASPECT_RATIO)
//                .let {
//                    (cardHeight * it.numerator / it.denominator)
//                }

        card.titleText = album.title
        card.contentText = album.description
        card.setMainImageScaleType(ImageView.ScaleType.CENTER_CROP)


        //card.setMainImageDimensions((cardWidth / 1.2).roundToInt(), (cardHeight / 1.2).roundToInt())
        card.setMainImageDimensions(cWidth, cHeight)
        //card.setMainImageDimensions(320, 320)
        card.badgeImage =
            ContextCompat.getDrawable(card.context, R.drawable.ic_baseline_audiotrack_24)

        //card.mainImageView.loadImage(card.context, album.art, cWidth, cHeight)

        val request = LoadRequest.Builder(card.context)
            .data(album.art ?: R.drawable.logo_grey_sm_en)
            //.crossfade(true)
            .size(cWidth, cHeight)
            .error(R.drawable.logo_grey_sm_en)
            .allowHardware(false) // Disable hardware bitmaps.

            //.transition(PaletteTransition(CrossfadeTransition())) { palette ->
            .transition(PaletteTransition(CrossfadeTransition()) { palette ->
                // Consume the palette.
                // Extract dominant color from the generated palette
//                val dominantColor =
//                    palette.getDominantColor(Color.WHITE) ?: Color.WHITE
                val colors = PaletteUtils.getPaletteColors(palette)
                Utils.albumColors[item.id] = colors

//                // Modify color's alpha channel to make it partly transparent
//                val backgroundColor =
//                    ColorUtils.setAlphaComponent(
//                        colors.toolbarBackgroundColor,
//                        BACKGROUND_TINT_ALPHA
//                    )
            })
            .target(card.mainImageView)
            .build()

        imageLoader.execute(request)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) = Unit

    companion object {

        /** Default height in DP used for card presenters, larger than this results in rows overflowing */
        const val DEFAULT_CARD_HEIGHT: Int = 460

        const val DEFAULT_ASPECT_RATIO: Int = TvContractCompat.PreviewPrograms.ASPECT_RATIO_1_1

        var cWidth = DEFAULT_CARD_HEIGHT
        var cHeight = DEFAULT_CARD_HEIGHT
    }
}
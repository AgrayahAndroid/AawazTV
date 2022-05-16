package com.aawaz.tv.ui.details

import android.view.View
import androidx.leanback.widget.AbstractDetailsDescriptionPresenter
import com.aawaz.tv.R
import com.aawaz.tv.data.db.Album
import com.aawaz.tv.utils.Utils


class DetailsDescriptionPresenter : AbstractDetailsDescriptionPresenter() {

    override fun onBindDescription(
        viewHolder: AbstractDetailsDescriptionPresenter.ViewHolder,
        item: Any
    ) {
        val album = item as Album
        //Timber.d("album details : year: ${album.year} :: ${album.pubDate} :: ${album.genres}")

        viewHolder.apply {
            title.text = album.title
            body.text = album.description
            body.textSize = 15.5F

            if (Utils.getFormattedDate(
                    album!!.pubDate,
                    expectedFormat = "MMMM dd, yyyy",
                    currentFormat = "MMMM dd,yyyy"
                ) == ""){
                subtitle.visibility == View.GONE
            }else {

                subtitle.text = subtitle.context.getString(
                    R.string.transport_title,
                    Utils.getFormattedDate(
                        album!!.pubDate,
                        expectedFormat = "MMMM dd, yyyy",
                        currentFormat = "MMMM dd,yyyy"
                    ),
                    album.categoryName
                )
            }



            val colors = Utils.albumColors[album.id]
            colors?.let {
                title.setTextColor(colors.textColor)
                subtitle.setTextColor(colors.titleColor)
                body.setTextColor(colors.titleColor)
            }
        }
    }
}

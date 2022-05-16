package com.aawaz.tv.presenters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.leanback.widget.Presenter
import com.aawaz.tv.R

/**
 * Created by Rinav Gangar <rinav.dev> on 27/5/20.
 * Agrahyah Technologies Pvt Ltd
 * rinav4all@gmail.com
 */
class GridItemPresenter : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_grid, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val gridItem = item as GridItem

        val title: TextView? = viewHolder.view.findViewById(R.id.item_text)
        title?.text = gridItem.title

        if (gridItem.icon != 0) {
            val icon: ImageView? = viewHolder.view.findViewById(R.id.item_icon)
            icon?.visibility = View.VISIBLE
            icon?.setImageDrawable(ContextCompat.getDrawable(icon.context, gridItem.icon))
        } else {
            (viewHolder.view.findViewById(R.id.item_icon) as ImageView?)?.visibility = View.GONE
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {

    }

    data class GridItem(val id: Int, val title: String, val icon: Int, val url: String)
}
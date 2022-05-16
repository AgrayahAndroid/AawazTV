package com.aawaz.tv.utils

import android.content.Context
import android.widget.ImageView
import coil.Coil
import coil.request.LoadRequest
import coil.transform.RoundedCornersTransformation
import com.aawaz.tv.R
import com.rollbar.android.Rollbar
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by Rinav Gangar <rinav.dev> on 2/5/20.
 * Agrahyah Technologies Pvt Ltd
 * rinav4all@gmail.com
 */

object Utils {

    var albumColors: HashMap<String, PaletteColors> = hashMapOf()
    var episodeColors: HashMap<String, PaletteColors> = hashMapOf()
    //var colorMap: HashMap<String, ExtractedColors> = hashMapOf()

    fun getFormattedDate(
        date: String?, currentFormat: String = "MMMM dd,yyyy", expectedFormat: String = "yyyy-MM-dd"
    ): String? {
        // Validating if the supplied parameters is null
        //Timber.d("FORMATTEDDATE: 1 $date")
        if (date == null) {
            return null
        }
        try {
            // Create SimpleDateFormat object with source string date format
            val sourceDateFormat = SimpleDateFormat(currentFormat, Locale.getDefault())
            // Parse the string into Date object
            val dateObj = sourceDateFormat.parse(date)
            // Create SimpleDateFormat object with desired date format
            val desiredDateFormat =
                SimpleDateFormat(expectedFormat, Locale.getDefault())
            return if (dateObj == null) {
                null
            } else {
                // Parse the date into another format
                return desiredDateFormat.format(dateObj).toString()
                //.apply {
                //Timber.d("FORMATTEDDATE: 2 $this")
                //}
            }
        } catch (e: ParseException) {
            Rollbar.instance().error(e)
            Timber.e("Unable to parse date for $date")
            return ""
        }
    }

    fun getCurrentTimeStamp(): String =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
            .format(Date())
}

fun ImageView.loadImage(context: Context, url: String?, width: Int, height: Int) {

    val imageLoader = Coil.imageLoader(context)

    val request = LoadRequest.Builder(context)
        .data(url ?: R.drawable.logo_grey_sm_en)
        .crossfade(true)
        .size(width, height)
        .error(R.drawable.logo_grey_sm_en)
        .target(
            onSuccess = { setImageDrawable(it) },
            onError = { R.drawable.logo_grey_sm_en }
        )
        .transformations(RoundedCornersTransformation(10F))
        .build()

    imageLoader.execute(request)
}
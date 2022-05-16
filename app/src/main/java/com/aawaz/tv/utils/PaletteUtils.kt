package com.aawaz.tv.utils

import android.graphics.Color
import androidx.palette.graphics.Palette

/**
 * Created by Rinav Gangar <rinav.dev> on 21/6/20.
 * Agrahyah Technologies Pvt Ltd
 * rinav4all@gmail.com
</rinav.dev> */
/**
 * Created by [Marcus Gabilheri](mailto:marcus@gabilheri.com)
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 10/8/16.
 */
object PaletteUtils {

    fun getPaletteColors(palette: Palette): PaletteColors {
        val colors = PaletteColors()

        //figuring out toolbar palette color in order of preference
        if (palette.darkVibrantSwatch != null) {
            colors.toolbarBackgroundColor = (palette.darkVibrantSwatch!!.rgb)
            colors.textColor = (palette.darkVibrantSwatch!!.bodyTextColor)
            colors.titleColor = (palette.darkVibrantSwatch!!.titleTextColor)
        } else if (palette.darkMutedSwatch != null) {
            colors.toolbarBackgroundColor = (palette.darkMutedSwatch!!.rgb)
            colors.textColor = (palette.darkMutedSwatch!!.bodyTextColor)
            colors.titleColor = (palette.darkMutedSwatch!!.titleTextColor)
        } else if (palette.vibrantSwatch != null) {
            colors.toolbarBackgroundColor = (palette.vibrantSwatch!!.rgb)
            colors.textColor = (palette.vibrantSwatch!!.bodyTextColor)
            colors.titleColor = (palette.vibrantSwatch!!.titleTextColor)
        }

        //set the status bar color to be a darker version of the toolbar background Color;
        if (colors.toolbarBackgroundColor != 0) {
            val hsv = FloatArray(3)
            val color = colors.toolbarBackgroundColor
            Color.colorToHSV(color, hsv)
            hsv[2] *= 0.8f // value component
            colors.statusBarColor = (Color.HSVToColor(hsv))
        }
        return colors
    }
}
package com.aawaz.tv.utils

import android.util.Rational
import androidx.tvprovider.media.tv.TvContractCompat

/**
 * Created by Rinav Gangar <rinav.dev> on 30/4/20.
 * Agrahyah Technologies Pvt Ltd
 * rinav4all@gmail.com
 */
class TvLauncherUtils private constructor() {

    companion object {

        /**
         * Parse an aspect ratio constant into the equivalent rational number. For example,
         * [TvContractCompat.PreviewPrograms.ASPECT_RATIO_16_9] becomes `Rational(16, 9)`. The
         * constant must be one of ASPECT_RATIO_* in [TvContractCompat.PreviewPrograms].
         */
        fun parseAspectRatio(ratioConstant: Int): Rational = when (ratioConstant) {
            TvContractCompat.PreviewPrograms.ASPECT_RATIO_16_9 -> Rational(16, 9)
            TvContractCompat.PreviewPrograms.ASPECT_RATIO_1_1 -> Rational(1, 1)
            TvContractCompat.PreviewPrograms.ASPECT_RATIO_2_3 -> Rational(2, 3)
            TvContractCompat.PreviewPrograms.ASPECT_RATIO_3_2 -> Rational(3, 2)
            TvContractCompat.PreviewPrograms.ASPECT_RATIO_4_3 -> Rational(4, 3)
            TvContractCompat.PreviewPrograms.ASPECT_RATIO_MOVIE_POSTER -> Rational(1000, 1441)
            else -> throw IllegalArgumentException(
                "Constant must be one of ASPECT_RATIO_* in TvContractCompat.PreviewPrograms"
            )
        }
    }
}
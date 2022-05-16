package com.aawaz.tv.utils

import android.graphics.drawable.BitmapDrawable
import androidx.palette.graphics.Palette
import coil.annotation.ExperimentalCoilApi
import coil.request.RequestResult
import coil.request.SuccessResult
import coil.transition.Transition
import coil.transition.TransitionTarget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Rinav Gangar <rinav.dev> on 2/5/20.
 * Agrahyah Technologies Pvt Ltd
 * rinav4all@gmail.com
 */

@ExperimentalCoilApi
class PaletteTransition(
    private val delegate: Transition?,
    private val onGenerated: (Palette) -> Unit
) : Transition {

    override suspend fun transition(
        target: TransitionTarget<*>,
        result: RequestResult
    ) {
        // Execute the delegate transition.
        val delegateJob = delegate?.let { delegate ->
            coroutineScope {
                launch(Dispatchers.Main.immediate) {
                    delegate.transition(target, result)
                }
            }
        }

        // Compute the palette on a background thread.
        if (result is SuccessResult) {
            val bitmap = (result.drawable as BitmapDrawable).bitmap
            val palette = withContext(Dispatchers.IO) {
                Palette.Builder(bitmap).generate()
            }
            onGenerated(palette)
        }

        delegateJob?.join()
    }
}
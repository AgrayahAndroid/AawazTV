package com.aawaz.tv.ui.exit

import android.os.Bundle
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist.Guidance
import androidx.leanback.widget.GuidedAction
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.aawaz.tv.R
import com.aawaz.tv.utils.C

/**
 * Created by Rinav Gangar <rinav.dev> on 28/5/20.
 * Agrahyah Technologies Pvt Ltd
 * rinav4all@gmail.com
 */
class ExitFragment : GuidedStepSupportFragment() {

    private val args: ExitFragmentArgs by navArgs()

    companion object {
        private const val ACTION_ID_POSITIVE = 1L
        private const val ACTION_ID_NEGATIVE = ACTION_ID_POSITIVE + 1L
    }

    override fun onCreateGuidance(savedInstanceState: Bundle?): Guidance {

        //Timber.d("ExitFragment args: ${args.exitType}")

        return when (args.exitType) {

            C.EXIT_EXO_PLAYER -> {

                Guidance(
                    getString(R.string.dialog_exit_exo_title),
                    getString(R.string.dialog_exit_exo_description),
                    "",
                    null
                    //ContextCompat.getDrawable(requireContext(), R.drawable.aawaz_banner_android_tv)
                )
            }

            C.EXIT_SEARCH_FRAGMENT -> {

                Guidance(
                    getString(R.string.dialog_exit_description),
                    getString(R.string.dialog_exit_search_continue),
                    "",
                    null
                    //ContextCompat.getDrawable(requireContext(), R.drawable.aawaz_banner_android_tv)
                )
            }

            C.EXIT_APP -> {

                Guidance(
                    getString(R.string.dialog_exit_title),
                    getString(R.string.dialog_exit_description),
                    "",
                    null
                    //ContextCompat.getDrawable(requireContext(), R.drawable.aawaz_banner_android_tv)
                )
            }

            else -> {

                Guidance(
                    getString(R.string.dialog_exit_title),
                    getString(R.string.dialog_exit_description),
                    "",
                    null
                    //ContextCompat.getDrawable(requireContext(), R.drawable.aawaz_banner_android_tv)
                )
            }
        }
    }

    override fun onCreateActions(
        actions: MutableList<GuidedAction?>,
        savedInstanceState: Bundle?
    ) {

        val positiveTitle = when (args.exitType) {

            C.EXIT_EXO_PLAYER -> {
                getString(R.string.dialog_exit_exo_button_positive)
            }

            C.EXIT_SEARCH_FRAGMENT -> {
                getString(R.string.dialog_exit_button_positive)
            }

            C.EXIT_APP -> {
                getString(R.string.dialog_exit_button_positive)
            }
            else -> {
                getString(R.string.dialog_exit_button_positive)
            }
        }

        var action = GuidedAction.Builder(requireContext())
            .id(ACTION_ID_POSITIVE)
            .title(positiveTitle).build()
        actions.add(action)
        action.isFocusable = true

        val negativeTitle = when (args.exitType) {

            C.EXIT_EXO_PLAYER -> {
                getString(R.string.dialog_exit_exo_button_negative)
            }

            C.EXIT_SEARCH_FRAGMENT -> {
                getString(R.string.dialog_exit_button_negative)
            }

            C.EXIT_APP -> {
                getString(R.string.dialog_exit_button_negative)
            }
            else -> {
                getString(R.string.dialog_exit_button_negative)
            }
        }

        action = GuidedAction.Builder(requireContext())
            .id(ACTION_ID_NEGATIVE)
            .title(negativeTitle).build()
        actions.add(action)
    }

    override fun onGuidedActionClicked(action: GuidedAction) {


        when (args.exitType) {

            C.EXIT_EXO_PLAYER -> {
                handleExoPlayerExit(action)
            }

            C.EXIT_SEARCH_FRAGMENT -> {
                handleSearchExit(action)
            }

            C.EXIT_APP -> {
                handleAppExit(action)
            }

            else -> {
                findNavController().popBackStack()
            }
        }
    }


    private fun handleExoPlayerExit(action: GuidedAction) {
        //Timber.d("Exit: handleExoPlayerExit")

        when (action.id) {
            ACTION_ID_POSITIVE -> {
                findNavController().popBackStack()
            }

            ACTION_ID_NEGATIVE -> {
                //findNavController().popBackStack(R.id.media_browser_fragment, true)
                // popback stack twice, instead of specifying action id
                findNavController().popBackStack(R.id.albumDetailsFragment, false)
                //findNavController().popBackStack()
            }
        }
    }

    private fun handleSearchExit(action: GuidedAction) {
        //Timber.d("Exit: handleExoPlayerExit")

        when (action.id) {
            ACTION_ID_POSITIVE -> {
                //findNavController().popBackStack(R.id.media_browser_fragment, true)
                // popback stack twice, instead of specifying action id
                findNavController().popBackStack(R.id.media_browser_fragment, false)
                //findNavController().popBackStack()

            }

            ACTION_ID_NEGATIVE -> {
                findNavController().popBackStack()
            }
        }
    }


    private fun handleAppExit(action: GuidedAction) {
        //Timber.d("Exit: handleAppExit")

        when (action.id) {
            ACTION_ID_POSITIVE -> {
                requireActivity().finish()
            }

            ACTION_ID_NEGATIVE -> {
                findNavController().popBackStack()
            }
        }
    }


}
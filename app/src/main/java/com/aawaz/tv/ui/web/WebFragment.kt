package com.aawaz.tv.ui.web

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs

/**
 * Created by Rinav Gangar <rinav.dev> on 27/5/20.
 * Agrahyah Technologies Pvt Ltd
 * rinav4all@gmail.com
 */
class WebFragment : Fragment() {

    private val args: WebFragmentArgs by navArgs()
    private var webView: WebView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = FrameLayout(requireContext())
        val lp = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
//        lp.marginStart = 32

        webView = WebView(requireContext())
        webView?.apply {
            webViewClient = WebViewClient()
            settings?.apply {
                loadWithOverviewMode = true
                useWideViewPort = true
                javaScriptEnabled = true
                loadsImagesAutomatically = true
                domStorageEnabled = true
                /*setAppCacheEnabled(true)
                databaseEnabled = true*/

            }
        }






        root.addView(webView, lp)

        return root
    }

    override fun onResume() {
        super.onResume()
        //Timber.d("ARGS: URL: ${args.url}")
        webView?.loadUrl(args.url)
    }
}
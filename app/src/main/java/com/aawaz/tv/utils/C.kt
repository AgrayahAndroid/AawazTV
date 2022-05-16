package com.aawaz.tv.utils

import com.aawaz.tv.R
import com.aawaz.tv.presenters.GridItemPresenter

/**
 * Created by Rinav Gangar <rinav.dev> on 15/6/20.
 * Agrahyah Technologies Pvt Ltd
 * rinav4all@gmail.com
 */
object C {
    const val DATABASE_NAME = "aawaz-tv.db"

    const val HEADER_LEGAL = "Legal"

//    private const val TNC_URL = "https://www.aawaz.com/policy/terms-of-use"
    private const val TNC_URL = "https://www.aawaz.com/policy/terms-of-use"
    private const val PRIVACY_POLICY_URL = "https://www.aawaz.com/policy/privacy-policy"
    private const val DISCLAIMER_URL = "https://www.aawaz.com/policy/disclaimer"

    const val TNC_ID = 0
    const val PRIVACY_POLICY_ID = 1
    const val DISCLAIMER_ID = 2

    val ITEM_TNC = GridItemPresenter.GridItem(TNC_ID, "Terms of Use", R.drawable.ic_tnc, TNC_URL)
    val ITEM_PRIVACY_POLICY = GridItemPresenter.GridItem(
        PRIVACY_POLICY_ID,
        "Privacy Policy",
        R.drawable.ic_baseline_privacy_tip_24,
        PRIVACY_POLICY_URL
    )
    val ITEM_DISCLAIMER = GridItemPresenter.GridItem(DISCLAIMER_ID, "Disclaimer", R.drawable.ic_baseline_info_24, DISCLAIMER_URL)

    const val EXIT_APP = 9
    const val EXIT_EXO_PLAYER = 18
    const val EXIT_SEARCH_FRAGMENT = 28
}
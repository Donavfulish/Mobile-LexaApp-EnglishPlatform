package com.home.lexa.ui.auth

import android.net.Uri
import com.home.lexa.BuildConfig

object GoogleUrls {
    const val BASE_URL = "${BuildConfig.SERVER_BASE_URL}api/auth"
    const val REDIRECT_URI = "lexa://auth-success"

    val loginUri: Uri
        get() = Uri.parse("$BASE_URL/google/login?redirectUrl=$REDIRECT_URI")
}
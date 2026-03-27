package com.home.lexa.ui.auth

import android.net.Uri

object GoogleUrls {
    const val BASE_URL = "http://10.0.2.2:8081/api/auth"
    const val REDIRECT_URI = "lexa://auth-success"

    val loginUri: Uri
        get() = Uri.parse("$BASE_URL/google/login?redirectUrl=$REDIRECT_URI")
}
// File: app/src/main/java/com/home/lexa/core/network/AuthInterceptor.kt
package com.home.lexa.core.network

import com.home.lexa.data.local.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        // Lấy token từ local storage
        val token = tokenManager.getToken()
        println("DEBUG_TOKEN: Current token is -> $token")

        // Nếu có token, gắn vào header
        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}
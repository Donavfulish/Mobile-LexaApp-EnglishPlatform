// File: app/src/main/java/com/home/lexa/data/local/TokenManager.kt
package com.home.lexa.data.local

import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {
    /*
    SharedPreferences: Class lưu trữ cục bộ
    context.getSharedPreferences("lexa_prefs", Context.MODE_PRIVATE): Tạo một SharedReferences riêng tên "lexa_prefs" và dùng MODE_PRIVATE
     */
    private val prefs: SharedPreferences = context.getSharedPreferences("lexa_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
    }

    fun saveAccessToken(token: String) {
        prefs.edit().putString(KEY_ACCESS_TOKEN, token).apply()
    }

    fun saveRefreshToken(token: String) {
        prefs.edit().putString(KEY_REFRESH_TOKEN, token).apply()
    }

    fun saveTokens(accessToken: String, refreshToken: String) {
        prefs.edit().apply {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            apply()
        }
    }

    fun getAccessToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }

    fun getRefreshToken(): String? {
        return prefs.getString(KEY_REFRESH_TOKEN, null)
    }

    fun clearTokens() {
        prefs.edit().clear().apply()
    }
}
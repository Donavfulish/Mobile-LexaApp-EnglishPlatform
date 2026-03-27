// File: app/src/main/java/com/home/lexa/data/local/TokenManager.kt
package com.home.lexa.data.local

import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("lexa_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString("ACCESS_TOKEN", token).apply()
    }

    fun getToken(): String? {
//        val myRealToken = ""
//        return myRealToken
        return prefs.getString("ACCESS_TOKEN", null)
    }

    fun clearToken() {
        prefs.edit().remove("ACCESS_TOKEN").apply()
    }
}
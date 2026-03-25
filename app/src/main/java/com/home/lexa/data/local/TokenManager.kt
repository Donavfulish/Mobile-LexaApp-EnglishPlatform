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
        val myRealToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJVc2VySW5mbyhpZD04LCBlbWFpbD1oZ2F1MjNAY2xjLmZpdHVzLmVkdS52biwgbmFtZT1IdeG7s25oIEdpYSBC4buLcCwgcm9sZT1URUFDSEVSLCBwYXNzd29yZEhhc2g9JDJhJDEwJGZuVm4zeDd1RThHZExwN3RPd3hrMS4yWnVNeGppaWRiSExmSG13MFVoZjIuczBzeWpPa0ZhKSIsImlzcyI6Imh0dHA6Ly8wLjAuMC4wOjgwODAiLCJpZCI6OCwidHlwZSI6ImFjY2VzcyIsImV4cCI6MTc3NDI3OTMxNH0._Tz60z7wD-X5A5PSqOxWWyuc0eSHe7tcqVPlVQKHoP8"
        return myRealToken
        //return prefs.getString("ACCESS_TOKEN", null)
    }

    fun clearToken() {
        prefs.edit().remove("ACCESS_TOKEN").apply()
    }
}
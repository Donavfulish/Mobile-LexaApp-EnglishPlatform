package com.home.lexa.data.local

import android.content.Context
import com.home.lexa.domain.models.UserInfo
import com.home.lexa.domain.models.UserRole

class UserManager(context: Context) {

    private val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun saveUser(user: UserInfo) {
        prefs.edit()
            .putInt("user_id", user.id)
            .putString("user_email", user.email)
            .putString("user_name", user.name)
            .putString("user_role", user.role.name)
            .apply()
    }

    fun getUserId(): Int = prefs.getInt("user_id", -1)

    fun getUserRole(): UserRole? {
        val role = prefs.getString("user_role", null)
        return role?.let { UserRole.valueOf(it) }
    }

    fun getUserName(): String? = prefs.getString("user_name", null)

    fun clearUser() {
        prefs.edit().clear().apply()
    }
}
package com.home.lexa.data.local

import android.content.Context
import com.home.lexa.domain.models.UserInfo
import com.home.lexa.domain.models.UserRole
import okhttp3.internal.userAgent

object PreferenceKeys {
    const val USER_ID = "user_id"
    const val USER_EMAIL = "user_email"
    const val USER_NAME = "user_name"
    const val USER_ROLE = "user_role"
    const val IS_EMAIL_VERIFIED = "is_email_verified"
}

class UserManager(context: Context) {

    private val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun saveUser(user: UserInfo) {
        prefs.edit()
            .putInt(PreferenceKeys.USER_ID, user.id)
            .putString(PreferenceKeys.USER_EMAIL, user.email)
            .putString(PreferenceKeys.USER_NAME, user.name)
            .putString(PreferenceKeys.USER_ROLE, user.role.name)
            .putBoolean(PreferenceKeys.IS_EMAIL_VERIFIED, user.isEmailVerified ?: false)
            .apply()
    }

    fun getUserId(): Int = prefs.getInt(PreferenceKeys.USER_ID, -1)

    fun getUserRole(): UserRole? {
        val role = prefs.getString(PreferenceKeys.USER_ROLE, null)
        return role?.let { UserRole.valueOf(it) }
    }

    fun getUserName(): String? = prefs.getString(PreferenceKeys.USER_NAME, null)

    fun isEmailVerified(): Boolean = prefs.getBoolean(PreferenceKeys.IS_EMAIL_VERIFIED, false)

    fun commitEmailVerified() = prefs.edit()
        .putBoolean(PreferenceKeys.IS_EMAIL_VERIFIED, true)
        .apply()

    fun clearUser() {
        prefs.edit().clear().apply()
    }
}
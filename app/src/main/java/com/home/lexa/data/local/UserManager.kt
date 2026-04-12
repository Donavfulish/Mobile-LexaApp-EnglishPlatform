package com.home.lexa.data.local

import android.content.Context
import com.home.lexa.domain.models.UserInfo
import com.home.lexa.domain.models.UserRole
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object PreferenceKeys {
    const val USER_ID = "user_id"
    const val USER_EMAIL = "user_email"
    const val USER_NAME = "user_name"
    const val USER_ROLE = "user_role"
    const val IS_EMAIL_VERIFIED = "is_email_verified"
    const val STREAK_COUNT = "streak_count"
    const val LAST_ACTIVE_DATE = "last_active_date"
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

    fun updateUserName(name: String) {
        prefs.edit()
            .putString(PreferenceKeys.USER_NAME, name)
            .apply()
    }


    fun updateStreak() {
        val today = getTodayString()
        val lastDate = prefs.getString(PreferenceKeys.LAST_ACTIVE_DATE, "")
        var currentStreak = prefs.getInt(PreferenceKeys.STREAK_COUNT, 0)

        if (lastDate == today) {
            return
        }

        if (lastDate == getYesterdayString()) {
            currentStreak++
        } else {
            currentStreak = 1
        }

        prefs.edit()
            .putInt(PreferenceKeys.STREAK_COUNT, currentStreak)
            .putString(PreferenceKeys.LAST_ACTIVE_DATE, today)
            .apply()
    }

    fun getStreakCount(): Int = prefs.getInt(PreferenceKeys.STREAK_COUNT, 0)

    private fun getTodayString(): String {
        return SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
    }

    private fun getYesterdayString(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, -1)
        return SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(cal.time)
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
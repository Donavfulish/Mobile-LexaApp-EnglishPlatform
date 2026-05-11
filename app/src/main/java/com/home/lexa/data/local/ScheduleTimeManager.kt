package com.home.lexa.data.local

import android.content.Context
import android.content.SharedPreferences

class ScheduleTimeManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("schedule_time_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_ON_STREAK_REMINDER = "is_on_streak"
        private const val KEY_IS_ON_TIME_REMINDER = "is_on_time"
        private const val KEY_STUDY_HOUR = "study_hour"
        private const val KEY_STUDY_MINUTE = "study_minute"

        private const val KEY_STUDY_DAYS = "study_days"
    }

    fun saveStateStreak(isOn: Boolean) {
        prefs.edit().putBoolean(KEY_IS_ON_STREAK_REMINDER, isOn).apply()

    }

    fun saveStateTime(isOn: Boolean) {
        prefs.edit().putBoolean(KEY_IS_ON_TIME_REMINDER, isOn).apply()
    }


    fun saveScheduleTime(hour: Int, minute: Int) {
        prefs.edit().putInt(KEY_STUDY_HOUR, hour).apply()
        prefs.edit().putInt(KEY_STUDY_MINUTE, minute).apply()
    }

    fun saveScheduleDays(days: Set<Int>) {

        val stringSet = days.map { it.toString() }.toSet()
        prefs.edit().putStringSet(KEY_STUDY_DAYS, stringSet).apply()
    }
    fun getStateStreak( ): Boolean {
        return prefs.getBoolean(KEY_IS_ON_STREAK_REMINDER, false)
    }

    fun getStateTime( ): Boolean {
        return prefs.getBoolean(KEY_IS_ON_TIME_REMINDER, false)
    }


    fun getHourScheduleTime():Int {
        return prefs.getInt(KEY_STUDY_HOUR, 0)
    }
    fun getMinuteScheduleTime():Int {
        return prefs.getInt(KEY_STUDY_MINUTE, 0)
    }
    fun getScheduleDays(): Set<Int> {
        //1=CN, 2=T2, 3=T3, 4=T4, 5=T5, 6=T6, 7=T7
        val defaultDays = setOf("1", "2", "3", "4", "5", "6", "7")

        val stringSet = prefs.getStringSet(KEY_STUDY_DAYS, defaultDays)

        return stringSet?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()
    }

}
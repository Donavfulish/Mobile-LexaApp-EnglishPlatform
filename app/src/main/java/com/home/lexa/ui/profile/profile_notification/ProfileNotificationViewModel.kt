package com.home.lexa.ui.profile.profile_notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.home.lexa.data.local.ScheduleTimeManager

class ProfileNotificationViewModel(
    private val timeManager: ScheduleTimeManager
):
    ViewModel() {

    private val _streakToggleState = MutableLiveData<Boolean>()
    val streakToggleState: LiveData<Boolean> get() = _streakToggleState


    private val _timeToggleState = MutableLiveData<Boolean>()
    val timeToggleState: LiveData<Boolean> get() = _timeToggleState


    private val _scheduleTime = MutableLiveData<Pair<Int, Int>>()
    val scheduleTime: LiveData<Pair<Int, Int>> get() = _scheduleTime
    private val _scheduleDays = MutableLiveData<Set<Int>>()
    val scheduleDays: LiveData<Set<Int>> get() = _scheduleDays
    init {

        loadSavedData()
    }

    private fun loadSavedData() {
        _streakToggleState.value = timeManager.getStateStreak()
        _timeToggleState.value = timeManager.getStateTime()

        val hour = timeManager.getHourScheduleTime()
        val minute = timeManager.getMinuteScheduleTime()
        _scheduleTime.value = Pair(hour, minute)
        _scheduleDays.value = timeManager.getScheduleDays()
    }



    fun onStreakToggled(isOn: Boolean) {
        timeManager.saveStateStreak(isOn)
        _streakToggleState.value = isOn
    }

    fun onTimeToggled(isOn: Boolean) {
        timeManager.saveStateTime(isOn)
        _timeToggleState.value = isOn
    }

    fun saveScheduleTime(hour: Int, minute: Int) {
        timeManager.saveScheduleTime(hour, minute)
        _scheduleTime.value = Pair(hour, minute)
    }
    fun saveScheduleDays(days: Set<Int>) {
        timeManager.saveScheduleDays(days)
        _scheduleDays.value = days
    }
}


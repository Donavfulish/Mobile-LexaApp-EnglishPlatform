package com.home.lexa.ui.components

import android.app.TimePickerDialog
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import com.home.lexa.R
import com.home.lexa.databinding.ViewReminderSettingBinding
import java.util.Calendar

class ReminderSettingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    private val binding: ViewReminderSettingBinding
    private var selectedHour = 19
    private var selectedMinute = 0
    private val selectedDays = mutableSetOf<Int>() // 1 (Sun) to 7 (Sat) or mapping as per UI

    private var onTimeChangedListener: ((hour: Int, minute: Int) -> Unit)? = null
    private var onDaysChangedListener: ((days: Set<Int>) -> Unit)? = null

    init {
        binding = ViewReminderSettingBinding.inflate(LayoutInflater.from(context), this, true)
        setupTimePickers()
        setupDaySelectors()
        updateTimeUI()
        
        // Default selection from image (T2 to T6)
        (2..6).forEach { toggleDay(it) }
        
        cardElevation = 4f
        strokeWidth = 0
    }

    private fun setupTimePickers() {
        val timeClickListener = {
            if (isEnabled) {
                val calendar = Calendar.getInstance()
                TimePickerDialog(context, { _, hourOfDay, minute ->
                    selectedHour = hourOfDay
                    selectedMinute = minute
                    updateTimeUI()
                    onTimeChangedListener?.invoke(selectedHour, selectedMinute)
                }, selectedHour, selectedMinute, true).show()
            }
        }

        binding.tvHour.setOnClickListener { timeClickListener() }
        binding.tvMinute.setOnClickListener { timeClickListener() }
    }

    private fun updateTimeUI() {
        binding.tvHour.text = String.format("%02d", selectedHour)
        binding.tvMinute.text = String.format("%02d", selectedMinute)
    }

    private fun setupDaySelectors() {
        val dayViews = listOf(
            binding.tvDay2 to 2,
            binding.tvDay3 to 3,
            binding.tvDay4 to 4,
            binding.tvDay5 to 5,
            binding.tvDay6 to 6,
            binding.tvDay7 to 7,
            binding.tvDay8 to 1
        )

        dayViews.forEach { (view, dayIndex) ->
            view.setOnClickListener {
                if (isEnabled) {
                    toggleDay(dayIndex)
                    onDaysChangedListener?.invoke(selectedDays)
                }
            }
        }
    }

    private fun toggleDay(dayIndex: Int) {
        if (selectedDays.contains(dayIndex)) {
            selectedDays.remove(dayIndex)
        } else {
            selectedDays.add(dayIndex)
        }
        updateDayUI(dayIndex)
    }

    private fun updateDayUI(dayIndex: Int) {
        val view = when (dayIndex) {
            2 -> binding.tvDay2
            3 -> binding.tvDay3
            4 -> binding.tvDay4
            5 -> binding.tvDay5
            6 -> binding.tvDay6
            7 -> binding.tvDay7
            1 -> binding.tvDay8
            else -> return
        }

        val isSelected = selectedDays.contains(dayIndex)
        val isViewEnabled = isEnabled

        if (isSelected) {
            view.background = ContextCompat.getDrawable(context, R.drawable.bg_day_circle_selected)
            view.setTextColor(ContextCompat.getColor(context, R.color.white))
            view.alpha = if (isViewEnabled) 1.0f else 0.5f
        } else {
            view.background = ContextCompat.getDrawable(context, R.drawable.bg_day_circle_unselected)
            view.setTextColor(ContextCompat.getColor(context, R.color.gray_888888))
            view.alpha = if (isViewEnabled) 1.0f else 0.5f
        }
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        alpha = if (enabled) 1.0f else 0.5f
        // Refresh all day UIs to apply alpha
        (1..7).forEach { updateDayUI(it) }
        
        binding.tvHour.alpha = if (enabled) 1.0f else 0.5f
        binding.tvMinute.alpha = if (enabled) 1.0f else 0.5f
        binding.tvHour.isClickable = enabled
        binding.tvMinute.isClickable = enabled
    }

    fun setTime(hour: Int, minute: Int) {
        selectedHour = hour
        selectedMinute = minute
        updateTimeUI()
    }

    fun setSelectedDays(days: Set<Int>) {
        selectedDays.clear()
        selectedDays.addAll(days)
        (1..7).forEach { updateDayUI(it) }
    }

    fun getSelectedTime(): Pair<Int, Int> = selectedHour to selectedMinute
    fun getSelectedDays(): Set<Int> = selectedDays

    fun setOnTimeChangedListener(listener: (Int, Int) -> Unit) {
        onTimeChangedListener = listener
    }

    fun setOnDaysChangedListener(listener: (Set<Int>) -> Unit) {
        onDaysChangedListener = listener
    }
}
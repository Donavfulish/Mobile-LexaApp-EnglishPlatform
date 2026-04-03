package com.home.lexa.ui.profile.profile_notification

import android.graphics.Color
import android.view.View
import com.home.lexa.MainActivity
import com.home.lexa.R
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.databinding.FragmentProfileNotificationBinding

class ProfileNotificationFragment : BaseFragment<FragmentProfileNotificationBinding>(FragmentProfileNotificationBinding::inflate) {

    override fun setupViews() {

        val activityBinding = (requireActivity() as MainActivity).binding
        activityBinding.appBarLayout.apply {
            removeCustomView()
            setText("Thông báo")
            setBackButtonVisible(true)
        }

        binding.notification.apply {
            setTitle("Thông báo")
            setDescription(null)
            setIcon(R.drawable.ic_notification, Color.parseColor("#636AE8"))
            setToggleState(false)
        }

        binding.streakNotification.apply {
            setTitle("Thông báo chuỗi")
            setDescription("Nhắc nhở bạn duy trì chuỗi học tập hàng ngày để không bỏ lỡ tiến độ.")
            setIcon(R.drawable.ic_fire_outline, Color.parseColor("#FF9800"))
            setToggleState(false)
        }

        binding.studyHourNotification.apply {
            setTitle("Thông báo giờ học")
            setDescription("Thông báo nhắc nhở trước khi đến giờ học đã cài đặt của bạn.")
            setIcon(R.drawable.ic_book, Color.parseColor("#4285F4"))
            
            // Initial state: hide or disable ReminderSettingView based on toggle
            val isChecked = false 
            setToggleState(isChecked)
            updateReminderVisibility(isChecked)

            setOnToggleChangeListener { isOn ->
                updateReminderVisibility(isOn)
            }
        }
    }

    private fun updateReminderVisibility(isVisible: Boolean) {
        binding.reminderSetting.apply {
            visibility = if (isVisible) View.VISIBLE else View.GONE
            // Optional: If you want it visible but "grayed out", use setEnabled(isVisible) instead of visibility
            // setEnabled(isVisible)
        }
    }

    override fun observeData() {

    }
}
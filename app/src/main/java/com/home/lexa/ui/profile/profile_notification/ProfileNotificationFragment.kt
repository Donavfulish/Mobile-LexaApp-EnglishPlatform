package com.home.lexa.ui.profile.profile_notification

import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.home.lexa.MainActivity
import com.home.lexa.R
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.databinding.FragmentProfileNotificationBinding
import com.home.lexa.ui.profile.profile.ProfileViewModel
import com.home.lexa.ui.utils.ScheduleNotificationUtils
import com.home.lexa.ui.utils.ScheduleNotificationUtils.cancelNotification
import com.home.lexa.ui.utils.ScheduleNotificationUtils.scheduleNotification
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Calendar
import kotlin.getValue

class ProfileNotificationFragment : BaseFragment<FragmentProfileNotificationBinding>(FragmentProfileNotificationBinding::inflate) {


    private val viewModel: ProfileNotificationViewModel by viewModel()

    override fun setupViews() {

        val activityBinding = (requireActivity() as MainActivity).binding
        activityBinding.appBarLayout.apply {
            removeCustomView()
            setText("Thông báo")
            setBackButtonVisible(true)
        }



        binding.streakNotification.apply {
            setTitle("Thông báo chuỗi")
            setDescription("Nhắc nhở bạn duy trì chuỗi học tập hàng ngày để không bỏ lỡ tiến độ.")
            setIcon(R.drawable.ic_fire_outline, ContextCompat.getColor(requireContext(), R.color.c_ff9800))
            setIconBackgroundTint(R.color.c_fff4e5)

            setOnToggleChangeListener { isOn ->
                viewModel.onStreakToggled(isOn)
                if (isOn) {

                    val everyday = (1..7).toSet()

                    ScheduleNotificationUtils.scheduleNotification(
                        context = requireContext(),
                        hour = 19,
                        minute = 0,
                        selectedDays = everyday,
                        title = "Lexa",
                        message = context.getString(R.string.notification_streak),
                        requestCode = ScheduleNotificationUtils.REQ_CODE_STREAK
                    )
                } else {
                    ScheduleNotificationUtils.cancelNotification(
                        requireContext(),
                        ScheduleNotificationUtils.REQ_CODE_STREAK
                    )
                }
            }
        }
        binding.studyHourNotification.apply {
            setTitle("Thông báo giờ học")
            setDescription("Thông báo nhắc nhở trước khi đến giờ học đã cài đặt của bạn.")
            setIcon(R.drawable.ic_book, ContextCompat.getColor(requireContext(), R.color.c_4285f4))

            setOnToggleChangeListener { isOn ->

                viewModel.onTimeToggled(isOn)

                updateReminderVisibility(isOn)

                if (isOn) {
                    val (hour, minute) = binding.reminderSetting.getSelectedTime()
                    val selectedDays = binding.reminderSetting.getSelectedDays();
                    scheduleDailyReminder(hour, minute,selectedDays)
                }else{
                    cancelNotification(
                        requireContext(),
                        ScheduleNotificationUtils.REQ_CODE_STUDY_HOUR
                    )
                }
            }
        }
        binding.reminderSetting.setOnTimeChangedListener { hour, minute ->

            viewModel.saveScheduleTime(hour, minute)


            if (viewModel.timeToggleState.value == true) {
                val selectedDays = binding.reminderSetting.getSelectedDays().toSet();

                scheduleDailyReminder(hour, minute,selectedDays)
                Toast.makeText(requireContext(), "Đã đặt lịch vào $hour:$minute", Toast.LENGTH_SHORT).show()
            }
        }
        binding.reminderSetting.setOnDaysChangedListener { days ->

            val safeDaysCopy = days.toSet()

            viewModel.saveScheduleDays(safeDaysCopy)


            if (viewModel.timeToggleState.value == true) {
                val (hour, minute) = binding.reminderSetting.getSelectedTime()

                scheduleDailyReminder(hour, minute,safeDaysCopy)
                Toast.makeText(requireContext(), "Đã đặt lịch vào $hour:$minute", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateReminderVisibility(isVisible: Boolean) {
        binding.reminderSetting.apply {
            visibility = if (isVisible) View.VISIBLE else View.GONE
        }
    }

    override fun observeData() {

        viewModel.streakToggleState.observe(viewLifecycleOwner) { isOn ->
            binding.streakNotification.setToggleState(isOn)
        }


        viewModel.timeToggleState.observe(viewLifecycleOwner) { isOn ->

            binding.studyHourNotification.setToggleState(isOn)
            updateReminderVisibility(isOn)
        }
        viewModel.scheduleTime.observe(viewLifecycleOwner) { timePair ->
            val hour = timePair.first
            val minute = timePair.second
            binding.reminderSetting.setTime(hour, minute)
        }
        viewModel.scheduleDays.observe(viewLifecycleOwner) { days ->
            binding.reminderSetting.setSelectedDays(days)
        }
    }
    private fun scheduleDailyReminder(hour: Int, minute: Int, selectedDays: Set<Int>) {
        if (selectedDays.isEmpty()) {
      
            ScheduleNotificationUtils.cancelNotification(
                requireContext(),
                ScheduleNotificationUtils.REQ_CODE_STUDY_HOUR
            )
            return
        }


        ScheduleNotificationUtils.scheduleNotification(
            context = requireContext(),
            hour = hour,
            minute = minute,
            selectedDays = selectedDays,
            title = "Lexa",
            message = getString(R.string.time_to_learn),
            requestCode = ScheduleNotificationUtils.REQ_CODE_STUDY_HOUR
        )
    }
}
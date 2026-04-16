package com.home.lexa.ui.profile.profile_notification

import android.graphics.Color
import android.view.View
import android.widget.Toast
import com.home.lexa.MainActivity
import com.home.lexa.R
import com.home.lexa.core.base.BaseFragment
import com.home.lexa.databinding.FragmentProfileNotificationBinding
import com.home.lexa.ui.profile.profile.ProfileViewModel
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
            setIconBackgroundTint("#FFF4E5")

            setOnToggleChangeListener { isOn ->
                viewModel.onStreakToggled(isOn)
                if (isOn) {
                    val testTime = System.currentTimeMillis() + 10000
                    scheduleNotification(
                        requireContext(),
                        testTime,
                        "Test Thông Báo Streak",
                        "Thông báo này sẽ xuất hiện sau 10 giây"
                    )
                }
            }
        }

        binding.studyHourNotification.apply {
            setTitle("Thông báo giờ học")
            setDescription("Thông báo nhắc nhở trước khi đến giờ học đã cài đặt của bạn.")
            setIcon(R.drawable.ic_book, Color.parseColor("#4285F4"))

            setOnToggleChangeListener { isOn ->

                viewModel.onTimeToggled(isOn)

                updateReminderVisibility(isOn)

                if (isOn) {
                    val (hour, minute) = binding.reminderSetting.getSelectedTime()
                    scheduleDailyReminder(hour, minute)
                }
            }
        }
        binding.reminderSetting.setOnTimeChangedListener { hour, minute ->

            viewModel.saveScheduleTime(hour, minute)


            if (viewModel.timeToggleState.value == true) {
                scheduleDailyReminder(hour, minute)
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

    }
    private fun scheduleDailyReminder(hour: Int, minute: Int) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // Quan trọng: Nếu giờ hẹn đã qua trong ngày hôm nay, cộng thêm 1 ngày để nổ vào ngày mai
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }

        // Gọi hàm tiện ích của bạn
        scheduleNotification(
            requireContext(),
            calendar.timeInMillis,
            "Đã đến giờ học!",
            "Hãy vào Lexa duy trì chuỗi học tập ngay nhé!"
        )
    }
}
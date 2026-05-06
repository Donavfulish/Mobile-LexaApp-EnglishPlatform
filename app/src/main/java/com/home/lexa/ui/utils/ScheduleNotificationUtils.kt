package com.home.lexa.ui.utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.home.lexa.service.ScheduleNotification
import android.app.AlarmManager
import android.os.Build
import android.provider.Settings
import java.util.Calendar

object ScheduleNotificationUtils {
    const val REQ_CODE_STUDY_HOUR = 100
    const val REQ_CODE_STREAK = 101

    fun getNextTargetTime(hour: Int, minute: Int, selectedDays: Set<Int>): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val now = System.currentTimeMillis()

        for (i in 0..7) {
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)


            if (calendar.timeInMillis > now && selectedDays.contains(dayOfWeek)) {
                return calendar.timeInMillis
            }


            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return calendar.timeInMillis
    }
    fun scheduleNotification(
        context: Context,
        hour: Int,
        minute: Int,
        selectedDays: Set<Int>,
        title: String,
        message: String,
        requestCode: Int
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager


        val targetTime = getNextTargetTime(hour, minute, selectedDays)

        val intent = Intent(context, ScheduleNotification::class.java).apply {
            putExtra("title", title)
            putExtra("message", message)
            putExtra("hour", hour)
            putExtra("minute", minute)
            putExtra("selectedDays", selectedDays.toIntArray()) // Gửi mảng ngày
            putExtra("requestCode", requestCode)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 3. Đặt báo thức chính xác (chỉ chạy 1 lần)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            val intentSetting = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            context.startActivity(intentSetting)
            return
        }

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, targetTime, pendingIntent)
    }
    fun cancelNotification(context: Context, requestCode: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ScheduleNotification::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }
}
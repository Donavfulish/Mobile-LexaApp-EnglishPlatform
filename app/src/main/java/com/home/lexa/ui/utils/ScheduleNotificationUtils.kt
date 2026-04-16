package com.home.lexa.ui.utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.home.lexa.service.ScheduleNotification
import android.app.AlarmManager
import android.os.Build
import android.provider.Settings
object ScheduleNotificationUtils {
    const val REQ_CODE_STUDY_HOUR = 100
    const val REQ_CODE_STREAK = 101
    fun scheduleNotification(context: Context, timeInMillis: Long, title: String, message: String, requestCode: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ScheduleNotification::class.java).apply {
            putExtra("title", title)
            putExtra("message", message)
        }

        // PendingIntent là "vé thông hành" để hệ thống mở Receiver của bạn sau này
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12+
            if (alarmManager?.canScheduleExactAlarms() == true) {

                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
            } else {

                val intent = Intent().apply {
                    action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                }
                context.startActivity(intent)
            }
        } else {
            alarmManager?.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
        }

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
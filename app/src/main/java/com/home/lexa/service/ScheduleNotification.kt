package com.home.lexa.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.home.lexa.MainActivity // Thay bằng Activity bạn muốn mở
import com.home.lexa.R

const val notificationId = 1
const val channelId = "channel1"
const val titleExtra = "titleExtra"
const val messageExtra = "messageExtra"

class ScheduleNotification: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 1. TẠO CHANNEL (Xử lý lỗi No Channel found)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Thông báo học tập", // Tên hiển thị trong cài đặt của máy
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Nhắc nhở giờ học và chuỗi streak"
            }
            manager.createNotificationChannel(channel)
        }

        // 2. TẠO HÀNH ĐỘNG KHI BẤM VÀO THÔNG BÁO (Mở MainActivity)
        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val tapPendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            tapIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // 3. BUILD THÔNG BÁO
        val notification: Notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(intent.getStringExtra(titleExtra))
            .setContentText(intent.getStringExtra(messageExtra))
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Cho Android 7.1 trở xuống
            .setContentIntent(tapPendingIntent) // Gắn hành động bấm
            .setAutoCancel(true) // Tự động tắt khi bấm
            .build()

        // Hiển thị
        manager.notify(notificationId, notification)
    }
}
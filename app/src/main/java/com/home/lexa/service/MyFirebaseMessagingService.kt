package com.home.lexa.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.home.lexa.R
import kotlin.random.Random

class MyFirebaseMessagingService: FirebaseMessagingService() {
    // Hàm này được gọi khi token của thiết bị thay đổi hoặc lần đầu cài app
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // TODO: Gửi token này lên Server của bạn để server biết đường gửi push về máy này
        println("FCM Token: $token")
    }

    // Hàm này được gọi khi có thông báo tới (Đảm bảo server gửi Data Payload)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        var title = "Thông báo mới"
        var body = "Bạn có một tin nhắn mới"

        // 1. Xử lý Data Payload (Ưu tiên dùng cái này để bắt được ở Background/Terminated)
        if (remoteMessage.data.isNotEmpty()) {
            title = remoteMessage.data["title"] ?: title
            body = remoteMessage.data["body"] ?: body
            // Bạn có thể lấy thêm các trường data khác ở đây: remoteMessage.data["id"], v.v.
        }

        // 2. Xử lý Notification Payload (Chỉ chạy vào đây nếu app đang mở - Foreground)
        remoteMessage.notification?.let {
            title = it.title ?: title
            body = it.body ?: body
        }

        // 3. Hiển thị thông báo lên màn hình
        sendNotification(title, body, remoteMessage.data)
    }


    private fun sendNotification(title: String, messageBody: String, data: Map<String, String>) {

        // 1. Đóng gói dữ liệu (Data Payload) vào Bundle để truyền sang Fragment
        val bundle = Bundle().apply {
            for ((key, value) in data) {
                putString(key, value)
            }
        }

        // 2. Tạo PendingIntent bằng NavDeepLinkBuilder
        val pendingIntent = NavDeepLinkBuilder(this)
            .setGraph(R.navigation.nav_graph) // TODO: Thay bằng tên file nav_graph.xml của bạn
            .setDestination(R.id.homeFragment) // TODO: Thay bằng ID Fragment bạn muốn mở khi click thông báo
            .setArguments(bundle)
            .createPendingIntent()

        // Build giao diện thông báo
        val channelId = "fcm_default_channel"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher) // TODO: Thay bằng Icon trong suốt (transparent) của bạn (VD: R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true) // Click vào thì tự biến mất
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Tạo Notification Channel (Bắt buộc từ Android 8.0 / API 26+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Kênh thông báo chung",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Kênh này dùng để hiển thị các thông báo quan trọng"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Hiển thị thông báo với một ID ngẫu nhiên để không đè lên các thông báo cũ
        val notificationId = Random.nextInt()
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}
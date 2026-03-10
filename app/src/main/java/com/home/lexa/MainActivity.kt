package com.home.lexa

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity // Bắt buộc dùng cái này
import com.home.lexa.ui.components.StatsBox

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Nạp giao diện từ file res/layout/activity_main.xml
        setContentView(R.layout.fragment_home)

        // 2. Ánh xạ các Component XML ra để dùng
        val cardKhoaHoc = findViewById<StatsBox>(R.id.cardKhoaHoc)

        // 3. Đổ dữ liệu vào Component
        cardKhoaHoc.setCardData(
            iconRes = R.drawable.ic_launcher_background,
            count = 12,
            title = "Khóa đang học"
        )
    }
}
package com.home.lexa.data.repository

import com.home.lexa.domain.models.IntroData
import com.home.lexa.domain.repository.IntroRepository
import kotlinx.coroutines.delay

class IntroRepositoryImpl : IntroRepository {
    override suspend fun fetchIntroContent(): IntroData {
        delay(1500) // Giả lập gọi API mất 1.5s
        return IntroData(
            title = "Chào mừng trở lại!",
            description = "Đăng nhập để tiếp tục học tập cùng chúng tôi",
            buttonText = "Chuyển sang trang..."
        )
    }
}

package com.home.lexa.core.network

import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.home.lexa.R
import com.home.lexa.data.local.TokenManager
import com.home.lexa.data.remote.AuthApiService
import com.home.lexa.domain.models.RefreshRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import org.koin.core.Koin
import javax.inject.Provider

// File: app/src/main/java/com/home/lexa/core/network/TokenAuthenticator.kt
class TokenAuthenticator(
    private val tokenManager: TokenManager,
    private val koin: Koin // Nhận instance Koin để lấy Service khi cần
) : okhttp3.Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // Kiểm tra nếu đã thử refresh rồi mà vẫn 401 thì dừng lại (tránh vòng lặp vô tận)
        if (response.request.url.encodedPath.contains("refresh")) return null

        val oldAccessToken = response.request.header("Authorization")?.removePrefix("Bearer ")

        synchronized(this) {
            // 1. KIỂM TRA QUAN TRỌNG:
            // Nếu token trong máy đã khác token cũ, nghĩa là thread khác đã refresh xong rồi
            val currentAccessToken = tokenManager.getAccessToken()

            if (currentAccessToken.isNullOrBlank()) return null

            if (currentAccessToken != oldAccessToken) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentAccessToken")
                    .build()
            }

            val authApiService = koin.get<AuthApiService>()
            val currentRefreshToken = tokenManager.getRefreshToken()

            // 2. Gọi API refresh token đồng bộ (.execute())
            // Lưu ý: Phải dùng một instance Retrofit/OkHttp khác không có Authenticator này để tránh loop
            val refreshRequest = RefreshRequest(currentRefreshToken ?: "")
            val refreshResponse = authApiService.refreshToken(refreshRequest).execute()

            return if (refreshResponse.isSuccessful && refreshResponse.body() != null) {
                val newAccessToken = refreshResponse.body()!!.data!!.accessToken ?: ""

                // 3. Lưu token mới vào bộ nhớ local
                tokenManager.saveAccessToken(newAccessToken)

                // 4. Trả về Request mới với Header mới để OkHttp tự động thực hiện lại
                response.request.newBuilder()
                    .header("Authorization", "Bearer $newAccessToken")
                    .build()
            } else {
                // Refresh thất bại (Refresh token cũng hết hạn) -> Logout người dùng
                handleLogout()
                null
            }
        }
    }

    // Hàm helper để đếm số lần request đã được thử lại
    private fun retryCount(response: Response): Int {
        var count = 1
        var priorResponse = response.priorResponse
        while (priorResponse != null) {
            count++
            priorResponse = priorResponse.priorResponse
        }
        return count
    }

    private fun handleLogout() {
        tokenManager.clearTokens()
        CoroutineScope(Dispatchers.IO).launch {
            AuthEventBus.expireToken()
        }
    }
}
//package com.home.lexa.core.network
//
//import com.home.lexa.data.local.TokenManager
//import com.home.lexa.data.remote.AuthApiService
//import okhttp3.Authenticator
//import okhttp3.Request
//import okhttp3.Response
//import okhttp3.Route
//import javax.inject.Provider
//
//class TokenAuthenticator(
//    private val tokenManager: TokenManager,
//    private val authService: Provider<AuthApiService> // Dùng Provider để tránh vòng lặp khởi tạo (Circular Dependency)
//) : Authenticator {
//
//    override fun authenticate(route: Route?, response: Response): Request? {
//        // 1. Kiểm tra xem đã thử refresh chưa, nếu thử rồi mà vẫn 401 thì dừng (tránh loop)
//        if (response.priorResponse != null) {
//            return null
//        }
//
//        synchronized(this) {
//            // 2. Lấy token hiện tại và refresh token từ storage
//            val currentToken = tokenManager.getToken()
//            val refreshToken = tokenManager.getRefreshToken()
//
//            // 3. Gọi API Refresh (Dùng blocking call vì chúng ta đang ở thread của OkHttp)
//            val refreshResponse = authService.get().refreshToken("Bearer $refreshToken").execute()
//
//            return if (refreshResponse.isSuccessful) {
//                val newTokens = refreshResponse.body()
//
//                // 4. Lưu token mới vào Local Storage
//                newTokens?.let {
//                    tokenManager.saveTokens(it.accessToken, it.refreshToken)
//
//                    // 5. Tạo lại request bị lỗi với token mới
//                    response.request.newBuilder()
//                        .header("Authorization", "Bearer ${it.accessToken}")
//                        .build()
//                }
//            } else {
//                // Refresh thất bại (refreshToken hết hạn) -> Logout người dùng
//                tokenManager.clearAll()
//                // Bạn có thể dùng EventBus hoặc LiveData để báo cho UI quay về màn hình Login
//                null
//            }
//        }
//    }
//}
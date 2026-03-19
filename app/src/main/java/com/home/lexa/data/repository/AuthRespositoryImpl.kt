package com.home.lexa.data.repository

import com.home.lexa.data.remote.AuthApiService
import com.home.lexa.domain.models.AuthResult
import com.home.lexa.domain.models.LoginRequest
import com.home.lexa.domain.repository.AuthRespository

class AuthRespositoryImpl(private val apiService: AuthApiService) : AuthRespository {
    override suspend fun login(request: LoginRequest): Result<AuthResult> {
        return try {
            val response = apiService.login(request)
            val body = response.body()

            if (response.isSuccessful && body != null) {
                if (body.success == true && body.data != null) {
                    Result.success(body.data)
                } else {
                    // Nếu BE trả success = false, lấy message lỗi từ ApiResponse
                    Result.failure(Exception(body.message ?: "Đăng nhập thất bại từ server"))
                }
            } else {
                Result.failure(Exception("Lỗi máy chủ: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối mạng: ${e.message}"))
        }
    }
}
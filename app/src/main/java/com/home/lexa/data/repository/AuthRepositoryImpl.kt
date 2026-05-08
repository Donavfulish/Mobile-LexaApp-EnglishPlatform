package com.home.lexa.data.repository

import com.home.lexa.data.remote.AuthApiService
import com.home.lexa.di.AppMemoryCache
import com.home.lexa.domain.models.AuthResult
import com.home.lexa.domain.models.ChangeEmailRequest
import com.home.lexa.domain.models.ChangePasswordRequest
import com.home.lexa.domain.models.LoginRequest
import com.home.lexa.domain.models.OtpRequest
import com.home.lexa.domain.models.OtpVerify
import com.home.lexa.domain.models.ResetPasswordRequest
import com.home.lexa.domain.models.SignUpRequest
import com.home.lexa.domain.models.UserInfo
import com.home.lexa.domain.repository.AuthRespository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AuthRepositoryImpl(private val apiService: AuthApiService) : AuthRespository {
    override suspend fun login(request: LoginRequest): Result<AuthResult> {
        return try {
            val response = apiService.login(request)
            val body = response.body()

            if (response.isSuccessful && body != null) {
                if (body.success == true && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body.message ?: "Đăng nhập thất bại từ server"))
                }
            } else {
                Result.failure(Exception("Lỗi máy chủ: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối mạng: ${e.message}"))
        }
    }

    override suspend fun signup(
        dataPart: RequestBody,
        languagePart: MultipartBody.Part?,
        pedagogyPart: MultipartBody.Part?
    ): Result<AuthResult> {
        return try {
            val response = apiService.signup(dataPart, languagePart, pedagogyPart)
            val body = response.body()

            if (response.isSuccessful && body != null) {
                if (body.success == true && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body.message ?: "Đăng nhập thất bại từ server"))
                }
            } else {
                Result.failure(Exception("Lỗi máy chủ: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối mạng: ${e.message}"))
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            val response = apiService.logout()
            val body = response.body()

            if (response.isSuccessful && body != null) {
                if (body.success == true) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(body.message ?: "Lỗi xóa khóa token"))
                }
            } else {
                Result.failure(Exception("Lỗi máy chủ: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối mạng: ${e.message}"))
        }
    }

    override suspend fun getMe(): Result<UserInfo?> {
        return try {
            val response = apiService.getMe()
            val body = response.body()

            if (response.isSuccessful && body != null) {
                if (body.success == true && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body.message ?: "Người dùng chưa đăng nhập"))
                }
            } else {
                Result.failure(Exception("Lỗi máy chủ: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối mạng: ${e.message}"))
        }
    }

    override suspend fun loginGoogle(): Result<AuthResult> {
        return try {
            val response = apiService.loginGoogle()
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

    override suspend fun signupGoogle(
        dataPart: RequestBody,
        languagePart: MultipartBody.Part?,
        pedagogyPart: MultipartBody.Part?
    ): Result<AuthResult> {
        return try {
            val response = apiService.signupGoogle(dataPart, languagePart, pedagogyPart)
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

    override suspend fun sendOTP(request: OtpRequest): Result<Unit> {
        return try {
            val response = apiService.sendOTP(request)
            val body = response.body()

            if (response.isSuccessful && body != null) {
                if (body.success == true) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(body.message ?: "Gửi OTP không thành công"))
                }
            } else {
                Result.failure(Exception("Lỗi server: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối mạng: ${e.message}"))
        }
    }

    override suspend fun verifyOTP(request: OtpVerify): Result<Unit> {
        return try {
            val response = apiService.verifyOTP(request)
            val body = response.body()

            if (response.isSuccessful && body != null) {
                if (body.success == true) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(body.message ?: "OTP không hợp lệ"))
                }
            } else {
                Result.failure(Exception("Lỗi server: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối mạng: ${e.message}"))
        }
    }

    override suspend fun resetPassword(request: ResetPasswordRequest): Result<Unit> {
        return try {
            val response = apiService.resetPassword(request)
            val body = response.body()

            if (response.isSuccessful && body != null) {
                if (body.success == true) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(body.message ?: "Cập nhật mật khẩu không thành công"))
                }
            } else {
                Result.failure(Exception("Lỗi server: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối mạng: ${e.message}"))
        }
    }

    override suspend fun changeEmail(request: ChangeEmailRequest): Result<AuthResult> {
        return try {
            val response = apiService.changeEmail(request)
            val body = response.body()

            if (response.isSuccessful && body != null) {
                if (body.success == true && body.data != null) {
                    AppMemoryCache.remove("getProfile")
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body.message ?: "Cập nhật email thất bại"))
                }
            } else {
                Result.failure(Exception("Lỗi máy chủ: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối mạng: ${e.message}"))
        }
    }

    override suspend fun changePassword(request: ChangePasswordRequest): Result<AuthResult> {
        return try {
            val response = apiService.changePassword(request)
            val body = response.body()

            if (response.isSuccessful && body != null) {
                if (body.success == true && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body.message ?: "Cập nhật mật khẩu thất bại"))
                }
            } else {
                Result.failure(Exception("Lỗi máy chủ: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối mạng: ${e.message}"))
        }
    }
}
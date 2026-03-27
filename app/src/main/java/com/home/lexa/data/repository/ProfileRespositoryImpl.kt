package com.home.lexa.data.repository

import com.home.lexa.data.remote.ProfileApiService
import com.home.lexa.domain.models.Profile
import com.home.lexa.domain.models.UpdateProfileRequest
import com.home.lexa.domain.repository.ProfileRepository

class ProfileRepositoryImpl(private val apiService: ProfileApiService) : ProfileRepository {

    override suspend fun getProfile(id: Int): Result<Profile> {
        return try {
            val response = apiService.getProfile(id)
            val body = response.body()

            if (response.isSuccessful && body != null) {
                if (body.success == true && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body.message ?: "Lấy hồ sơ thất bại"))
                }
            } else {
                Result.failure(Exception("Lỗi máy chủ: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối: ${e.message}"))
        }
    }

    override suspend fun updateProfile(data: UpdateProfileRequest): Result<Boolean> {
        return try {
            val response = apiService.updateProfile(data)
            val body = response.body()

            if (response.isSuccessful && body != null) {
                if (body.success == true && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body.message ?: "Cập nhật hồ sơ thất bại"))
                }
            } else {
                Result.failure(Exception("Lỗi máy chủ: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối: ${e.message}"))
        }
    }
}
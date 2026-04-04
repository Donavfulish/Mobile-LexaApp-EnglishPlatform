package com.home.lexa.data.repository

import android.util.Log
import com.home.lexa.data.remote.ProfileApiService
import com.home.lexa.di.AppMemoryCache
import com.home.lexa.domain.models.Profile
import com.home.lexa.domain.models.UpdateProfileRequest
import com.home.lexa.domain.repository.ProfileRepository

class ProfileRepositoryImpl(private val apiService: ProfileApiService) : ProfileRepository {

    override suspend fun getProfile(): Result<Profile> {
        return try {
            val profile: Profile? = AppMemoryCache.get("getProfile");
            Log.d("ProfileCache", profile.toString())
            if (profile != null){
                return Result.success(profile);
            }

            val response = apiService.getProfile();
            val body = response.body()

            if (response.isSuccessful && body != null) {
                if (body.success == true && body.data != null) {
                    val data = body.data;
                    Log.d("ProfileCache", data.toString())
                    AppMemoryCache.put("getProfile", data as Any);
                    Result.success(data)
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


            Log.d("ProfileCacheBody", body.toString())
            if (response.isSuccessful && body != null) {
                if (body.success == true) {
                    // Xoá cache sau khi update thành công
                    Log.d("Đã xoá cache", "Đã xoá");
                    AppMemoryCache.remove("getProfile")
                    Result.success(true)
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
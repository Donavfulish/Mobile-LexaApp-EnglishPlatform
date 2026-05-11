package com.home.lexa.data.repository

import android.util.Log
import com.home.lexa.data.remote.ProfileApiService
import com.home.lexa.di.AppMemoryCache
import com.home.lexa.domain.models.GetAchievementResponse
import com.home.lexa.domain.models.Profile
import com.home.lexa.domain.models.UpdateFcmTokenRequest
import com.home.lexa.domain.models.UpdateProfileRequest
import com.home.lexa.domain.repository.ProfileRepository
import com.home.lexa.ui.profile.profile.AVATAR_ACTION
import okhttp3.MultipartBody

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

    override suspend fun updateFcmToken(data: UpdateFcmTokenRequest): Result<Boolean> {
        return try {
            val response = apiService.updateFcmToken(data)
            val body = response.body()

            if (response.isSuccessful && body != null) {
                if (body.success == true) {
                    Result.success(true)
                } else {
                    Result.failure(Exception(body.message ?: "Cập nhật FCM token thất bại"))
                }
            } else {
                Result.failure(Exception("Lỗi máy chủ: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối: ${e.message}"))
        }
    }

    override suspend fun updateAvatar(
        avatarPart: MultipartBody.Part?,
        action: AVATAR_ACTION
    ): Result<Boolean> {
        return try {
            val response = apiService.updateAvatar(avatarPart, action.value)
            val body = response.body()

            if (response.isSuccessful && body != null) {
                if (body.success == true) {
                    Log.d("Đã xoá cache", "Đã xoá");
                    AppMemoryCache.remove("getProfile")
                    Result.success(true)
                } else {
                    Result.failure(Exception(body.message ?: "Cập nhật avatar thất bại"))
                }
            } else {
                Result.failure(Exception("Lỗi máy chủ: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối: ${e.message}"))
        }
    }
    override suspend fun getAchievements(): Result<GetAchievementResponse> {
        return try {
            val achievementResponse: GetAchievementResponse? = AppMemoryCache.get("getAchievements");

            if (achievementResponse != null){
                return Result.success(achievementResponse);
            }

            val response = apiService.getAchievements();
            val body = response.body()

            if (response.isSuccessful && body != null) {
                if (body.success == true && body.data != null) {
                    val data = body.data;
                    AppMemoryCache.put("getAchievements", data as Any);
                    Result.success(data)
                } else {
                    Result.failure(Exception(body.message ?: "Lấy thành tựu thất bại"))
                }
            } else {
                Result.failure(Exception("Lỗi máy chủ: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối: ${e.message}"))
        }
    }
}
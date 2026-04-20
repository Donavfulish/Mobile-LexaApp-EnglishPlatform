package com.home.lexa.domain.repository

import com.home.lexa.domain.models.Profile
import com.home.lexa.domain.models.UpdateFcmTokenRequest
import com.home.lexa.domain.models.UpdateProfileRequest
import com.home.lexa.ui.profile.profile.AVATAR_ACTION
import okhttp3.MultipartBody

interface ProfileRepository {
    suspend fun getProfile(): Result<Profile>
    suspend fun updateProfile(data: UpdateProfileRequest): Result<Boolean>
    suspend fun updateFcmToken(data: UpdateFcmTokenRequest): Result<Boolean>
    suspend fun updateAvatar(avatarPart: MultipartBody.Part?, action: AVATAR_ACTION): Result<Boolean>
}
package com.home.lexa.domain.repository

import com.home.lexa.domain.models.Profile
import com.home.lexa.domain.models.UpdateProfileRequest

interface ProfileRepository {
    suspend fun getProfile(): Result<Profile>
    suspend fun updateProfile(data: UpdateProfileRequest): Result<Boolean>
}
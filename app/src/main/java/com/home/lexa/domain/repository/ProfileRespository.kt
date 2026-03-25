package com.home.lexa.domain.repository

import com.home.lexa.domain.models.Profile

interface ProfileRepository {
    suspend fun getProfile(id: Int): Result<Profile>
}
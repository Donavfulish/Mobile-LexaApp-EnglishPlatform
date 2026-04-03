package com.home.lexa.data.remote

import com.home.lexa.core.network.ApiResponse
import com.home.lexa.domain.models.Profile
import com.home.lexa.domain.models.UpdateProfileRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface ProfileApiService {
    @GET("api/profile")
    suspend fun getProfile(): Response<ApiResponse<Profile>>

    @PATCH("api/profile")
    suspend fun updateProfile(@Body data: UpdateProfileRequest): Response<ApiResponse<Boolean?>>
}
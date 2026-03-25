package com.home.lexa.data.remote

import com.home.lexa.core.network.ApiResponse
import com.home.lexa.domain.models.Profile
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ProfileApiService {
    @GET("api/profile/{id}")
    suspend fun getProfile(@Path("id") id: Int): Response<ApiResponse<Profile>>
}
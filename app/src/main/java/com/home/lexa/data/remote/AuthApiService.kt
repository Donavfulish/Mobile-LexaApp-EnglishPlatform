package com.home.lexa.data.remote

import com.home.lexa.core.network.ApiResponse
import com.home.lexa.domain.models.AuthResult
import com.home.lexa.domain.models.LoginRequest
import com.home.lexa.domain.models.SignUpRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<AuthResult>>

    @POST("api/auth/signup")
    suspend fun signup(@Body request: SignUpRequest): Response<ApiResponse<AuthResult>>
}
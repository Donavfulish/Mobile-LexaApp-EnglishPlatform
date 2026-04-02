package com.home.lexa.data.remote

import com.home.lexa.core.network.ApiResponse
import com.home.lexa.domain.models.AuthResult
import com.home.lexa.domain.models.LoginRequest
import com.home.lexa.domain.models.OAuthRegisterRequest
import com.home.lexa.domain.models.RefreshRequest
import com.home.lexa.domain.models.SignUpRequest
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<AuthResult>>

    @POST("api/auth/signup")
    suspend fun signup(@Body request: SignUpRequest): Response<ApiResponse<AuthResult>>

    @Headers(
        "Auth-Action: Refresh",
        "Content-Type: application/json",
        "Accept: application/json"
    )
    @POST("api/auth/refresh")
    fun refreshToken(@Body request: RefreshRequest): Call<ApiResponse<AuthResult>>

    @GET("/api/auth/google/check")
    suspend fun loginGoogle(): Response<ApiResponse<AuthResult>>

    @POST("/api/auth/google/signup")
    suspend fun signupGoogle(@Body request: OAuthRegisterRequest): Response<ApiResponse<AuthResult>>

}
package com.home.lexa.data.remote

import com.home.lexa.core.network.ApiResponse
import com.home.lexa.domain.models.AuthResult
import com.home.lexa.domain.models.LoginRequest
import com.home.lexa.domain.models.OtpRequest
import com.home.lexa.domain.models.OtpVerify
import com.home.lexa.domain.models.RefreshRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AuthApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<AuthResult>>

    @Multipart
    @POST("api/auth/signup")
    suspend fun signup(
        @Part("data") request: RequestBody,
        @Part languageCert: MultipartBody.Part?,
        @Part pedagogyCert: MultipartBody.Part?
    ): Response<ApiResponse<AuthResult>>

    @Headers(
        "Auth-Action: Refresh",
        "Content-Type: application/json",
        "Accept: application/json"
    )
    @POST("api/auth/refresh")
    fun refreshToken(@Body request: RefreshRequest): Call<ApiResponse<AuthResult>>

    @GET("/api/auth/google/check")
    suspend fun loginGoogle(): Response<ApiResponse<AuthResult>>

    @Multipart
    @POST("/api/auth/google/signup")
    suspend fun signupGoogle(
        @Part("data") request: RequestBody,
        @Part languageCert: MultipartBody.Part?,
        @Part pedagogyCert: MultipartBody.Part?
    ): Response<ApiResponse<AuthResult>>

    @POST("/api/auth/otp/send")
    suspend fun sendOTP(@Body request: OtpRequest): Response<ApiResponse<Unit>>


    @POST("/api/auth/otp/verify")
    suspend fun verifyOTP(@Body request: OtpVerify): Response<ApiResponse<Unit>>
}
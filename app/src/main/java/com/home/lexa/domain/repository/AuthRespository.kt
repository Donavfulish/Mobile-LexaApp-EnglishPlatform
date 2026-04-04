package com.home.lexa.domain.repository

import com.home.lexa.domain.models.AuthResult
import com.home.lexa.domain.models.LoginRequest
import com.home.lexa.domain.models.OtpRequest
import com.home.lexa.domain.models.OtpVerify
import com.home.lexa.domain.models.SignUpRequest
import com.home.lexa.domain.models.UserInfo
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface AuthRespository {
    suspend fun login(request: LoginRequest): Result<AuthResult>
    suspend fun signup(
        dataPart: RequestBody,
        languagePart: MultipartBody.Part?,
        pedagogyPart: MultipartBody.Part?
    ): Result<AuthResult>
    suspend fun logout(): Result<Unit>
    suspend fun getMe(): Result<UserInfo?>
    suspend fun loginGoogle(): Result<AuthResult>
    suspend fun signupGoogle(
         dataPart: RequestBody,
         languagePart: MultipartBody.Part?,
         pedagogyPart: MultipartBody.Part?
    ): Result<AuthResult>
    suspend fun sendOTP(request: OtpRequest): Result<Unit>
    suspend fun verifyOTP(request: OtpVerify): Result<Unit>
}
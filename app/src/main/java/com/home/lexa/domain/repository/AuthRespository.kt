package com.home.lexa.domain.repository

import com.home.lexa.domain.models.AuthResult
import com.home.lexa.domain.models.LoginRequest
import com.home.lexa.domain.models.SignUpRequest

interface AuthRespository {
    suspend fun login(request: LoginRequest): Result<AuthResult>
    suspend fun signup(request: SignUpRequest): Result<AuthResult>
}
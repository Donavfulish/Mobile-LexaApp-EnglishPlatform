package com.home.lexa.domain.repository

import com.home.lexa.domain.models.AuthResult
import com.home.lexa.domain.models.LoginRequest

interface AuthRespository {
    suspend fun login(request: LoginRequest): Result<AuthResult>
}
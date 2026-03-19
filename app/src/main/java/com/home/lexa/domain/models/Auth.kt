package com.home.lexa.domain.models

// Enum role (nếu bạn chưa định nghĩa)
enum class UserRole { TEACHER, STUDENT }

data class UserInfo(
    val id: Int,
    val email: String,
    val name: String,
    val role: UserRole,
    val passwordHash: String? = ""
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResult(
    val ok: Boolean,
    val message: String? = "",
    val id: Int? = null,
    val user: UserInfo? = null,
    val accessToken: String? = null,
    val refreshToken: String? = null
)
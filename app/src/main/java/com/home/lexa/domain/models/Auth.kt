package com.home.lexa.domain.models

import androidx.compose.ui.semantics.Role

// Enum role (nếu bạn chưa định nghĩa)
enum class UserRole { TEACHER, STUDENT }

data class UserInfo(
    val id: Int,
    val email: String,
    val name: String,
    val role: UserRole,
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class SignUpRequest(
    val email: String,
    val password: String,
    val date_of_birth: String,
    val address: String,
    val name: String,
    val role: UserRole
)

data class AuthResult(
    val ok: Boolean,
    val message: String? = "",
    val id: Int? = null,
    val user: UserInfo? = null,
    val accessToken: String? = null,
    val refreshToken: String? = null
)

val mockUserInfo = UserInfo(
    id = 8,
    name = "Huỳnh Gia Bịp",
    email = "hgau23@clc.fitus.edu.vn",
    role = UserRole.TEACHER
)
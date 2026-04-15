package com.home.lexa.domain.models

enum class UserRole { TEACHER, STUDENT }
enum class ProviderType { GOOGLE, FACEBOOK, GITHUB }

data class UserInfo(
    val id: Int,
    val email: String,
    val name: String,
    val role: UserRole,
    var isEmailVerified: Boolean? = false,
)

data class GoogleUserInfo(
    val sub: String? = null,
    val name: String,
    val email: String,
    val picture: String? = null
)

data class OAuthUserInfo(
    val provider: ProviderType,
    val sub: String? = null,
    val name: String,
    val email: String,
    val picture: String? = null
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class SignUpRequest(
    val email: String,
    val password: String,
    val date_of_birth: String?,
    val address: String?,
    val name: String,
    val role: UserRole
)

data class RefreshRequest(
    val refreshToken: String
)

data class AuthResult(
    val ok: Boolean,
    val message: String? = "",
    val id: Int? = null,
    val user: UserInfo? = null,
    val accessToken: String? = null,
    val refreshToken: String? = null
)

data class OAuthRegisterRequest (
    val provider: ProviderType,
    val name: String,
    val email: String? = null,
    val address: String,
    val role: UserRole,
    val english_certificate_url: String? = null,
    val pedagogical_certificate_url: String? = null
)

data class OAuthGoogleResult (
    val accessToken: String? = null,
    val user: GoogleUserInfo? = null,
    val registered: Boolean = false
)

data class OtpRequest (
    val email: String
)

data class OtpVerify (
    val email: String,
    val otp: String,
)

data class ResetPasswordRequest(
    val email: String,
    val password: String
)

data class ChangeEmailRequest (
    val email: String
)

val mockUserInfo = UserInfo(
    id = 8,
    name = "Huỳnh Gia Bịp",
    email = "hgau23@clc.fitus.edu.vn",
    role = UserRole.TEACHER
)
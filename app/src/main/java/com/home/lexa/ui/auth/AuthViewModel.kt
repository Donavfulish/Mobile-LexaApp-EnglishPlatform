package com.home.lexa.ui.auth

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.home.lexa.data.local.TokenManager
import com.home.lexa.data.local.UserManager
import com.home.lexa.domain.models.AuthResult
import com.home.lexa.domain.models.LoginRequest
import com.home.lexa.domain.models.OAuthGoogleResult
import com.home.lexa.domain.models.OAuthRegisterRequest
import com.home.lexa.domain.models.SignUpRequest
import com.home.lexa.domain.repository.AuthRespository
import com.home.lexa.ui.utils.MediaUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import androidx.lifecycle.AndroidViewModel
import com.home.lexa.R
import com.home.lexa.domain.models.ChangeEmailRequest
import com.home.lexa.domain.models.ChangePasswordRequest
import com.home.lexa.domain.models.OtpRequest
import com.home.lexa.domain.models.OtpVerify
import com.home.lexa.domain.models.ResetPasswordRequest
import com.home.lexa.domain.models.Profile
import com.home.lexa.domain.models.UpdateFcmTokenRequest
import com.home.lexa.domain.repository.ProfileRepository

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String? = "") : AuthState()
    data class Error(val error: String? = "") : AuthState()
}

class AuthViewModel (
    application: Application,
    private val repository: AuthRespository,
    private val profileRepository: ProfileRepository,
    private val tokenManager: TokenManager,
    private val userManager: UserManager
) : AndroidViewModel(application) {

    private val _loginState = MutableStateFlow<AuthState>(AuthState.Idle)
    private val _signupState = MutableStateFlow<AuthState>(AuthState.Idle)
    private val _OTPState = MutableStateFlow<AuthState>(AuthState.Idle)
    private val _changeEmailState = MutableStateFlow<AuthState>(AuthState.Idle)
    private val _changePasswordState = MutableStateFlow<AuthState>(AuthState.Idle)

    val loginState: StateFlow<AuthState> = _loginState.asStateFlow()
    val signupState: StateFlow<AuthState> = _signupState.asStateFlow()
    val OTPState: StateFlow<AuthState> = _OTPState.asStateFlow()
    val changeEmailState: StateFlow<AuthState> = _changeEmailState.asStateFlow()
    val changePasswordState: StateFlow<AuthState> = _changePasswordState.asStateFlow()

    private var selectedLanguageUri: Uri? = null
    private var selectedPedagogyUri: Uri? = null

    val oauthGoogleResult = MutableLiveData<OAuthGoogleResult?>()
    val rememberedLoginRequest = MutableLiveData<LoginRequest?>(null)
    val resetPasswordResult = MutableLiveData<Boolean?>(null)

    fun setAccessToken(token: String) {
        tokenManager.saveAccessToken(token)
    }

    fun login(request: LoginRequest) {
        viewModelScope.launch {
            _loginState.value = AuthState.Loading

            val result = repository.login(request)

            result.onSuccess { authResult ->
                Log.d("AuthViewModel", "FULL RESPONSE: $authResult")
                if (authResult.ok) {
                    saveUserAndToken(authResult)

                    val fcmToken = tokenManager.getFcmToken()
                    if (!fcmToken.isNullOrEmpty()) {
                        val request = UpdateFcmTokenRequest(fcmToken)
                        profileRepository.updateFcmToken(request)
                        println("Đã đồng bộ FCM Token với tài khoản user này!")
                    }

                    _loginState.value = AuthState.Success()
                } else {
                    _loginState.value = AuthState.Error()
                }
            }.onFailure {
                _loginState.value = AuthState.Error()
            }
        }
    }

    fun signup(request: SignUpRequest) {
        viewModelScope.launch {
            _signupState.value = AuthState.Loading

            val context = getApplication<Application>().applicationContext

            val dataPart = Gson().toJson(request).toRequestBody("application/json".toMediaTypeOrNull())
            val langPart = selectedLanguageUri?.let { MediaUtils.prepareFilePart(context, "languageCert", it) }
            val pedaPart = selectedPedagogyUri?.let { MediaUtils.prepareFilePart(context, "pedagogyCert", it) }

            val result = repository.signup(
                dataPart,
                langPart,
                pedaPart
            )

            result.onSuccess { authResult ->
                Log.d("AuthViewModel", "FULL RESPONSE: $authResult")
                if (authResult.ok) {
                    saveUserAndToken(authResult)

                    _signupState.value =
                        AuthState.Success()
                } else {
                    _signupState.value = AuthState.Error()
                }
            }.onFailure { error ->
                _signupState.value = AuthState.Error()
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            resetState()
            repository.logout()
            userManager.clearUser()
            tokenManager.clearTokens()
        }
    }

    fun getMe() {
        viewModelScope.launch {
            _loginState.value = AuthState.Loading

            val result = repository.getMe()

            result.onSuccess { user ->
                if (user != null) {
                    userManager.saveUser(user)

                    _loginState.value = AuthState.Success()
                } else {
                    _loginState.value = AuthState.Error()
                }
            }.onFailure { error ->
                _loginState.value = AuthState.Error()
            }
        }
    }

    fun setLanguageUri(uri: Uri?) { selectedLanguageUri = uri }
    fun setPedagogyUri(uri: Uri?) { selectedPedagogyUri = uri }

    fun signupGoogle(request: OAuthRegisterRequest) {
        viewModelScope.launch {
            _signupState.value = AuthState.Loading

            val context = getApplication<Application>().applicationContext

            val dataPart = Gson().toJson(request).toRequestBody("application/json".toMediaTypeOrNull())
            val langPart = selectedLanguageUri?.let { MediaUtils.prepareFilePart(context, "languageCert", it) }
            val pedaPart = selectedPedagogyUri?.let { MediaUtils.prepareFilePart(context, "pedagogyCert", it) }

            val result = repository.signupGoogle(
                dataPart,
                langPart,
                pedaPart
            )

            result.onSuccess { authResult ->
                if (authResult.ok) {
                    saveUserAndToken(authResult)

                    _signupState.value =
                        AuthState.Success()
                } else {
                    _signupState.value =
                        AuthState.Error()
                }
            }.onFailure { error ->
                _signupState.value = AuthState.Error()
            }
        }
    }

    fun loginGoogle() {
        viewModelScope.launch {
            _loginState.value = AuthState.Loading

            val result = repository.loginGoogle()

            result.onSuccess { authResult ->
                Log.d("AuthViewModel", "FULL RESPONSE: $authResult")
                if (authResult.ok) {
                    saveUserAndToken(authResult)

                    _loginState.value =
                        AuthState.Success()
                } else {
                    _loginState.value =
                        AuthState.Error()
                }
            }.onFailure { error ->
                _loginState.value = AuthState.Error()
            }
        }
    }

    fun resetOAuth() {
        oauthGoogleResult.value = null
    }

    fun resetState() {
        _loginState.value = AuthState.Idle
        _signupState.value = AuthState.Idle
    }

    fun isEmailVerified(): Boolean {
        return userManager.isEmailVerified()
    }

    fun sendOTP(email: String) {
        viewModelScope.launch {
            val request = OtpRequest(email)
            repository.sendOTP(request)
        }
    }

    fun verifyOTP(email: String, otp: String) {
        viewModelScope.launch {
            _OTPState.value = AuthState.Loading

            val request = OtpVerify(email, otp)
            val result = repository.verifyOTP(request)

            result.onSuccess {
                _OTPState.value = AuthState.Success()
            }.onFailure { error ->
                _OTPState.value = AuthState.Error()
            }
        }
    }

    fun resetPassword(request: ResetPasswordRequest) {
        viewModelScope.launch {
            val result = repository.resetPassword(request)

            result.onSuccess {
                resetPasswordResult.value = true
            }.onFailure { error ->
                resetPasswordResult.value = false
            }
        }
    }

    fun changeEmail(request: ChangeEmailRequest) {
        viewModelScope.launch {
            _changeEmailState.value = AuthState.Loading

            val result = repository.changeEmail(request)

            result.onSuccess { authResult ->
                if (authResult.ok) {
                    saveUserAndToken(authResult)

                    _changeEmailState.value =
                        AuthState.Success()
                } else {
                    _changeEmailState.value =
                        AuthState.Error()
                }
            }.onFailure { error ->
                _changeEmailState.value = AuthState.Error()
            }
        }
    }

    fun changePassword(request: ChangePasswordRequest) {
        viewModelScope.launch {
            _changePasswordState.value = AuthState.Loading

            val result = repository.changePassword(request)

            result.onSuccess { authResult ->
                if (authResult.ok) {
                    saveUserAndToken(authResult)

                    _changePasswordState.value =
                        AuthState.Success()
                } else {
                    _changePasswordState.value =
                        AuthState.Error()
                }
            }.onFailure { error ->
                _changePasswordState.value = AuthState.Error()
            }
        }
    }

    fun clearChangeEmailState() {
        _changeEmailState.value = AuthState.Idle
    }

    fun resetOTPState() {
        _OTPState.value = AuthState.Idle
    }

    fun commitEmailVerified() {
        return userManager.commitEmailVerified()
    }

    fun rememberLoginRequest(request: LoginRequest) {
        userManager.rememberLoginRequest(request)
    }

    fun forgetLoginRequest() {
        userManager.forgetLoginRequest()
    }

    fun getRememberedLoginRequest() {
        val saved = userManager.getRememberLoginRequest()
        rememberedLoginRequest.value = saved
    }

    fun getUserEmail(): String? {
        return userManager.getUserEmail()
    }

    private fun saveUserAndToken(data: AuthResult?) {
        if (data == null) return

        tokenManager.saveTokens(data.accessToken ?: "", data.refreshToken ?: "")
        data.user?.let { user ->
            userManager.saveUser(user)
        }
    }
}
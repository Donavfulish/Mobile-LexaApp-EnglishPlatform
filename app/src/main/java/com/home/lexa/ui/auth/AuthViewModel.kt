package com.home.lexa.ui.auth.login

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.lexa.data.local.TokenManager
import com.home.lexa.data.local.UserManager
import com.home.lexa.domain.models.AuthResult
import com.home.lexa.domain.models.LoginRequest
import com.home.lexa.domain.models.OAuthGoogleResult
import com.home.lexa.domain.models.OAuthRegisterRequest
import com.home.lexa.domain.models.SignUpRequest
import com.home.lexa.domain.repository.AuthRespository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val error: String) : AuthState()
}

class AuthViewModel (
    private val repository: AuthRespository,
    private val tokenManager: TokenManager,
    private val userManager: UserManager
) : ViewModel() {

    private val _loginState = MutableStateFlow<AuthState>(AuthState.Idle)
    private val _signupState = MutableStateFlow<AuthState>(AuthState.Idle)

    val loginState: StateFlow<AuthState> = _loginState.asStateFlow()
    val signupState: StateFlow<AuthState> = _signupState.asStateFlow()

    val oauthGoogleResult = MutableLiveData<OAuthGoogleResult?>()

//    companion object {
//        private var instance: AuthViewModel? = null
//
//        fun initialize(repo: AuthRespository, token: TokenManager, user: UserManager): AuthViewModel {
//            if (instance == null) {
//                instance = AuthViewModel(repo, token, user)
//            }
//            return instance!!
//        }
//
//        fun getInstance(): AuthViewModel {
//            return instance ?: throw IllegalStateException("AuthViewModel must be initialized in MainActivity first")
//        }
//    }

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

                    _loginState.value = AuthState.Success(authResult.message ?: "Đăng nhập thành công")
                } else {
                    _loginState.value = AuthState.Error(authResult.message ?: "Sai email hoặc mật khẩu")
                }
            }.onFailure { error ->
                _loginState.value = AuthState.Error(error.message ?: "Có lỗi xảy ra")
            }
        }
    }

    fun signup(request: SignUpRequest) {
        viewModelScope.launch {
            _signupState.value = AuthState.Loading

            val result = repository.signup(request)

            result.onSuccess { authResult ->
                Log.d("AuthViewModel", "FULL RESPONSE: $authResult")
                if (authResult.ok) {
                    saveUserAndToken(authResult)

                    _signupState.value = AuthState.Success(authResult.message ?: "Đăng ký thành công")
                } else {
                    _signupState.value = AuthState.Error(authResult.message ?: "Lỗi đăng ký")
                }
            }.onFailure { error ->
                _signupState.value = AuthState.Error(error.message ?: "Có lỗi xảy ra")
            }
        }
    }

    fun signupGoogle(request: OAuthRegisterRequest) {
        viewModelScope.launch {
            _signupState.value = AuthState.Loading

            val result = repository.signupGoogle(request)

            result.onSuccess { authResult ->
                if (authResult.ok) {
                    saveUserAndToken(authResult)

                    _signupState.value = AuthState.Success(authResult.message ?: "Đăng ký với Google thành công")
                } else {
                    _signupState.value = AuthState.Error(authResult.message ?: "Lỗi đăng ký bằng Google")
                }
            }.onFailure { error ->
                _signupState.value = AuthState.Error(error.message ?: "Có lỗi xảy ra")
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

                    _loginState.value = AuthState.Success(authResult.message ?: "Đăng nhập bằng Google thành công")
                } else {
                    _loginState.value = AuthState.Error(authResult.message ?: "Sai thông tin tài khoản Google")
                }
            }.onFailure { error ->
                _loginState.value = AuthState.Error(error.message ?: "Có lỗi xảy ra")
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

    private fun saveUserAndToken(data: AuthResult?) {
        if (data == null) return

        tokenManager.saveTokens(data.accessToken ?: "", data.refreshToken ?: "")
        data.user?.let { user ->
            userManager.saveUser(user)
        }
    }
}
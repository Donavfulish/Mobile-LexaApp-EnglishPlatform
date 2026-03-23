package com.home.lexa.ui.auth.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.lexa.data.local.TokenManager
import com.home.lexa.data.local.UserManager
import com.home.lexa.domain.models.LoginRequest
import com.home.lexa.domain.models.SignUpRequest
import com.home.lexa.domain.repository.AuthRespository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val error: String) : AuthState()
}

class AuthViewModel(
    private val repository: AuthRespository,
    private val tokenManager: TokenManager,
    private val userManager: UserManager
) : ViewModel() {

    private val _loginState = MutableStateFlow<AuthState>(AuthState.Idle)
    private val _signupState = MutableStateFlow<AuthState>(AuthState.Idle)

    val loginState: StateFlow<AuthState> = _loginState.asStateFlow()
    val signupState: StateFlow<AuthState> = _signupState.asStateFlow()

    fun login(request: LoginRequest) {
        viewModelScope.launch {
            _loginState.value = AuthState.Loading

            val result = repository.login(request)

            result.onSuccess { authResult ->
                Log.d("AuthViewModel", "FULL RESPONSE: $authResult")
                if (authResult.ok) {
                    // Lưu Token qua TokenManager
                    // LƯU Ý: Đổi 'accessToken' thành đúng tên property trong model response của bạn nhé
                    authResult.accessToken?.let { token ->
                        tokenManager.saveToken(token)
                    }
                    authResult.user?.let { user ->
                        userManager.saveUser(user)
                    }

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
                    // Nếu API signup của bạn cũng trả về token (auto-login sau khi đăng ký), thì lưu luôn tại đây
                    authResult.accessToken?.let { token ->
                        tokenManager.saveToken(token)
                    }

                    authResult.user?.let { user ->
                        userManager.saveUser(user)
                    }

                    _signupState.value = AuthState.Success(authResult.message ?: "Đăng ký thành công")
                } else {
                    _signupState.value = AuthState.Error(authResult.message ?: "Lỗi đăng ký")
                }
            }.onFailure { error ->
                _signupState.value = AuthState.Error(error.message ?: "Có lỗi xảy ra")
            }
        }
    }

    fun resetState() {
        _loginState.value = AuthState.Idle
        _signupState.value = AuthState.Idle
    }
}
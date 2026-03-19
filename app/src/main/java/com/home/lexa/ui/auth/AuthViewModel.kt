package com.home.lexa.ui.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.lexa.domain.models.LoginRequest
import com.home.lexa.domain.repository.AuthRespository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val error: String) : AuthState()
}

class AuthViewModel(
    private val repository: AuthRespository
) : ViewModel() {

    private val _loginState = MutableStateFlow<AuthState>(AuthState.Idle)
    val loginState: StateFlow<AuthState> = _loginState.asStateFlow()

    fun login(request: LoginRequest) {
        viewModelScope.launch {
            _loginState.value = AuthState.Loading

            val result = repository.login(request)

            result.onSuccess { authResult ->
                Log.d("AuthViewModel", "FULL RESPONSE: $authResult")
                if (authResult.ok) {
                    // TODO: Lưu accessToken vào SharedPreferences hoặc DataStore tại đây
                    _loginState.value = AuthState.Success(authResult.message ?: "Đăng nhập thành công")
                } else {
                    _loginState.value = AuthState.Error(authResult.message ?: "Sai email hoặc mật khẩu")
                }
            }.onFailure { error ->
                _loginState.value = AuthState.Error(error.message ?: "Có lỗi xảy ra")
            }
        }
    }

    fun resetState() {
        _loginState.value = AuthState.Idle;
    }
}
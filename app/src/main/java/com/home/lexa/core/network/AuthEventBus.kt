package com.home.lexa.core.network

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object AuthEventBus {
    private val _events = MutableSharedFlow<AuthEvent>()
    val events = _events.asSharedFlow()

    suspend fun logout() {
        _events.emit(AuthEvent.LOGOUT)
    }
    suspend fun expireToken() {
        _events.emit(AuthEvent.TOKEN_EXPIRED)
    }
}

enum class AuthEvent {
    LOGOUT,
    TOKEN_EXPIRED
}
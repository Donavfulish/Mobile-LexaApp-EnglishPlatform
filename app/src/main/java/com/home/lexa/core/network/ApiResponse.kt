package com.home.lexa.core.network

// Bọc dữ liệu trả về từ Backend
data class ApiResponse<T>(
    val success: Boolean? = null,
    val message: String? = null,
    val data: T?
)
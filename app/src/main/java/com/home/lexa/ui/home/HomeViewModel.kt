package com.home.lexa.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.lexa.data.repository.CourseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: CourseRepository) : ViewModel() {

    // State lưu status hiển thị (giống useState)
    private val _uiState = MutableStateFlow("Đang chờ thao tác...")
    val uiState: StateFlow<String> = _uiState

    fun getCourses() {
        _uiState.value = "Đang tải dữ liệu..."
        // Khởi chạy luồng bất đồng bộ (Coroutine)
        viewModelScope.launch {
            try {
                val courses = repository.fetchCourses()
                _uiState.value = "Đã tải xong ${courses.size} khóa học: ${courses[0].title}"
            } catch (e: Exception) {
                _uiState.value = "Lỗi rùi!"
            }
        }
    }
}
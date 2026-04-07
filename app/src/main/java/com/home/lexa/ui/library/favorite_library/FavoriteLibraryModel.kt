package com.home.lexa.ui.library.favorite_library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.lexa.domain.models.ShortCourseDto
import com.home.lexa.domain.repository.CourseRepository
import kotlinx.coroutines.launch

class FavoriteLibraryModel(private val repository: CourseRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // LiveData để chứa danh sách Decks
    private val _courses = MutableLiveData<List<ShortCourseDto>>(emptyList())
    val courses: LiveData<List<ShortCourseDto>> get() = _courses

    // Hàm này không cần suspend, gọi phát chạy luôn
    fun fetchAllCourses() {
        viewModelScope.launch {
            _isLoading.value = true

            val result = repository.getFavoriteDecks()

            result.onSuccess { list ->
                _courses.value = list
            }.onFailure {
                _courses.value = emptyList()
            }

            _isLoading.value = false
        }
    }
}
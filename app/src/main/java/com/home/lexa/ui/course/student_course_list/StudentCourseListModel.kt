package com.home.lexa.ui.course.student_course_list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.lexa.domain.models.DeckDto
import com.home.lexa.domain.models.IntroData
import com.home.lexa.domain.models.ShortCourseDto
import com.home.lexa.domain.models.mockUserInfo
import com.home.lexa.domain.repository.CourseRepository
import com.home.lexa.domain.repository.DeckRepository
import com.home.lexa.domain.repository.IntroRepository
import kotlinx.coroutines.launch
enum class CourseFilter {
    ALL,
    FAVORITE,
    LEARNING
}
class StudentCourseListModel(private val repository: CourseRepository) : ViewModel() {

    lateinit var filter: CourseFilter
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _currentFilter = MutableLiveData(CourseFilter.ALL)
    val currentFilter: LiveData<CourseFilter> = _currentFilter
    // LiveData để chứa danh sách Decks
    private val _courses = MutableLiveData<List<ShortCourseDto>>(emptyList())
    val courses: LiveData<List<ShortCourseDto>> get() = _courses

    fun changeFilter(filter: CourseFilter) {
        if (_currentFilter.value == filter) return
        _currentFilter.value = filter
        fetchAllCourses()
    }
    fun fetchAllCourses() {
        viewModelScope.launch {
            _isLoading.value = true

            val result = when (_currentFilter.value) {
                CourseFilter.ALL -> repository.getAllCourses()
                CourseFilter.FAVORITE -> repository.getFavoriteDecks(mockUserInfo.id)
                CourseFilter.LEARNING -> repository.getLearningCourses() // nếu có API
            }

            result.onSuccess { list ->
                _courses.value = list
            }.onFailure {
                _courses.value = emptyList()
            }

            _isLoading.value = false
        }
    }
}
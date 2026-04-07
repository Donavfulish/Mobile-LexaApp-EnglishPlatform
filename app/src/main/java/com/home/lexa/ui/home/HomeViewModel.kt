package com.home.lexa.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.lexa.domain.repository.CourseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.home.lexa.domain.models.GetFeaturedCourseResponse
import com.home.lexa.domain.models.GetStudyingCourseResponse

data class UserStats(
    val streakDays: Int,
    val weeklyHours: Float,
    val monthlyHours: Float
)

class HomeViewModel(private val repository: CourseRepository) : ViewModel() {




    private val _featuredCoursesFlow = MutableStateFlow<List<GetFeaturedCourseResponse>>(emptyList())
    val featuredCoursesFlow: StateFlow<List<GetFeaturedCourseResponse>> = _featuredCoursesFlow.asStateFlow()

    private val _studyingCoursesFlow = MutableStateFlow<List<GetStudyingCourseResponse>>(emptyList())
    val studyingCoursesFlow: StateFlow<List<GetStudyingCourseResponse>> = _studyingCoursesFlow.asStateFlow()

    private val _topStudiedCoursesFlow = MutableStateFlow<List<GetFeaturedCourseResponse>>(emptyList())
    val topStudiedCoursesFlow: StateFlow<List<GetFeaturedCourseResponse>> = _topStudiedCoursesFlow.asStateFlow()

    private val _userStatsFlow = MutableStateFlow(UserStats(3, 5f, 6f))
    val userStatsFlow: StateFlow<UserStats> = _userStatsFlow.asStateFlow()

    init {

        fetchFeaturedCourses()


        checkRoleAndFetchData()
    }

    private fun checkRoleAndFetchData() {

        val isTeacher = true

        if (isTeacher) {
            fetchTopStudiedCourses()
        } else {
            fetchStudyingCourses()
        }
    }

    public fun fetchFeaturedCourses() {
        viewModelScope.launch {
            val result = repository.getFeaturedCourses()
            result.onSuccess { dtoList ->
                _featuredCoursesFlow.value = dtoList
            }.onFailure { exception ->
                println("Lỗi gọi API Featured Courses: ${exception.message}")
            }
        }
    }

    public fun fetchStudyingCourses() {
        viewModelScope.launch {
            val result = repository.getStudyingCourses()
            result.onSuccess { dtoList ->
                _studyingCoursesFlow.value = dtoList
            }.onFailure { exception ->
                println("Lỗi gọi API Studying Courses: ${exception.message}")
            }
        }
    }

    public fun fetchTopStudiedCourses() {
        viewModelScope.launch {
            val result = repository.getTopStudiedCourses()
            result.onSuccess { dtoList ->
                _topStudiedCoursesFlow.value = dtoList
            }.onFailure { exception ->
                println("Lỗi gọi API Top Studied Courses: ${exception.message}")
            }
        }
    }
}
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
import com.home.lexa.data.local.UserManager
import com.home.lexa.domain.models.UserRole
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import com.home.lexa.R
import com.home.lexa.domain.repository.ProfileRepository

data class UserStats(
    val streakDays: Int,
    val weeklyHours: Float,
    val monthlyHours: Float,
    val studentCount: Int = 0,
    val favoriteCount: Int = 0,
)

class HomeViewModel(private val profileRepository: ProfileRepository,private val repository: CourseRepository, private val userManager: UserManager) : ViewModel() {



    private val isTeacher: Boolean get() = userManager.getUserRole() == UserRole.TEACHER
    private val _featuredCoursesFlow = MutableStateFlow<List<GetFeaturedCourseResponse>>(emptyList())
    val featuredCoursesFlow: StateFlow<List<GetFeaturedCourseResponse>> = _featuredCoursesFlow.asStateFlow()

    private val _studyingCoursesFlow = MutableStateFlow<List<GetStudyingCourseResponse>>(emptyList())
    val studyingCoursesFlow: StateFlow<List<GetStudyingCourseResponse>> = _studyingCoursesFlow.asStateFlow()

    private val _topStudiedCoursesFlow = MutableStateFlow<List<GetFeaturedCourseResponse>>(emptyList())
    val topStudiedCoursesFlow: StateFlow<List<GetFeaturedCourseResponse>> = _topStudiedCoursesFlow.asStateFlow()

    private val _userStatsFlow = MutableStateFlow(UserStats(userManager.getStreakCount(), 5f, 6f))
    val userStatsFlow: StateFlow<UserStats> = _userStatsFlow.asStateFlow()

    private val _toastMessageFlow = MutableSharedFlow<Int>()
    val toastMessageFlow: SharedFlow<Int> = _toastMessageFlow.asSharedFlow()

    init {

        fetchFeaturedCourses()

        checkRoleAndFetchData()
    }

    private fun checkRoleAndFetchData() {

        if (isTeacher) {
            fetchTopStudiedCourses()
            fetchTeacherAchievements()
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

    fun toggleFavorite(course: GetFeaturedCourseResponse, isFavorite: Boolean) {
        viewModelScope.launch {
            val result = if (isFavorite) {
                repository.favoriteCourse(course.id)
            } else {
                repository.disFavoriteCourse(course.id)
            }
            
            result.onSuccess {
                // Cập nhật lại danh sách featured courses trong bộ nhớ để UI phản hồi nhanh
                val updatedList = _featuredCoursesFlow.value.map {
                    if (it.id == course.id) {
                        it.copy(
                            is_favorite = isFavorite,
                            favorite_user_count = if (isFavorite) it.favorite_user_count + 1 else it.favorite_user_count - 1
                        )
                    } else it
                }
                _featuredCoursesFlow.value = updatedList
                
                // Tương tự cho top studied courses nếu có
                val updatedTopList = _topStudiedCoursesFlow.value.map {
                    if (it.id == course.id) {
                        it.copy(
                            is_favorite = isFavorite,
                            favorite_user_count = if (isFavorite) it.favorite_user_count + 1 else it.favorite_user_count - 1
                        )
                    } else it
                }
                _topStudiedCoursesFlow.value = updatedTopList
                
                _toastMessageFlow.emit(if (isFavorite) R.string.favorite_success else R.string.unfavorite_success)
            }.onFailure {
                _toastMessageFlow.emit(R.string.system_error)
            }
        }
    }
    fun updateStudyTime(weeklyHours: Float, monthlyHours: Float) {
        val currentStats = _userStatsFlow.value
        _userStatsFlow.value = currentStats.copy(
            weeklyHours = weeklyHours,
            monthlyHours = monthlyHours
        )
    }
    private fun fetchTeacherAchievements() {
        viewModelScope.launch {

            val result = profileRepository.getAchievements()

            result.onSuccess { response ->
                val currentStats = _userStatsFlow.value
                _userStatsFlow.value = currentStats.copy(
                    studentCount = response.countStudent,
                    favoriteCount = response.countFavorite
                )
            }.onFailure { exception ->
                println("Lỗi gọi API Achievements: ${exception.message}")
            }
        }
    }
}
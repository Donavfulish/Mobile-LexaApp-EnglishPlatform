package com.home.lexa.ui.course.teacher_course_list

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
enum class TeacherCourseFilter {
    ALL,
    FAVORITE,
    LEARNING,
    MYCOURSE
}
class StudentCourseListModel(private val repository: CourseRepository) : ViewModel() {

    lateinit var filter: TeacherCourseFilter
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _currentFilter = MutableLiveData(TeacherCourseFilter.ALL)
    val currentFilter: LiveData<TeacherCourseFilter> = _currentFilter
    // LiveData để chứa danh sách Decks
    private val _courses = MutableLiveData<List<ShortCourseDto>>(emptyList())
    val courses: LiveData<List<ShortCourseDto>> get() = _courses

    // Hàm này không cần suspend, gọi phát chạy luôn
    fun changeFilter(filter: TeacherCourseFilter) {
        Log.d("File filter", filter.toString());
        if (_currentFilter.value == filter) return
        _currentFilter.value = filter
        fetchAllCourses()
    }
    fun fetchAllCourses() {
        viewModelScope.launch {
            _isLoading.value = true

            val result = when (_currentFilter.value) {
                TeacherCourseFilter.ALL -> repository.getAllCourses()
                TeacherCourseFilter.FAVORITE -> repository.getFavoriteDecks()
                TeacherCourseFilter.LEARNING -> repository.getLearningCourses() // nếu có API
                TeacherCourseFilter.MYCOURSE -> repository.getMyCourses() // nếu có API
            }

            result.onSuccess { list ->
                _courses.value = list
                Log.d("Filter: ", currentFilter.toString());
                Log.d("Value: ", list.toString());
            }.onFailure {
                _courses.value = emptyList()
            }

            _isLoading.value = false
        }
    }
}
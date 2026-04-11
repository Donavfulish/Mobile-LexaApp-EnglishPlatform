package com.home.lexa.ui.course.teacher_course_list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.lexa.domain.models.SearchInfo
import com.home.lexa.domain.models.ShortCourseDto
import com.home.lexa.domain.repository.CourseRepository
import kotlinx.coroutines.launch

enum class TeacherCourseFilter {
    ALL,
    FAVORITE,
    LEARNING,
    MYCOURSE
}

class TeacherCourseListModel(private val repository: CourseRepository) : ViewModel() {

    lateinit var filter: TeacherCourseFilter
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _currentFilter = MutableLiveData(TeacherCourseFilter.ALL)
    val currentFilter: LiveData<TeacherCourseFilter> = _currentFilter
    // LiveData để chứa danh sách Decks
    private val _courses = MutableLiveData<List<ShortCourseDto>>(emptyList())
    val courses: LiveData<List<ShortCourseDto>> get() = _courses

    var lastId: Long? = null
    var isLastPage = false
    var currentPages = 0
    var totalPages = 0

    // Hàm này không cần suspend, gọi phát chạy luôn
    fun changeFilter(filter: TeacherCourseFilter, searchInfo: SearchInfo, nextCursor: Long?) {
        Log.d("File filter", filter.toString());
        if (_currentFilter.value == filter) return
        _currentFilter.value = filter

        fetchAllCourses(false, searchInfo, nextCursor)
    }
    fun fetchAllCourses(isLoadMore: Boolean, searchInfo: SearchInfo, nextCursor: Long?) {
        if (isLoadMore && (isLastPage || _isLoading.value == true)) return

        if (!isLoadMore) {
            isLastPage = false
            lastId = null
            currentPages = 0
            _courses.value = emptyList()
        }

        viewModelScope.launch {
            _isLoading.value = true

            val result = when (_currentFilter.value) {
                TeacherCourseFilter.ALL -> repository.getAllCourses(searchInfo, nextCursor)
                TeacherCourseFilter.FAVORITE -> repository.getFavoriteCourses(searchInfo, nextCursor)
                TeacherCourseFilter.LEARNING -> repository.getLearningCourses(searchInfo, nextCursor)
                TeacherCourseFilter.MYCOURSE -> repository.getMyCourses(searchInfo, nextCursor)
            }
            result.onSuccess { list ->
                currentPages += list.data.size
                totalPages = list.totalItem.toInt()
                lastId = list.nextCursor
                _courses.value = list.data
                if(currentPages.toLong() == list.totalItem){
                    isLastPage = true
                    lastId = null
                }
            }.onFailure {
                _courses.value = emptyList()
            }

            _isLoading.value = false
        }
    }
}
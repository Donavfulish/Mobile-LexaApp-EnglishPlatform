package com.home.lexa.ui.course.student_course_list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.lexa.domain.models.SearchInfo
import com.home.lexa.domain.models.ShortCourseDto
import com.home.lexa.domain.repository.CourseRepository
import kotlinx.coroutines.launch

enum class CourseFilter {
    ALL,
    FAVORITE,
    LEARNING
}

class StudentCourseListModel(private val repository: CourseRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _currentFilter = MutableLiveData(CourseFilter.ALL)
    val currentFilter: LiveData<CourseFilter> = _currentFilter

    private val _courses = MutableLiveData<List<ShortCourseDto>>(emptyList())
    val courses: LiveData<List<ShortCourseDto>> get() = _courses

    var lastId: Long? = null
    var isLastPage = false
    var currentPages = 0
    var totalPages = 0

    fun changeFilter(filter: CourseFilter, searchInfo: SearchInfo, nextCursor: Long?) {
        Log.d("File filter", filter.toString())
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
                CourseFilter.ALL -> repository.getAllCourses(searchInfo, nextCursor)
                CourseFilter.FAVORITE -> repository.getFavoriteCourses(searchInfo, nextCursor)
                CourseFilter.LEARNING -> repository.getLearningCourses(searchInfo, nextCursor)
                else -> repository.getAllCourses(searchInfo, nextCursor)
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
package com.home.lexa.ui.course.student_course_list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.lexa.domain.models.StudentCourseFilter
import com.home.lexa.domain.models.SearchInfo
import com.home.lexa.domain.models.ShortCourse
import com.home.lexa.domain.repository.CourseRepository
import kotlinx.coroutines.launch

class StudentCourseListModel(private val repository: CourseRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    var searchInfo = SearchInfo(
        query = null,
        sortBy = null,
        order = null,
        limit = 10
    )

    private val _currentFilter = MutableLiveData(StudentCourseFilter.ALL)
    val currentFilter: LiveData<StudentCourseFilter> = _currentFilter

    private val _courses = MutableLiveData<ShortCourse>()
    val courses: LiveData<ShortCourse> get() = _courses

    var lastId: Long? = null
    var isLastPage = false
    var currentPages = 0
    var totalPages = 0

    fun changeFilter(filter: StudentCourseFilter, searchInfo: SearchInfo, nextCursor: Long?) {
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
            _courses.value = ShortCourse(emptyList(), _currentFilter.value ?: StudentCourseFilter.ALL)
        }

        viewModelScope.launch {
            _isLoading.value = true
            val requestFilter = _currentFilter.value ?: StudentCourseFilter.ALL

            val result = when (requestFilter) {
                StudentCourseFilter.ALL -> repository.getAllCourses(searchInfo, nextCursor)
                StudentCourseFilter.FAVORITE -> repository.getFavoriteCourses(searchInfo, nextCursor)
                StudentCourseFilter.LEARNING -> repository.getLearningCourses(searchInfo, nextCursor)
                StudentCourseFilter.FINISHED -> repository.getLearningCourses(searchInfo, nextCursor)
            }

            result.onSuccess { list ->
                if(!list.data.isNullOrEmpty()){
                    currentPages += list.data.size
                    totalPages = list.totalItem.toInt()
                    lastId = list.nextCursor

                    if(currentPages.toLong() == list.totalItem){
                        isLastPage = true
                        lastId = null
                    }
                    _courses.value = ShortCourse(list.data, requestFilter)
                } else {
                    isLastPage = true
                    totalPages = 0
                    _courses.value = ShortCourse(emptyList(), requestFilter)
                }

            }.onFailure {
            }

            _isLoading.value = false
        }
    }
}
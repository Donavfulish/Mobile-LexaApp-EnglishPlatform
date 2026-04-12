package com.home.lexa.ui.course.teacher_course_list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.lexa.domain.models.SearchInfo
import com.home.lexa.domain.models.ShortCourse
import com.home.lexa.domain.models.TeacherCourseFilter
import com.home.lexa.domain.repository.CourseRepository
import kotlinx.coroutines.launch

class TeacherCourseListModel(private val repository: CourseRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _currentFilter = MutableLiveData(TeacherCourseFilter.ALL)
    val currentFilter: LiveData<TeacherCourseFilter> = _currentFilter
    
    // Đổi kiểu dữ liệu sang ShortCourse
    private val _courses = MutableLiveData<ShortCourse>()
    val courses: LiveData<ShortCourse> get() = _courses

    var lastId: Long? = null
    var isLastPage = false
    var currentPages = 0
    var totalPages = 0

    fun changeFilter(filter: TeacherCourseFilter, searchInfo: SearchInfo, nextCursor: Long?) {
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
            _courses.value = ShortCourse(emptyList(), _currentFilter.value ?: TeacherCourseFilter.ALL)
        }

        viewModelScope.launch {
            _isLoading.value = true
            val requestFilter = _currentFilter.value ?: TeacherCourseFilter.ALL

            val result = when (requestFilter) {
                TeacherCourseFilter.ALL -> repository.getAllCourses(searchInfo, nextCursor)
                TeacherCourseFilter.FAVORITE -> repository.getFavoriteCourses(searchInfo, nextCursor)
                TeacherCourseFilter.LEARNING -> repository.getLearningCourses(searchInfo, nextCursor)
                TeacherCourseFilter.MYCOURSE -> repository.getMyCourses(searchInfo, nextCursor)
            }
            
            result.onSuccess { list ->
                currentPages += list.data.size
                totalPages = list.totalItem.toInt()
                lastId = list.nextCursor
                
                if(currentPages.toLong() == list.totalItem){
                    isLastPage = true
                    lastId = null
                }
                _courses.value = ShortCourse(list.data, requestFilter)
            }.onFailure {
                _courses.value = ShortCourse(emptyList(), requestFilter)
            }

            _isLoading.value = false
        }
    }
}

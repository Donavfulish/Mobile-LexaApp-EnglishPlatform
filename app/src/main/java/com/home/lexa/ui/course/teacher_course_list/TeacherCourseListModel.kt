package com.home.lexa.ui.course.teacher_course_list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Query
import com.home.lexa.domain.models.SearchInfo
import com.home.lexa.domain.models.ShortCourse
import com.home.lexa.domain.models.TeacherCourseFilter
import com.home.lexa.domain.repository.CourseRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TeacherCourseListModel(private val repository: CourseRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _isSuggesting = MutableLiveData<Boolean>(false)
    val isSuggesting: LiveData<Boolean> get() = _isSuggesting
    private val _suggestions = MutableLiveData<List<String>>(emptyList())
    val suggestions: LiveData<List<String>> get() = _suggestions
    var searchInfo = SearchInfo(
        query = null,
        sortBy = null,
        order = null,
        limit = 10
    )

    private val _currentFilter = MutableLiveData(TeacherCourseFilter.ALL)
    val currentFilter: LiveData<TeacherCourseFilter> = _currentFilter
    
    // Đổi kiểu dữ liệu sang ShortCourse
    private val _courses = MutableLiveData<ShortCourse>()
    val courses: LiveData<ShortCourse> get() = _courses

    private val _deleteStatus = MutableLiveData<Result<Boolean>?>()
    val deleteStatus: LiveData<Result<Boolean>?> get() = _deleteStatus

    var lastId: Long? = null
    var isLastPage = false
    var currentPages = 0
    var totalPages = 0
    private var searchJob: Job? = null

    fun getSuggestions(query: String){
        if(isSuggesting.value == true) return
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(200)
            _isSuggesting.value = true
            val result = repository.getCourseSuggestions(query)
            result.onSuccess {list ->
                _suggestions.value = list!!
                _isSuggesting.value = false
            }.onFailure {
                _suggestions.value = emptyList()
                _isSuggesting.value = false
            }
        }
    }

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
                    totalPages = 0
                    isLastPage = true
                    _courses.value = ShortCourse(emptyList(), requestFilter)
                }
            }.onFailure {
                _courses.value = ShortCourse(emptyList(), requestFilter)
            }

            _isLoading.value = false
        }
    }

    fun deleteCourse(courseId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.deleteCourse(courseId)
            _deleteStatus.value = result
            _isLoading.value = false
        }
    }

    fun resetDeleteStatus() {
        _deleteStatus.value = null
    }
}

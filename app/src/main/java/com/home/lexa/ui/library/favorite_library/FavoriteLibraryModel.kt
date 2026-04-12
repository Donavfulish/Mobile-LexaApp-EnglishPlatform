package com.home.lexa.ui.library.favorite_library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.lexa.domain.models.SearchInfo
import com.home.lexa.domain.models.ShortCourse
import com.home.lexa.domain.models.ShortCourseDto
import com.home.lexa.domain.models.TeacherCourseFilter
import com.home.lexa.domain.repository.CourseRepository
import com.home.lexa.domain.repository.DeckRepository
import kotlinx.coroutines.launch

class FavoriteLibraryModel(private val repository: DeckRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // LiveData để chứa danh sách Decks
    private val _courses = MutableLiveData<List<ShortCourseDto>>(emptyList())
    val courses: LiveData<List<ShortCourseDto>> get() = _courses

    // Hàm này không cần suspend, gọi phát chạy luôn
    var lastId: Long? = null
    var isLastPage = false
    var currentPages = 0
    var totalPages = 0

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
            val result = repository.getFavoriteDecks(searchInfo, nextCursor)

            result.onSuccess { list ->
                currentPages += list.data.size
                totalPages = list.totalItem.toInt()
                lastId = list.nextCursor

                if(currentPages.toLong() == list.totalItem){
                    isLastPage = true
                    lastId = null
                }
                _courses.value = list.data
            }.onFailure {
                _courses.value = emptyList()
            }

            _isLoading.value = false
        }
    }
}
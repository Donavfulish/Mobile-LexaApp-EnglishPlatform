package com.home.lexa.ui.course.course_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.lexa.di.AppMemoryCache
import com.home.lexa.domain.models.DetailFlashcard
import com.home.lexa.domain.models.SpeakingCourseDetailDto
import com.home.lexa.domain.repository.CourseRepository
import com.home.lexa.domain.repository.FlashcardRepository
import kotlinx.coroutines.launch

class CourseDetailViewModel(
    private val courseRepository: CourseRepository,
    private val flashcardRepository: FlashcardRepository
) : ViewModel() {
    private val _courseDetailData = MutableLiveData<SpeakingCourseDetailDto?>()
    val courseDetailData: LiveData<SpeakingCourseDetailDto?> get() = _courseDetailData
    private val _flashcardDetailData = MutableLiveData<List<DetailFlashcard>>()
    val flashcardDetailData: LiveData<List<DetailFlashcard>> get() = _flashcardDetailData
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun setCourseAndFlashcard(course: SpeakingCourseDetailDto, flashcard: List<DetailFlashcard>){
        _courseDetailData.value = course
        _flashcardDetailData.value = flashcard
        _isLoading.value = false
    }
    fun loadCourseDetail(courseId: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val course = courseRepository.getSpeakingDayCourse(courseId)
                course.onSuccess { data ->
                    _courseDetailData.value = data
                    _isLoading.value = false
                    if (data != null){
                        AppMemoryCache.put("speakingCourseDetail_${data.id}", data)
                        fetchFlashcardsInBackground(data.id, data.deckId)
                    }
                }.onFailure {
                    _courseDetailData.value = null
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _courseDetailData.value = null
                _isLoading.value = false
            }
        }
    }

    private fun fetchFlashcardsInBackground(courseId: Long, deckId: Long) {
        viewModelScope.launch {
            flashcardRepository.getAllFlashcard(deckId).onSuccess { list ->
                _flashcardDetailData.value = list ?: emptyList()
                if(!list.isNullOrEmpty()){
                    AppMemoryCache.put("vocabularyList_${courseId}", list)
                }
            }.onFailure {
                _flashcardDetailData.value = emptyList()
            }
        }
    }
}

package com.home.lexa.ui.course.course_detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.lexa.di.AppMemoryCache
import com.home.lexa.domain.models.CreateDeckRequest
import com.home.lexa.domain.models.CreateFlashcardRequest
import com.home.lexa.domain.models.CreateSpeakingDayRequest
import com.home.lexa.domain.models.DetailFlashcard
import com.home.lexa.domain.models.SpeakingCourseDetailDto
import com.home.lexa.domain.repository.CourseRepository
import com.home.lexa.domain.repository.FlashcardRepository
import kotlinx.coroutines.launch
import com.home.lexa.domain.models.EditCourseRequest
import com.home.lexa.domain.models.Topic
import com.home.lexa.domain.models.mockUserInfo
import com.home.lexa.domain.repository.DeckRepository
import com.home.lexa.domain.repository.SpeakingDayRepository

class CourseDetailViewModel(
    private val courseRepository: CourseRepository,
    private val flashcardRepository: FlashcardRepository,
    private val speakingDayRepository: SpeakingDayRepository,
) : ViewModel() {
    private val _createStatus = MutableLiveData<Result<Unit>?>()
    val createStatus: LiveData<Result<Unit>?> get() = _createStatus
    private val _updateStatus = MutableLiveData<Result<Unit>?>()
    val updateStatus: LiveData<Result<Unit>?> get() = _updateStatus
    private val _courseDetailData = MutableLiveData<SpeakingCourseDetailDto?>()
    val courseDetailData: LiveData<SpeakingCourseDetailDto?> get() = _courseDetailData
    private val _flashcardDetailData = MutableLiveData<List<DetailFlashcard>>()
    val flashcardDetailData: LiveData<List<DetailFlashcard>> get() = _flashcardDetailData
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _topicData = MutableLiveData<List<Topic>>()
    val topicData: LiveData<List<Topic>> get() = _topicData
    private val _deckStatus = MutableLiveData<Result<Unit>?>()
    val deckStatus : LiveData<Result<Unit>?> get() = _deckStatus

    fun loadCourseDetail(courseId: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val course = courseRepository.getSpeakingDayCourse(courseId)
                course.onSuccess { data ->
                    _courseDetailData.value = data
                    _isLoading.value = false
                    if (data != null){
                        if(data.deckId != null) {
                            fetchFlashcardsInBackground(data.id, data.deckId)
                        }
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

    fun loadTopics() {
        viewModelScope.launch {
            val result = courseRepository.getAllTopics()
            try {
            result.onSuccess { list ->
                _topicData.value = list
            }.onFailure {
                _topicData.value = emptyList()
                Log.e("DEBUG_VM", "Load Topics failed: ${it.message}")
            }
        } catch (e: Exception){
                _topicData.value = emptyList()
                _isLoading.value = false
            }
        }
    }

    private fun fetchFlashcardsInBackground(courseId: Long, deckId: Long) {
        viewModelScope.launch {
            flashcardRepository.getAllFlashcard(deckId)
                .onSuccess { list ->
                _flashcardDetailData.value = list ?: emptyList()
                if(!list.isNullOrEmpty()){
                    AppMemoryCache.put("vocabularyList_${courseId}", list)
                }
            }.onFailure {
                _flashcardDetailData.value = emptyList()
            }
        }
    }

    fun editCourse(courseId: Long, request: EditCourseRequest){
        viewModelScope.launch {
            val result = courseRepository.editCourse(courseId, request)
            result.onSuccess {
                AppMemoryCache.remove("getSpeakingDayCourse_${courseId}")
                _updateStatus.value = Result.success(Unit)
            }.onFailure {
                _updateStatus.value = Result.failure(it)
            }
        }
    }

    fun resetUpdateStatus() {
        _updateStatus.value = null
    }

    fun createSpeakingDay(request: CreateSpeakingDayRequest){
        viewModelScope.launch {
            val result = speakingDayRepository.createSpeakingDay(request)
            result.onSuccess {
                AppMemoryCache.remove("getSpeakingDayCourse_${request.courseId}")
                _createStatus.value = Result.success(Unit)
            }.onFailure {
                Log.e("CREATE_STATUS", "Lỗi: ${it.message}", it)
                _createStatus.value = Result.failure(it)
            }
        }
    }

    fun resetCreateStatus() {
        _createStatus.value = null
    }

    fun deleteFlashcard(courseId: Long, flashcardId: Long, deckId: Long) {
        viewModelScope.launch {
            val result = flashcardRepository.deleteFlashcard(flashcardId)
            result.onSuccess {
                AppMemoryCache.remove("getAllFlashcard_${deckId}")
                _updateStatus.value = Result.success(Unit)
            }.onFailure {
                Log.e("DELETE_FLASHCARD", "Lỗi: ${it.message}", it)
                _updateStatus.value = Result.failure(it)
            }
        }
    }
}

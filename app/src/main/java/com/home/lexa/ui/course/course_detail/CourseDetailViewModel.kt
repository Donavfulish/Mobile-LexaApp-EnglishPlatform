package com.home.lexa.ui.course.course_detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.lexa.di.AppMemoryCache
import com.home.lexa.domain.models.CreateCourseRequest
import com.home.lexa.domain.models.CreateSpeakingDayRequest
import com.home.lexa.domain.models.DetailFlashcard
import com.home.lexa.domain.models.SpeakingCourseDetailDto
import com.home.lexa.domain.repository.CourseRepository
import com.home.lexa.domain.repository.FlashcardRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import com.home.lexa.domain.models.EditCourseRequest
import com.home.lexa.domain.models.Topic
import com.home.lexa.domain.repository.DeckRepository
import com.home.lexa.domain.repository.SpeakingDayRepository

class CourseDetailViewModel(
    private val courseRepository: CourseRepository,
    private val flashcardRepository: FlashcardRepository,
    private val speakingDayRepository: SpeakingDayRepository,
    private val deckRepository: DeckRepository
) : ViewModel() {
    private val _createCourseStatus = MutableLiveData<Result<Long>?>()
    val createCourseStatus: LiveData<Result<Long>?> get() = _createCourseStatus
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
    private val _favortieStatus = MutableLiveData<Result<Unit>?>()
    val favoriteStatus: LiveData<Result<Unit>?> get() = _favortieStatus

//    LOGIC TAO MOI KHOA HOC
    fun loadTopics() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = courseRepository.getAllTopics()
            try {
                result.onSuccess { list ->
                    _topicData.value = list
                    _isLoading.value = false
                }.onFailure {
                    _topicData.value = emptyList()
                    _isLoading.value = false
                    Log.e("DEBUG_VM", "Load Topics failed: ${it.message}")
                }
            } catch (e: Exception){
                _topicData.value = emptyList()
                _isLoading.value = false
            }
        }
    }

    fun resetTopicData(){
        _topicData.value = emptyList()
    }

    fun createCourse(request: CreateCourseRequest){
        viewModelScope.launch {
            val result = courseRepository.createCourse(request)
            try {
                result
                    .onSuccess { courseId ->
                    _createCourseStatus.value = Result.success(courseId)
                }
                    .onFailure {
                        _createCourseStatus.value = Result.failure(it)
                    }
            } catch (e: Exception){
                _createCourseStatus.value = Result.failure(e)
            }
        }
    }

    fun resetCreateCourseStatus() {
        _createCourseStatus.value = null
    }

    // LOGIC XU LY VAI TRO GIAO VIEN


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

    private fun fetchFlashcardsInBackground(courseId: Long, deckId: Long) {
        viewModelScope.launch {
            flashcardRepository.getAllFlashcard(deckId)
                .onSuccess { list ->
                _flashcardDetailData.value = list ?: emptyList()
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

    fun setFavorite(courseId: Long, deckId: Long) {
        viewModelScope.launch {
            try {
            val courseFavDeferred = async { courseRepository.favoriteCourse(courseId) }
            val deckFavDeferred = async { deckRepository.favoriteDeck(deckId) } // Giả định flashcardRepository có hàm này

            val courseResult = courseFavDeferred.await()
            val deckResult = deckFavDeferred.await()

            if (courseResult.isSuccess && deckResult.isSuccess) {
                _favortieStatus.value = Result.success(Unit)
                AppMemoryCache.remove("getSpeakingDayCourse_${courseId}")
            } else {
                _favortieStatus.value =  Result.failure(Exception("Lỗi cập nhật"))
            }
        } catch (e: Exception) {
                _favortieStatus.value =  Result.failure(Exception("Lỗi cập nhật"))
            }
        }
    }

    fun removeFavorite(courseId: Long, deckId: Long) {
        viewModelScope.launch {
            try {
                val courseUnfavDeferred = async { courseRepository.disFavoriteCourse(courseId) }
                val deckUnfavDeferred = async { deckRepository.disFavoriteDeck(deckId) }

                val courseResult = courseUnfavDeferred.await()
                val deckResult = deckUnfavDeferred.await()

                if (courseResult.isSuccess && deckResult.isSuccess) {
                    _favortieStatus.value = Result.success(Unit)
                    AppMemoryCache.remove("getSpeakingDayCourse_${courseId}")
                } else {
                    _favortieStatus.value =  Result.failure(Exception("Lỗi cập nhật"))
                }
            } catch (e: Exception) {
                _favortieStatus.value =  Result.failure(Exception("Lỗi cập nhật"))
            }
        }
    }
    fun resetFavoriteStatus() {
        _favortieStatus.value = null
    }
}

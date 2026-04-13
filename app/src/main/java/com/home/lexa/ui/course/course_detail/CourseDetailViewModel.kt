package com.home.lexa.ui.course.course_detail

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.home.lexa.di.AppMemoryCache
import com.home.lexa.domain.models.CreateCourseRequest
import com.home.lexa.domain.models.CreateSpeakingDayRequest
import com.home.lexa.domain.models.DetailFlashcard
import com.home.lexa.domain.models.CourseDetailDto
import com.home.lexa.domain.repository.CourseRepository
import com.home.lexa.domain.repository.FlashcardRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import com.home.lexa.domain.models.EditCourseRequest
import com.home.lexa.domain.models.ShortSpeakingDayDto
import com.home.lexa.domain.models.SpeakingDayPagination
import com.home.lexa.domain.models.Topic
import com.home.lexa.domain.repository.DeckRepository
import com.home.lexa.domain.repository.SpeakingDayRepository
import com.home.lexa.ui.utils.MediaUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class CourseDetailViewModel(
    application: Application,
    private val courseRepository: CourseRepository,
    private val flashcardRepository: FlashcardRepository,
    private val speakingDayRepository: SpeakingDayRepository,
    private val deckRepository: DeckRepository
) : AndroidViewModel(application) {
    private val _createCourseStatus = MutableLiveData<Result<Long>?>()
    val createCourseStatus: LiveData<Result<Long>?> get() = _createCourseStatus
    private val _createStatus = MutableLiveData<Result<Unit>?>()
    val createStatus: LiveData<Result<Unit>?> get() = _createStatus
    private val _updateStatus = MutableLiveData<Result<Unit>?>()
    val updateStatus: LiveData<Result<Unit>?> get() = _updateStatus
    private val _courseDetailData = MutableLiveData<CourseDetailDto?>()
    val courseDetailData: LiveData<CourseDetailDto?> get() = _courseDetailData
    private val _flashcardDetailData = MutableLiveData<List<DetailFlashcard>>()
    val flashcardDetailData: LiveData<List<DetailFlashcard>> get() = _flashcardDetailData
    private val _speakingDayDetailData = MutableLiveData<List<ShortSpeakingDayDto>>()
    val speakingDayDetailData: LiveData<List<ShortSpeakingDayDto>> get() = _speakingDayDetailData
    private val _paginationLoading = MutableLiveData<Boolean>()
    val paginationLoading: LiveData<Boolean> get() = _paginationLoading
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _topicData = MutableLiveData<List<Topic>>()
    val topicData: LiveData<List<Topic>> get() = _topicData
    private val _favortieStatus = MutableLiveData<Result<Unit>?>()
    val favoriteStatus: LiveData<Result<Unit>?> get() = _favortieStatus
    var isLastPage = false
    var currentPages = 0
    var totalPages = 0
    var nextItem: Long? = null


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

    fun createCourse(request: CreateCourseRequest, courseImageUri: Uri?){
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            val dataPart = Gson().toJson(request).toRequestBody("application/json".toMediaTypeOrNull())
            val imagePart = courseImageUri?.let { MediaUtils.prepareFilePart(context, "courseImage", it) }

            val result = courseRepository.createCourse(dataPart, imagePart)
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

    fun loadMoreSpeakingDay(isLoadMore: Boolean, courseId: Long, nextOrder: Long?){
        if(isLoadMore && (paginationLoading.value == true || isLastPage)) return
        if(!isLoadMore){
            isLastPage = false
            currentPages = 0
            nextItem = null
            _speakingDayDetailData.value = emptyList()
        }

        _paginationLoading.value = true
        viewModelScope.launch {
            val result = speakingDayRepository.getSpeakingDays(courseId, nextOrder)
            result.onSuccess { list ->
                currentPages =  list.data.size
                totalPages = list.totalItems
                nextItem = list.data[list.data.size - 1].order
                Log.d("PAGINATION_DEBUG", "curr: $currentPages, totalPages: $totalPages, nextItem: $nextItem")

                if(currentPages == totalPages){
                    isLastPage = true
                }
                _speakingDayDetailData.value = list.data
                _paginationLoading.value = false
            } .onFailure {
                _speakingDayDetailData.value = emptyList()
                _paginationLoading.value = false
            }
        }
    }

    // LOGIC XU LY VAI TRO GIAO VIEN


    fun loadCourseDetail(courseId: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val course = courseRepository.getCourseDetail(courseId)
                course.onSuccess { data ->
                    _courseDetailData.value = data
                    _isLoading.value = false
                    if (data != null){
                        if(data.list_speaking_day.data.size != 0){
                            currentPages = data.list_speaking_day.data.size
                            totalPages = data.list_speaking_day.totalItems
                            nextItem = data.list_speaking_day.data[data.list_speaking_day.data.size - 1].order

                            Log.d("PAGINATION_DEBUG", "curr: $currentPages, totalPages: $totalPages, nextItem: $nextItem")

                            if(currentPages == totalPages){
                                isLastPage = true
                            }
                            AppMemoryCache.put("getSpeakingDays", data.list_speaking_day)
                            _speakingDayDetailData.value = data.list_speaking_day.data
                            _paginationLoading.value = false
                        }
                        if(data.deckId != null) {
                            fetchFlashcardsInBackground(data.id, data.deckId)
                        }
                    }
                }.onFailure {
                    _courseDetailData.value = null
                    _isLoading.value = false
                    _speakingDayDetailData.value = emptyList()
                    _paginationLoading.value = false
                }
            } catch (e: Exception) {
                _courseDetailData.value = null
                _isLoading.value = false
                _speakingDayDetailData.value = emptyList()
                _paginationLoading.value = false
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

    fun editCourse(courseId: Long, request: EditCourseRequest, courseImageUri: Uri?){
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            val dataPart = Gson().toJson(request).toRequestBody("application/json".toMediaTypeOrNull())
            val imagePart = courseImageUri?.let { MediaUtils.prepareFilePart(context, "courseImage", it) }

            val result = courseRepository.editCourse(courseId, dataPart, imagePart)
            result.onSuccess {
                AppMemoryCache.remove("getCourseDetail_${courseId}")
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
                AppMemoryCache.remove("getCourseDetail_${request.courseId}")
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
            val result = flashcardRepository.deleteFlashcard(flashcardId, deckId)
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
                AppMemoryCache.remove("getCourseDetail_${courseId}")
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
                    AppMemoryCache.remove("getCourseDetail_${courseId}")
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

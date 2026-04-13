package com.home.lexa.ui.speaking.speaking_practice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.lexa.di.AppMemoryCache
import com.home.lexa.domain.models.CreateParagraphRequest
import com.home.lexa.domain.models.EditSpeakingDayRequest
import com.home.lexa.domain.models.ReorderParagraphsRequest
import com.home.lexa.domain.models.ShortParagraphSpeakingDayDto
import com.home.lexa.domain.models.UpdateParagraphRequest
import com.home.lexa.domain.repository.ParagraphRepository
import com.home.lexa.domain.repository.SpeakingDayRepository
import kotlinx.coroutines.launch

class SpeakingPracticeViewModel(
    private val speakingDayRepository: SpeakingDayRepository,
    private val paragraphRepository: ParagraphRepository
) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    
    private val _paragraphDetailData = MutableLiveData<ShortParagraphSpeakingDayDto?>()
    val paragraphDetailData: LiveData<ShortParagraphSpeakingDayDto?> get() = _paragraphDetailData

    private val _createStatus = MutableLiveData<Result<Unit>?>()
    val createStatus: LiveData<Result<Unit>?> get() = _createStatus

    private val _updateStatus = MutableLiveData<Result<Unit>?>()
    val updateStatus: LiveData<Result<Unit>?> get() = _updateStatus

    private val _updateParagraphStatus = MutableLiveData<Result<Unit>?>()
    val updateParagraphStatus: LiveData<Result<Unit>?> get() = _updateParagraphStatus

    private val _deleteStatus = MutableLiveData<Result<Unit>?>()
    val deleteStatus: LiveData<Result<Unit>?> get() = _deleteStatus

    private val _deleteSpeakingDayStatus = MutableLiveData<Result<Unit>?>()
    val deleteSpeakingDayStatus: LiveData<Result<Unit>?> get() = _deleteSpeakingDayStatus

    private val _reorderStatus = MutableLiveData<Result<Unit>?>()
    val reorderStatus: LiveData<Result<Unit>?> get() = _reorderStatus

    fun loadParagraphList(speakingDayId: Long){
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val paragraph = speakingDayRepository.getParagraphSpeakingDay(speakingDayId)
                paragraph.onSuccess { data ->
                    _paragraphDetailData.value = data
                    _isLoading.value = false
                }.onFailure {
                    _paragraphDetailData.value = null
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _paragraphDetailData.value = null
                _isLoading.value = false
            }
        }
    }

    fun editSpeakingDay(courseId: Long, speakingDayId: Long, request: EditSpeakingDayRequest) {
        viewModelScope.launch {
            val result = speakingDayRepository.editSpeakingDay(courseId, speakingDayId, request)
            result.onSuccess {
                _paragraphDetailData.value = _paragraphDetailData.value?.copy(title = request.title)
                _updateStatus.value = Result.success(Unit)
            }.onFailure {
                _updateStatus.value = Result.failure(it)
            }
        }
    }

    fun reorderParagraphs(courseId: Long, speakingDayId: Long, request: ReorderParagraphsRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = speakingDayRepository.reorderParagraphs(courseId, speakingDayId, request)
            result.onSuccess {
                _reorderStatus.value = Result.success(Unit)
            }.onFailure {
                _reorderStatus.value = Result.failure(it)
                _isLoading.value = false
            }
        }
    }

    fun createParagraph(speakingDayId: Long, paragraph: String, order: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val request = CreateParagraphRequest(
                speakingDayId = speakingDayId,
                audioURL = "",
                paragraph = paragraph,
                paragraphOrder = order
            )
            val result = paragraphRepository.createParagraph(request)
            result.onSuccess {
                _createStatus.value = Result.success(Unit)
            }.onFailure {
                _createStatus.value = Result.failure(it)
                _isLoading.value = false
            }
        }
    }

    fun updateParagraph(speakingDayId: Long, paragraphId: Long, newText: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = paragraphRepository.updateParagraph(speakingDayId, paragraphId, UpdateParagraphRequest(
                paragraph = newText,
                audioUrl = ""))
            result.onSuccess {
                _updateParagraphStatus.value = Result.success(Unit)
            }.onFailure {
                _updateParagraphStatus.value = Result.failure(it)
                _isLoading.value = false
            }
        }
    }

    fun deleteParagraph(speakingDayId: Long, paragraphId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = paragraphRepository.deleteParagraph(speakingDayId, paragraphId)
            result.onSuccess {
                _deleteStatus.value = Result.success(Unit)
            }.onFailure {
                _deleteStatus.value = Result.failure(it)
                _isLoading.value = false
            }
        }
    }

    fun deleteSpeakingDay(speakingDayId: Long, courseId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = speakingDayRepository.deleteSpeakingDay(courseId, speakingDayId)
            AppMemoryCache.remove("getSpeakingDayCourse_${courseId}")
            result.onSuccess {
                _deleteSpeakingDayStatus.value = Result.success(Unit)
            }.onFailure {
                _deleteSpeakingDayStatus.value = Result.failure(it)
                _isLoading.value = false
            }
        }
    }

    fun resetUpdateStatus() {
        _updateStatus.value = null
    }

    fun resetCreateStatus() {
        _createStatus.value = null
    }

    fun resetReorderStatus() {
        _reorderStatus.value = null
    }

    fun resetDeleteStatus() {
        _deleteStatus.value = null
    }

    fun resetUpdateParagraphStatus() {
        _updateParagraphStatus.value = null
    }

    fun resetDeleteSpeakingDayStatus() {
        _deleteSpeakingDayStatus.value = null
    }
}

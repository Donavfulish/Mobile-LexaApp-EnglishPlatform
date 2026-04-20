package com.home.lexa.ui.speaking.speaking_practice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.lexa.domain.models.ParagraphResultItemRequest
import com.home.lexa.domain.models.ParagraphResultResponseDto
import com.home.lexa.domain.models.ShortParagraphSpeakingDayDto
import com.home.lexa.domain.models.SubmitBulkDailyResultRequest
import com.home.lexa.domain.models.UpdateParagraphResultRequest
import com.home.lexa.domain.models.WordEvaluationItem
import com.home.lexa.domain.repository.ParagraphRepository
import com.home.lexa.domain.repository.SpeakingDayRepository
import kotlinx.coroutines.launch

class SpeakingPracticeStudentViewModel(
    private val speakingDayRepository: SpeakingDayRepository,
    private val paragraphRepository: ParagraphRepository
) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    
    private val _paragraphDetailData = MutableLiveData<ShortParagraphSpeakingDayDto?>()
    val paragraphDetailData: LiveData<ShortParagraphSpeakingDayDto?> get() = _paragraphDetailData

    private val _updateParagraphResultStatus = MutableLiveData<Result<ParagraphResultResponseDto>?>()
    val updateParagraphResultStatus: LiveData<Result<ParagraphResultResponseDto>?> get() = _updateParagraphResultStatus

    private val _bulkSaveStatus = MutableLiveData<Result<Boolean>?>()
    val bulkSaveStatus: LiveData<Result<Boolean>?> get() = _bulkSaveStatus

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

    fun submitBulkProgress(speakingDayId: Long, cacheData: List<ParagraphCacheItem>) {
        viewModelScope.launch {
            _isLoading.value = true
            try {

                // Bước 2: Map dữ liệu từ Cache sang Request DTO
                val mappedResults = cacheData.map { cache ->
                    ParagraphResultItemRequest(
                        paragraphId = cache.paragraphId,
                        wordEvaluation = cache.evaluationResults,
                        goodCount = cache.goodCount ?: 0,       // Đảm bảo không null
                        mediumCount = cache.mediumCount ?: 0,   // Đảm bảo không null
                        badCount = cache.badCount ?: 0,         // Đảm bảo không null
                        userAudioUrl = cache.localAudioPath     // *Thay bằng URL Server thực tế nếu cần
                    )
                }

                val request = SubmitBulkDailyResultRequest(
                    speakingDayId = speakingDayId,
                    results = mappedResults
                )

                // Bước 3: Gọi Repository để gửi request
                val result = paragraphRepository.submitBulkParagraphResults(request)

                _bulkSaveStatus.value = result
            } catch (e: Exception) {
                _bulkSaveStatus.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetBulkSaveStatus() {
        _bulkSaveStatus.value = null
    }

    // Trong SpeakingPracticeStudentViewModel.kt
    fun submitRecordingResult(
        paragraphId: Long,
        evaluationResults: List<WordEvaluationItem>,
        audioUrl: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true

            // Tính toán số lượng từ đúng, tạm, sai
            val goodCount = evaluationResults.count { it.status == "GOOD" }
            val mediumCount = evaluationResults.count { it.status == "MEDIUM" }
            val badCount = evaluationResults.count { it.status == "BAD" }

            val request = UpdateParagraphResultRequest(
                paragraphId = paragraphId,
                wordEvaluation = evaluationResults,
                goodCount = goodCount,
                mediumCount = mediumCount,
                badCount = badCount,
                userAudioUrl = audioUrl
            )
            val result = paragraphRepository.updateParagraphResult(request)
            _updateParagraphResultStatus.value = result
            _isLoading.value = false
        }
    }
    
    fun resetUpdateParagraphResultStatus() {
        _updateParagraphResultStatus.value = null
    }
}

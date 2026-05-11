package com.home.lexa.ui.speaking.daily_result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.lexa.domain.models.DailyResultSummary
import com.home.lexa.domain.models.ParagraphResultItemRequest
import com.home.lexa.domain.models.SubmitBulkDailyResultRequest
import com.home.lexa.domain.models.toDailyResultSummary
import com.home.lexa.domain.repository.SpeakingDayRepository
import com.home.lexa.domain.repository.ParagraphRepository
import com.home.lexa.ui.speaking.speaking_practice.ParagraphCacheItem

import kotlinx.coroutines.launch

class DailyResultViewModel(
    private val speakingDayRepository: SpeakingDayRepository,
    private val paragraphRepository: ParagraphRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _dailyResultData = MutableLiveData<DailyResultSummary?>()
    val dailyResultData: LiveData<DailyResultSummary?> get() = _dailyResultData

    private val _uploadStatus = MutableLiveData<Boolean?>()
    val uploadStatus: LiveData<Boolean?> get() = _uploadStatus

    fun submitFinalResult(speakingDayId: Long, cacheData: List<ParagraphCacheItem>) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val mappedResults = cacheData.map { cache ->
                    ParagraphResultItemRequest(
                        paragraphId = cache.paragraphId,
                        wordEvaluation = cache.evaluationResults,
                        goodCount = cache.goodCount,
                        mediumCount = cache.mediumCount,
                        badCount = cache.badCount,
                        userAudioUrl = "url_thuc_te_sau_khi_upload"
                    )
                }

                val request = SubmitBulkDailyResultRequest(
                    speakingDayId = speakingDayId,
                    results = mappedResults
                )

                val result = paragraphRepository.submitBulkParagraphResults(request)

                result.onSuccess {
                    _uploadStatus.value = true
                }.onFailure {
                    _uploadStatus.value = false
                }

            } catch (e: Exception) {
                _uploadStatus.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** Loads saved evaluations for [speakingDayId] from the API (bypasses in-memory paragraph cache first page). */
    fun loadDailyResult(speakingDayId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = speakingDayRepository.getParagraphSpeakingDay(speakingDayId, skipCache = true)
                result.onSuccess { dto ->
                    _dailyResultData.value = dto?.toDailyResultSummary()
                }.onFailure {
                    _dailyResultData.value = null
                }
            } catch (e: Exception) {
                _dailyResultData.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }
}

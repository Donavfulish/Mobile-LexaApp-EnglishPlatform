package com.home.lexa.ui.speaking.daily_result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.lexa.domain.models.DailyResultSummary
import com.home.lexa.domain.models.ParagraphResult
import com.home.lexa.domain.models.ParagraphResultItemRequest
import com.home.lexa.domain.models.ParagraphWord
import com.home.lexa.domain.models.SubmitBulkDailyResultRequest
import com.home.lexa.domain.repository.SpeakingDayRepository
import com.home.lexa.domain.repository.ParagraphRepository
import com.home.lexa.ui.speaking.speaking_practice.ParagraphCacheItem

import kotlinx.coroutines.delay
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
                // LƯU Ý QUAN TRỌNG VỀ AUDIO:
                // Vì bạn đang lưu file local (localAudioPath), bạn không thể nhét chuỗi này vào JSON gửi lên backend được.
                // BƯỚC 1: Duyệt qua cacheData, upload các file âm thanh này lên Server (hoặc S3/Firebase) để lấy URL thực tế.
                // val uploadedAudioUrls = uploadAudios(cacheData.map { it.localAudioPath })

                // BƯỚC 2: Map data chuẩn bị gửi
                val mappedResults = cacheData.map { cache ->
                    ParagraphResultItemRequest(
                        paragraphId = cache.paragraphId,
                        wordEvaluation = cache.evaluationResults,
                        goodCount = cache.goodCount,
                        mediumCount = cache.mediumCount,
                        badCount = cache.badCount,
                        userAudioUrl = "url_thuc_te_sau_khi_upload" // Thay bằng URL thật
                    )
                }

                val request = SubmitBulkDailyResultRequest(
                    speakingDayId = speakingDayId,
                    results = mappedResults
                )

                // BƯỚC 3: Gọi Repository để đẩy 1 cục lên
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
    fun loadDailyResult(speakingDayId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // =====================================================================
                // TODO: [DÙNG KHI CÓ API THẬT]
                // val result = speakingDayRepository.getDailySummaryResult(speakingDayId)
                // result.onSuccess { data ->
                //     _dailyResultData.value = data
                // }.onFailure {
                //     _dailyResultData.value = null
                // }
                // =====================================================================

                // =====================================================================
                // CODE MOCK DATA TẠM THỜI (Để bạn test UI chạy mượt trước khi nối API)
                // =====================================================================
                delay(500) // Giả lập độ trễ mạng

                val mockParagraph1 = ParagraphResult(
                    id = 1, order = "1", audioUrl = "url_mau_1", userUrl = "url_user_1",
                    paragraph = listOf(
                        ParagraphWord("This", "green"), ParagraphWord("is", "green"),
                        ParagraphWord("the", "green"), ParagraphWord("first", "green"),
                        ParagraphWord("paragraph", "yellow"), ParagraphWord("for", "green"),
                        ParagraphWord("today.", "green")
                    )
                )

                val mockParagraph2 = ParagraphResult(
                    id = 2, order = "2", audioUrl = "url_mau_2", userUrl = "url_user_2",
                    paragraph = listOf(
                        ParagraphWord("Keep", "green"), ParagraphWord("practicing", "green"),
                        ParagraphWord("to", "green"), ParagraphWord("improve", "yellow"),
                        ParagraphWord("your", "green"), ParagraphWord("speaking", "red"),
                        ParagraphWord("skills.", "green")
                    )
                )

                _dailyResultData.value = DailyResultSummary(
                    totalGood = 11,
                    totalAccepted = 2,
                    totalBad = 1,
                    paragraphs = listOf(mockParagraph1, mockParagraph2)
                )
                // =====================================================================

            } catch (e: Exception) {
                _dailyResultData.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }
}
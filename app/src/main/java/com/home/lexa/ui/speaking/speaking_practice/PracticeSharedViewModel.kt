package com.home.lexa.ui.speaking.speaking_practice

import androidx.lifecycle.ViewModel
import com.home.lexa.domain.models.WordEvaluationItem

// Data class đại diện cho 1 đoạn văn đã được thu âm & chấm điểm
data class ParagraphCacheItem(
    val paragraphId: Long,
    val originalText: String,
    val evaluationResults: List<WordEvaluationItem>,
    val localAudioPath: String,
    val goodCount: Int,
    val mediumCount: Int,
    val badCount: Int
)

class PracticeSharedViewModel : ViewModel() {
    // Cache lưu trữ kết quả của các đoạn văn
    private val _sessionCache = mutableMapOf<Int, ParagraphCacheItem>()
    val sessionCache: Map<Int, ParagraphCacheItem> get() = _sessionCache

    // Lưu kết quả của 1 đoạn vào cache
    fun saveParagraphToCache(
        index: Int,
        paragraphId: Long,
        originalText: String,
        evaluationResults: List<WordEvaluationItem>,
        audioPath: String
    ) {
        val good = evaluationResults.count { it.status == "GOOD" }
        val medium = evaluationResults.count { it.status == "MEDIUM" }
        val bad = evaluationResults.count { it.status == "BAD" }

        _sessionCache[index] = ParagraphCacheItem(
            paragraphId = paragraphId,
            originalText = originalText,
            evaluationResults = evaluationResults,
            localAudioPath = audioPath,
            goodCount = good,
            mediumCount = medium,
            badCount = bad
        )
    }

    // Xóa cache (Dùng khi ấn Học lại)
    fun clearCache() {
        _sessionCache.clear()
        // TODO: Mở rộng - Bạn có thể viết thêm logic xóa các file audio local ở đây để giải phóng bộ nhớ máy
    }
}
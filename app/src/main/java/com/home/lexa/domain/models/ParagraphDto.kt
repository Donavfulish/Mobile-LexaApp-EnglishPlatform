package com.home.lexa.domain.models

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

// --- REQUESTS ---
@Serializable
data class ShortParagraphDto(
    val id: Long,
    val paragraph: String?,
    @SerializedName(value = "paragraph_order", alternate = ["paragraphOrder"])
    val paragraph_order: Long?,
    @SerializedName(value = "audioUrl", alternate = ["audio_url"])
    val audioUrl: String? = null,
    @SerializedName(value = "wordEvaluation", alternate = ["word_evaluation"])
    val wordEvaluation: List<WordEvaluationItem>? = null,
    @SerializedName(value = "goodCount", alternate = ["good_count"])
    val goodCount: Int? = null,
    @SerializedName(value = "mediumCount", alternate = ["medium_count"])
    val mediumCount: Int? = null,
    @SerializedName(value = "badCount", alternate = ["bad_count"])
    val badCount: Int? = null,
    @SerializedName(value = "userAudioUrl", alternate = ["user_audio_url"])
    val userAudioUrl: String? = null
)

data class CreateParagraphRequest(
    val speakingDayId: Long,
    val paragraphOrder: Int,
    val paragraph: String,
    val audioURL: String? = null
)

data class UpdateParagraphRequest(
    val paragraph: String? = null,
    val audioUrl: String? = null
)

data class ParagraphResponseDto(
    val id: Long,
    val paragraph: String,
    val audioUrl: String?,
    val paragraphOrder: Int
)

@Serializable
data class WordEvaluationItem(
    val word: String,
    val score: Int,
    val status: String
)

data class UpdateParagraphResultRequest(
    val paragraphId: Long,
    val wordEvaluation: List<WordEvaluationItem>? = null,
    val goodCount: Int? = null,
    val mediumCount: Int? = null,
    val badCount: Int? = null,
    val userAudioUrl: String? = null
)

data class ParagraphResultItemRequest(
    val paragraphId: Long,
    val wordEvaluation: List<WordEvaluationItem>?,
    val goodCount: Int,
    val mediumCount: Int,
    val badCount: Int,
    val userAudioUrl: String? // URL sau khi upload audio lên server
)

// Request tổng để gửi Bulk
data class SubmitBulkDailyResultRequest(
    val speakingDayId: Long,
    val results: List<ParagraphResultItemRequest>
)

data class ParagraphResultResponseDto(
    val userId: Int,
    val paragraphId: Long,
    val wordEvaluation: List<WordEvaluationItem>?,
    val goodCount: Int?,
    val mediumCount: Int?,
    val badCount: Int?,
    val userAudioUrl: String?
)

// Data class này dùng để tổng hợp dữ liệu cho màn hình Result
data class DailyResultSummary(
    val totalGood: Int,
    val totalAccepted: Int, // Tương đương với Medium
    val totalBad: Int,
    val paragraphs: List<ParagraphResult>
)
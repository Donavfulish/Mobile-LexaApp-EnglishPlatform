package com.home.lexa.domain.models

// --- REQUESTS ---

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

data class ParagraphResultResponseDto(
    val userId: Int,
    val paragraphId: Long,
    val wordEvaluation: List<WordEvaluationItem>?,
    val goodCount: Int?,
    val mediumCount: Int?,
    val badCount: Int?,
    val userAudioUrl: String?
)
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
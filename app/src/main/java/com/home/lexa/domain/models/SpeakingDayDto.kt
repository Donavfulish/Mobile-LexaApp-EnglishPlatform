package com.home.lexa.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class ShortSpeakingDayDto(
    val speakingDayId: Long,
    val title: String,
    val completed: Int,
    val paragraphNum: Int
)
@Serializable
data class CreateSpeakingDayRequest(
    val courseId: Long,
    val title: String?,
)
@Serializable
data class EditSpeakingDayRequest(
    val title: String?,
)

@Serializable
data class ShortParagraphSpeakingDayDto(
    val title: String?,
    val list_paragraphs: List<ShortParagraphDto>
)
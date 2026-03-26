package com.home.lexa.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class ShortSpeakingDayDto(
    val title: String,
    val completed: Int
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
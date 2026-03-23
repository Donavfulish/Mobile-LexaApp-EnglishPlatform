package com.home.lexa.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class ShortSpeakingDayDto(
    val title: String,
    val completed: Int
)
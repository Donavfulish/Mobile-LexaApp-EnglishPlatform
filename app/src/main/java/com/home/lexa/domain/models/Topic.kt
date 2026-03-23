package com.home.lexa.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class TopicDto(
    val id: Int,
    val name: String,
    val colorHex: String
)
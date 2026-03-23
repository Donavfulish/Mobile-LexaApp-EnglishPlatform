package com.home.lexa.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Topic(
    val id: Int,
    val name: String,
    val colorHex: String
)
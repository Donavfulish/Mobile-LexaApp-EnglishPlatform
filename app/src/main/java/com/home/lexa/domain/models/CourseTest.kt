package com.home.lexa.domain.models

import kotlinx.serialization.Serializable

@Serializable


data class ParagraphWord(
    val w: String,
    val s: String // green, yellow, red
)
data class ParagraphResult(
    val id: Int,
    val paragraph: List<ParagraphWord>,
    val order: String,
    val audioUrl: String,
    val userUrl: String
)
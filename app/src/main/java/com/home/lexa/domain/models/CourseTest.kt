package com.home.lexa.domain.models

import android.graphics.drawable.Drawable
import android.media.Image
import kotlinx.serialization.Serializable

@Serializable
data class CourseTest(val id: Int, val title: String, val teacher: String, val topic: String, val isFavorite: Boolean, val imageRes: Int, val member: String, val favorite: String)
data class CourseProgress(
    val id: Int,
    val title: String,
    val topic: String,
    val progress: Int,
    val imageRes: Int
)
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
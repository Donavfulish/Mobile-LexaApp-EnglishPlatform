package com.home.lexa.domain.models

import kotlinx.serialization.Serializable

// Model hứng data từ luồng GET
data class Course(
    val id: Long,
    val topicId: Int?,
    val title: String,
    val description: String?,
    val creatorId: Int,
    val privacy: String?
)

// Model đẩy data lên cho luồng POST
data class CreateCourseRequest(
    val topicId: Int? = null,
    val title: String,
    val description: String? = null,
    val creatorId: Int,
    val privacy: String
)
@Serializable
data class CreatorDto(
    val name: String,
    val image: String?
)


@Serializable
data class SpeakingCourseDetailDto(
    val id: Long,
    val thumbnail_url: String?,
    val creator: CreatorDto,
    val type: String?,
    val typeColor: String?,
    val is_favorite: Boolean?,
    val title: String,
    val studying_user_count: Int,
    val favorite_user_count: Int,
    val description: String?,
    val deckId: Long,
    val list_speaking_day: List<ShortSpeakingDayDto>
)
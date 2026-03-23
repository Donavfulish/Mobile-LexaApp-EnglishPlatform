package com.home.lexa.domain.models

// Model hứng data từ luồng GET
data class ShortCourseDto(
    val id: Long,
    val thumbnail_url: String?,
    val topic: TopicDto,
    val is_favorite: Boolean? = null,
    val title: String,
    val description: String,
    val creator_name: String,
    val creator_avatar_url: String,
    val vocabNumber: Int,
    val studying_user_count: Int,
    val favorite_user_count: Int,
    val completed: Int? = null
)

// Model đẩy data lên cho luồng POST
data class CreateCourseRequest(
    val topicId: Int? = null,
    val title: String,
    val description: String? = null,
    val creatorId: Int,
    val privacy: String
)
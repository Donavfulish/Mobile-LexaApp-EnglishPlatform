package com.home.lexa.domain.models

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
package com.home.lexa.domain.models
import kotlinx.serialization.Serializable


data class Course(
    val id: Long,
    val topicId: Int?,
    val title: String,
    val description: String?,
    val creatorId: Int,
    val privacy: String?
)

interface BaseCourseFilter

enum class StudentCourseFilter : BaseCourseFilter {
    ALL,
    FAVORITE,
    LEARNING
}

enum class TeacherCourseFilter : BaseCourseFilter {
    ALL,
    FAVORITE,
    LEARNING,
    MYCOURSE
}

data class ShortCourse(
    val data: List<ShortCourseDto>,
    val status: BaseCourseFilter
)

data class ShortCourseDto(
    val id: Long,
    val thumbnail_url: String?,
    val topic: Topic,
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
data class GetFeaturedCourseResponse(
    val id: Long,
    val thumbnail_url: String?,
    val topic: Topic,
    val is_favorite: Boolean? = null,
    val title: String,
    val creator_name: String,
    val creator_avatar_url: String,
    val studying_user_count: Int,
    val favorite_user_count: Int,
)
data class GetStudyingCourseResponse(
    val id: Long,
    val title: String,
    val topic: Topic,
    val progress: Int,
    val thumbnail_url: String?
)
@Serializable
data class CreatorDto(
    val id: Int,
    val name: String,
    val image: String?
)


@Serializable
data class CourseDetailDto(
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
    val deckId: Long?,
    val list_speaking_day: List<ShortSpeakingDayDto>,
    val list_topic: List<Topic>
)


@Serializable
data class CreateCourseRequest(
    val topicId: Int? = null,
    val title: String,
    val description: String? = null,
    val privacy: String,
    val thumbnailUrl: String? = null
)
@Serializable
data class EditCourseRequest(
    val topicId: Int? = null,
    val title: String,
    val description: String? = null,
    val privacy: String,
    val thumbnailUrl: String? = null
)

// Pagination, sort, query and filter.
@Serializable
data class AllCoursePaginationResponse(
    val data: List<ShortCourseDto>,
    val searchInfo: SearchInfo,
    val nextCursor: Long?= null,
    val totalItem: Long)

@Serializable
data class SearchInfo(
    val query: String ?= null,
    val sortBy: String ?= null,
    val order: String ?= null,
    val limit: Int ?= null
)

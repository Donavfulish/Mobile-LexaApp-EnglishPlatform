package com.home.lexa.domain.repository

import com.home.lexa.domain.models.CreateCourseRequest
import com.home.lexa.domain.models.EditCourseRequest
import com.home.lexa.domain.models.ShortCourseDto
import com.home.lexa.domain.models.SpeakingCourseDetailDto
import com.home.lexa.domain.models.GetFeaturedCourseResponse
import com.home.lexa.domain.models.GetStudyingCourseResponse

interface CourseRepository {
    suspend fun getAllCourses(): Result<List<ShortCourseDto>>
    suspend fun createCourse(request: CreateCourseRequest): Result<Long>
    suspend fun editCourse(courseId: Long, request: EditCourseRequest): Result<Boolean>
    suspend fun deleteCourse(courseId: Long): Result<Boolean>

    suspend fun getFavoriteDecks(): Result<List<ShortCourseDto>>
    suspend fun getSpeakingDayCourse(courseId: Long): Result<SpeakingCourseDetailDto?>
    suspend fun getFeaturedCourses(): Result<List<GetFeaturedCourseResponse>>
    suspend fun getTopStudiedCourses(): Result<List<GetFeaturedCourseResponse>>
    suspend fun getStudyingCourses(): Result<List<GetStudyingCourseResponse>>

    suspend fun getMyCourses(): Result<List<ShortCourseDto>>
    suspend fun getLearningCourses(): Result<List<ShortCourseDto>>
    suspend fun favoriteCourse(courseId: Long ): Result<Boolean>
    suspend fun disFavoriteCourse(courseId: Long): Result<Boolean>
}
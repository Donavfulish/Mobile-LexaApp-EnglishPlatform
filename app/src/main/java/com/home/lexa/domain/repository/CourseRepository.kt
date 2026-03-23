package com.home.lexa.domain.repository

import com.home.lexa.domain.models.CreateCourseRequest
import com.home.lexa.domain.models.ShortCourseDto
import com.home.lexa.domain.models.SpeakingCourseDetailDto

interface CourseRepository {
    suspend fun getAllCourses(): Result<List<ShortCourseDto>>
    suspend fun createCourse(request: CreateCourseRequest): Result<Long>
    suspend fun getFavoriteDecks(userId: Int): Result<List<ShortCourseDto>>
    suspend fun getSpeakingDayCourse(courseId: Long): Result<SpeakingCourseDetailDto?>
}
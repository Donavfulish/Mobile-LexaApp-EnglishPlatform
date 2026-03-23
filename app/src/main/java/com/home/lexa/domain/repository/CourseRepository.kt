package com.home.lexa.domain.repository

import com.home.lexa.domain.models.Course
import com.home.lexa.domain.models.CreateCourseRequest
import com.home.lexa.domain.models.GetFeaturedCourseResponse
import com.home.lexa.domain.models.GetStudyingCourseResponse
import com.home.lexa.domain.models.ShortCourseDto

interface CourseRepository {
    suspend fun getCourses(): Result<List<Course>>
    suspend fun createCourse(request: CreateCourseRequest): Result<Long>
    suspend fun getFeaturedCourses(): Result<List<GetFeaturedCourseResponse>>
    suspend fun getTopStudiedCourses(): Result<List<GetFeaturedCourseResponse>>
    suspend fun getStudyingCourses(): Result<List<GetStudyingCourseResponse>>
}
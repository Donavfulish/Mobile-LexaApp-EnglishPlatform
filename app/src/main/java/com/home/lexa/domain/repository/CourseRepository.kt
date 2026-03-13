package com.home.lexa.domain.repository

import com.home.lexa.domain.models.Course
import com.home.lexa.domain.models.CreateCourseRequest

interface CourseRepository {
    suspend fun getCourses(): Result<List<Course>>
    suspend fun createCourse(request: CreateCourseRequest): Result<Long>
}
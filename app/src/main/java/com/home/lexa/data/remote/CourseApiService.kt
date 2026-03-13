package com.home.lexa.data.remote

import com.home.lexa.core.network.ApiResponse
import com.home.lexa.domain.models.Course
import com.home.lexa.domain.models.CreateCourseRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CourseApiService {

    // Gọi GET /api/courses
    @GET("api/courses")
    suspend fun getCourses(): Response<ApiResponse<List<Course>>>

    // Gọi POST /api/courses
    @POST("api/courses")
    suspend fun createCourse(@Body request: CreateCourseRequest): Response<ApiResponse<Map<String, Long>>>
}
package com.home.lexa.data.remote

import com.home.lexa.core.network.ApiResponse
import com.home.lexa.domain.models.Course
import com.home.lexa.domain.models.CreateCourseRequest
import com.home.lexa.domain.models.SpeakingCourseDetailDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CourseApiService {

    // Gọi GET /api/courses
    @GET("api/courses")
    suspend fun getCourses(): Response<ApiResponse<List<Course>>>

    // Gọi POST /api/courses
    @POST("api/courses")
    suspend fun createCourse(@Body request: CreateCourseRequest): Response<ApiResponse<Map<String, Long>>>

    @GET("/api/courses/{courseId}/speaking-days")
    suspend fun getSpeakingDayCourse(
        @Path("courseId") courseId: Long
    ) : Response<ApiResponse<SpeakingCourseDetailDto>>
}
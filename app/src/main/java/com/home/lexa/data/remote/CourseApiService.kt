package com.home.lexa.data.remote

import com.home.lexa.core.network.ApiResponse
import com.home.lexa.domain.models.Course
import com.home.lexa.domain.models.CreateCourseRequest
import com.home.lexa.domain.models.GetFeaturedCourseResponse
import com.home.lexa.domain.models.GetStudyingCourseResponse
import com.home.lexa.domain.models.ShortCourseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CourseApiService {

    // Gọi GET /api/courses
    @GET("api/courses")
    suspend fun getCourses(): Response<ApiResponse<List<Course>>>
    // Gọi GET /api/courses/featured
    @GET("api/courses/featured")
    suspend fun getFeaturedCourses(): Response<ApiResponse<List<GetFeaturedCourseResponse>>>
    // Gọi GET /api/courses/studying
    @GET("api/courses/studying")
    suspend fun getStudyingCourses(): Response<ApiResponse<List<GetStudyingCourseResponse>>>
    @GET("api/courses/top-studied")
    suspend fun getTopStudiedCourses(): Response<ApiResponse<List<GetFeaturedCourseResponse>>>
    // Gọi @GET("api/courses/studying")
    @POST("api/courses")
    suspend fun createCourse(@Body request: CreateCourseRequest): Response<ApiResponse<Map<String, Long>>>
}
package com.home.lexa.data.remote

import com.home.lexa.core.network.ApiResponse
import com.home.lexa.domain.models.AllCoursePaginationResponse
import com.home.lexa.domain.models.CreateCourseRequest
import com.home.lexa.domain.models.EditCourseRequest
import com.home.lexa.domain.models.CourseDetailDto
import com.home.lexa.domain.models.ShortCourseDto
import com.home.lexa.domain.models.GetFeaturedCourseResponse
import com.home.lexa.domain.models.GetStudyingCourseResponse
import com.home.lexa.domain.models.Topic
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CourseApiService {
    @GET("api/topics")
    suspend fun getTopics(): Response<ApiResponse<List<Topic>>>

    // Gọi GET /api/courses
    @GET("api/courses")
    suspend fun getCourses(
        @Query("query") query: String? = null,
        @Query("sort") sort: String? = null,
        @Query("order") order: String? = null,
        @Query("limit") limit: String? = null,
        @Query("next_id") next_id: String? = null,
        ): Response<ApiResponse<AllCoursePaginationResponse>>

    // Gọi GET /api/courses/featured
    @GET("api/courses/featured")
    suspend fun getFeaturedCourses(): Response<ApiResponse<List<GetFeaturedCourseResponse>>>
    // Gọi GET /api/courses/studying
    @GET("api/courses/studying")
    suspend fun getStudyingCourses(): Response<ApiResponse<List<GetStudyingCourseResponse>>>
    @GET("api/courses/top-studied")
    suspend fun getTopStudiedCourses(): Response<ApiResponse<List<GetFeaturedCourseResponse>>>
    // Gọi @GET("api/courses/studying")
    @POST("api/users/me/courses")
    suspend fun createCourse(@Body request: CreateCourseRequest): Response<ApiResponse<Long>>

    @PATCH("api/users/me/courses/{courseId}")
    suspend fun editCourse(@Path("courseId") courseId: Long, @Body request: EditCourseRequest): Response<ApiResponse<Map<String, Any>>>

    @DELETE("api/users/me/courses/{courseId}")
    suspend fun deleteCourse(@Path("courseId") courseId: Long): Response<ApiResponse<Map<String, Any>>>

    @GET("/api/user/me/course/favorite")
    suspend fun getFavoriteCourses(
        @Query("query") query: String? = null,
        @Query("sort") sort: String? = null,
        @Query("order") order: String? = null,
        @Query("limit") limit: String? = null,
        @Query("next_id") next_id: String? = null,
    ): Response<ApiResponse<AllCoursePaginationResponse>>

    @GET("/api/user/me/course/learning")
    suspend fun getLearningCourses(
        @Query("query") query: String? = null,
        @Query("sort") sort: String? = null,
        @Query("order") order: String? = null,
        @Query("limit") limit: String? = null,
        @Query("next_id") next_id: String? = null,
    ): Response<ApiResponse<AllCoursePaginationResponse>>


    @GET("/api/users/me/courses")
    suspend fun getMyCourses(
        @Query("query") query: String? = null,
        @Query("sort") sort: String? = null,
        @Query("order") order: String? = null,
        @Query("limit") limit: String? = null,
        @Query("next_id") next_id: String? = null,
    ): Response<ApiResponse<AllCoursePaginationResponse>>

    @GET("/api/courses/{courseId}/course-detail")
    suspend fun getCourseDetail(
        @Path("courseId") courseId: Long
    ) : Response<ApiResponse<CourseDetailDto>>

    @POST("api/courses/{courseId}/favorite")
    suspend fun favoriteCourse(@Path("courseId") courseId: Long): Response<ApiResponse<Map<String, Any>>>

    @DELETE("api/courses/{courseId}/favorite")
    suspend fun disFavoriteCourse(@Path("courseId") courseId: Long): Response<ApiResponse<Map<String, Any>>>

}

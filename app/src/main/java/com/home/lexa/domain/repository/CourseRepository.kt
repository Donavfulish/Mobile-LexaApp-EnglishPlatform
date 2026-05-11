package com.home.lexa.domain.repository


import com.home.lexa.domain.models.AllCoursePaginationResponse
import com.home.lexa.domain.models.CourseDetailDto
import com.home.lexa.domain.models.GetFeaturedCourseResponse
import com.home.lexa.domain.models.GetStudyingCourseResponse
import com.home.lexa.domain.models.SearchInfo
import com.home.lexa.domain.models.Topic
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface CourseRepository {
    suspend fun getAllCourses(searchInfo: SearchInfo, nextCursor: Long?): Result<AllCoursePaginationResponse>
    suspend fun getAllTopics(): Result<List<Topic>>
    suspend fun createCourse(dataPart: RequestBody, imagePart: MultipartBody.Part?): Result<Long>
    suspend fun editCourse(courseId: Long, dataPart: RequestBody, imagePart: MultipartBody.Part?): Result<Boolean>
    suspend fun deleteCourse(courseId: Long): Result<Boolean>
    suspend fun getFavoriteCourses(searchInfo: SearchInfo, nextCursor: Long?): Result<AllCoursePaginationResponse>
    suspend fun getCourseDetail(courseId: Long): Result<CourseDetailDto?>
    suspend fun getFeaturedCourses(): Result<List<GetFeaturedCourseResponse>>
    suspend fun getTopStudiedCourses(): Result<List<GetFeaturedCourseResponse>>
    suspend fun getStudyingCourses(): Result<List<GetStudyingCourseResponse>>

    suspend fun getMyCourses(searchInfo: SearchInfo, nextCursor: Long?): Result<AllCoursePaginationResponse>
    suspend fun getLearningCourses(searchInfo: SearchInfo, nextCursor: Long?): Result<AllCoursePaginationResponse>
    suspend fun favoriteCourse(courseId: Long ): Result<Boolean>
    suspend fun disFavoriteCourse(courseId: Long): Result<Boolean>
    suspend fun getCourseSuggestions(query: String?): Result<List<String>?>
    }

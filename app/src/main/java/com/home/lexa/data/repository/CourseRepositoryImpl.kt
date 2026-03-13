package com.home.lexa.data.repository

import com.home.lexa.data.remote.CourseApiService
import com.home.lexa.domain.models.Course
import com.home.lexa.domain.models.CreateCourseRequest
import com.home.lexa.domain.repository.CourseRepository

class CourseRepositoryImpl(
    private val apiService: CourseApiService
) : CourseRepository {

    override suspend fun getCourses(): Result<List<Course>> {
        return try {
            val response = apiService.getCourses()
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                // Thành công: bóc tách dữ liệu ra và trả về
                Result.success(body.data ?: emptyList())
            } else {
                // Thất bại từ Backend (Ví dụ lỗi 400 do validation)
                Result.failure(Exception(body?.message ?: "Lỗi từ máy chủ"))
            }
        } catch (e: Exception) {
            // Lỗi do mất mạng, không connect được server...
            Result.failure(Exception("Không thể kết nối. Vui lòng kiểm tra mạng!"))
        }
    }

    override suspend fun createCourse(request: CreateCourseRequest): Result<Long> {
        return try {
            val response = apiService.createCourse(request)
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                val newId = body.data?.get("id") ?: throw Exception("Không lấy được ID")
                Result.success(newId)
            } else {
                Result.failure(Exception(body?.message ?: "Tạo khóa học thất bại"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
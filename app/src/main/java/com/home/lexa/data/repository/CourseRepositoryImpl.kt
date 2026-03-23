package com.home.lexa.data.repository

import com.home.lexa.data.remote.CourseApiService
import com.home.lexa.domain.models.CreateCourseRequest
import com.home.lexa.domain.models.ShortCourseDto
import com.home.lexa.domain.models.SpeakingCourseDetailDto
import com.home.lexa.domain.models.GetStudyingCourseResponse
import com.home.lexa.domain.repository.CourseRepository
import com.home.lexa.domain.models.GetFeaturedCourseResponse
class CourseRepositoryImpl(
    private val apiService: CourseApiService
) : CourseRepository {

    override suspend fun getAllCourses(): Result<List<ShortCourseDto>> {
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
    override  suspend fun getFeaturedCourses(): Result<List<GetFeaturedCourseResponse>>{
        return try {
            val response = apiService.getFeaturedCourses()
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                Result.success(body.data ?: emptyList())
            } else {
                Result.failure(Exception(body?.message ?: "Lấy khóa học nổi bật thất bại"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun getStudyingCourses():Result<List<GetStudyingCourseResponse>>{
        return try {
            val response = apiService.getStudyingCourses()
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                Result.success(body.data ?: emptyList())
            } else {
                Result.failure(Exception(body?.message ?: "Lấy khóa học đang học thất bại"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun getTopStudiedCourses():Result<List<GetFeaturedCourseResponse>>{
        return try {
            val response = apiService.getTopStudiedCourses()
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                Result.success(body.data ?: emptyList())
            } else {
                Result.failure(Exception(body?.message ?: "Lấy khóa học đang học thất bại"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSpeakingDayCourse(courseId: Long): Result<SpeakingCourseDetailDto?> {
        return try {
            val response = apiService.getSpeakingDayCourse(courseId)
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.message ?: "Lỗi từ máy chủ"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối: ${e.message}"))
        }
    }

    override suspend fun getFavoriteDecks(userId: Int): Result<List<ShortCourseDto>> {
        return try {
            val response = apiService.getFavoriteDecks(userId)
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
}
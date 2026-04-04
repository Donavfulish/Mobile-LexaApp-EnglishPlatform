package com.home.lexa.data.repository

import com.home.lexa.data.remote.CourseApiService
import com.home.lexa.di.AppMemoryCache
import com.home.lexa.domain.models.CreateCourseRequest
import com.home.lexa.domain.models.EditCourseRequest
import com.home.lexa.domain.models.ShortCourseDto
import com.home.lexa.domain.models.SpeakingCourseDetailDto
import com.home.lexa.domain.models.GetStudyingCourseResponse
import com.home.lexa.domain.models.Topic
import com.home.lexa.domain.repository.CourseRepository
import com.home.lexa.domain.models.GetFeaturedCourseResponse

class CourseRepositoryImpl(
    private val apiService: CourseApiService
) : CourseRepository {

    override suspend fun getAllCourses(): Result<List<ShortCourseDto>> {
        return try {
            val courses: List<ShortCourseDto>? = AppMemoryCache.get("getAllCourses");
            if (courses != null){
                 Result.success(courses);
            }
            else {
                val response = apiService.getCourses()
                val body = response.body()

                if (response.isSuccessful && body?.success == true) {
                    // Thành công: bóc tách dữ liệu ra và trả về
                    val data = body.data ?: emptyList()
                    AppMemoryCache.put("getAllCourses", data);
                    Result.success(data);


                } else {
                    // Thất bại từ Backend (Ví dụ lỗi 400 do validation)
                     Result.failure(Exception(body?.message ?: "Lỗi từ máy chủ"))
                }
            }

        } catch (e: Exception) {
            // Lỗi do mất mạng, không connect được server...
             Result.failure(Exception("Không thể kết nối. Vui lòng kiểm tra mạng!"))
        }
    }

    override suspend fun getAllTopics(): Result<List<Topic>> {
        return try {
            val response = apiService.getTopics()
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                Result.success(body.data ?: emptyList())
            } else {
                Result.failure(Exception(body?.message ?: "Lấy danh sách chủ đề thất bại"))
            }
        } catch (e: Exception) {
            Result.failure(e)
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
    override suspend fun editCourse(courseId: Long, request: EditCourseRequest): Result<Boolean> {
        return try {
            val response = apiService.editCourse(courseId, request)
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {

                Result.success(true)
            } else {
                Result.failure(Exception(body?.message ?: "Chỉnh sửa khóa học thất bại"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun deleteCourse(courseId: Long): Result<Boolean> {
        return try {
            val response = apiService.deleteCourse(courseId)
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                Result.success(true)
            } else {
                Result.failure(Exception(body?.message ?: "Xóa khóa học thất bại"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override  suspend fun getFeaturedCourses(): Result<List<GetFeaturedCourseResponse>>{
        return try {
            val courses: List<GetFeaturedCourseResponse>? = AppMemoryCache.get("getFeaturedCourses");
            if (courses != null){
               return Result.success(courses);
            }

            val response = apiService.getFeaturedCourses()
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                val data = body.data ?: emptyList();
                AppMemoryCache.put("getFeaturedCourses", data);
                Result.success(data);
            } else {
                Result.failure(Exception(body?.message ?: "Lấy khóa học nổi bật thất bại"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun getStudyingCourses():Result<List<GetStudyingCourseResponse>>{
        return try {
            val courses: List<GetStudyingCourseResponse>? = AppMemoryCache.get("getStudyingCourses");
            if (courses != null){
               return Result.success(courses);
            }
            val response = apiService.getStudyingCourses()
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                val data = body.data ?: emptyList();
                AppMemoryCache.put("getStudyingCourses", data);
                Result.success(data);
            } else {
                Result.failure(Exception(body?.message ?: "Lấy khóa học đang học thất bại"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun getTopStudiedCourses():Result<List<GetFeaturedCourseResponse>>{
        return try {
            val courses: List<GetFeaturedCourseResponse>? = AppMemoryCache.get("getTopStudiedCourses");
            if (courses != null){
                return Result.success(courses);
            }
            val response = apiService.getTopStudiedCourses()
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                val data = body.data ?: emptyList();
                AppMemoryCache.put("getTopStudiedCourses", data);
                Result.success(data);

            } else {
                Result.failure(Exception(body?.message ?: "Lấy khóa học đang học thất bại"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSpeakingDayCourse(courseId: Long): Result<SpeakingCourseDetailDto?> {
        return try {
            val courses: SpeakingCourseDetailDto? = AppMemoryCache.get("getSpeakingDayCourse_${courseId}");
            if (courses != null){
                return Result.success(courses);
            }
            val response = apiService.getSpeakingDayCourse(courseId)
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                val data = body.data;
                AppMemoryCache.put("getTopStudiedCourses_${courseId}", data as Any);
                Result.success(data);
            } else {
                Result.failure(Exception(body?.message ?: "Lỗi từ máy chủ"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối: ${e.message}"))
        }
    }

    override suspend fun getFavoriteDecks(): Result<List<ShortCourseDto>> {
        return try {
            val courses: List<ShortCourseDto>? = AppMemoryCache.get("getFavoriteDecks");
            if (courses != null){
                return Result.success(courses);
            }
            val response = apiService.getFavoriteDecks()
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                // Thành công: bóc tách dữ liệu ra và trả về

                val data = body.data ?: emptyList();
                AppMemoryCache.put("getFavoriteDecks", data);
                Result.success(data);
            } else {
                // Thất bại từ Backend (Ví dụ lỗi 400 do validation)
                Result.failure(Exception(body?.message ?: "Lỗi từ máy chủ"))
            }
        } catch (e: Exception) {
            // Lỗi do mất mạng, không connect được server...
            Result.failure(Exception("Không thể kết nối. Vui lòng kiểm tra mạng!"))
        }
    }

    override  suspend fun getLearningCourses(): Result<List<ShortCourseDto>>{
        return try {
            val courses: List<ShortCourseDto>? = AppMemoryCache.get("getLearningCourses");
            if (courses != null){
                return Result.success(courses);
            }
            val response = apiService.getLearningCourses();
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                // Thành công: bóc tách dữ liệu ra và trả về
                val data = body.data ?: emptyList();
                AppMemoryCache.put("getLearningCourses", data);
                Result.success(data);
            } else {
                // Thất bại từ Backend (Ví dụ lỗi 400 do validation)
                Result.failure(Exception(body?.message ?: "Lỗi từ máy chủ"))
            }
        } catch (e: Exception) {
            // Lỗi do mất mạng, không connect được server...
            Result.failure(Exception("Không thể kết nối. Vui lòng kiểm tra mạng!"))
        }
    }

    override suspend fun getMyCourses(): Result<List<ShortCourseDto>> {
        return try {
            val courses: List<ShortCourseDto>? = AppMemoryCache.get("getMyCourses");
            if (courses != null){
                return Result.success(courses);
            }
            val response = apiService.getMyCourses();
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                // Thành công: bóc tách dữ liệu ra và trả về
                val data = body.data ?: emptyList();
                AppMemoryCache.put("getMyCourses", data);
                Result.success(data);
            } else {
                // Thất bại từ Backend (Ví dụ lỗi 400 do validation)
                Result.failure(Exception(body?.message ?: "Lỗi từ máy chủ"))
            }
        } catch (e: Exception) {
            // Lỗi do mất mạng, không connect được server...
            Result.failure(Exception("Không thể kết nối. Vui lòng kiểm tra mạng!"))
        }


    }
    override suspend fun favoriteCourse(courseId: Long): Result<Boolean> {
        return try {
            val response = apiService.favoriteCourse(courseId)
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                Result.success(true)
            } else {
                Result.failure(Exception(body?.message ?: "Yêu thích khóa học thất bại"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun disFavoriteCourse(courseId: Long): Result<Boolean> {
        return try {
            val response = apiService.disFavoriteCourse(courseId)
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {

                Result.success(true)
            } else {
                Result.failure(Exception(body?.message ?: "Bỏ yêu thích khóa học thất bại"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

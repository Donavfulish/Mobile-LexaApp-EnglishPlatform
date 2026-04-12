package com.home.lexa.data.repository

import android.util.Log
import com.home.lexa.data.remote.CourseApiService
import com.home.lexa.di.AppMemoryCache
import com.home.lexa.domain.models.AllCoursePaginationResponse
import com.home.lexa.domain.models.CreateCourseRequest
import com.home.lexa.domain.models.EditCourseRequest
import com.home.lexa.domain.models.ShortCourseDto
import com.home.lexa.domain.models.SpeakingCourseDetailDto
import com.home.lexa.domain.models.GetStudyingCourseResponse
import com.home.lexa.domain.models.Topic
import com.home.lexa.domain.repository.CourseRepository
import com.home.lexa.domain.models.GetFeaturedCourseResponse
import com.home.lexa.domain.models.SearchInfo

class CourseRepositoryImpl(
    private val apiService: CourseApiService
) : CourseRepository {

    private fun generateCacheKey(type: String, searchInfo: SearchInfo): String {
        val q = searchInfo.query ?: ""
        val sort = searchInfo.sortBy ?: ""
        val order = searchInfo.order ?: ""
        return "${type}_${q}_${sort}_${order}"
    }

    override suspend fun getAllCourses(
        searchInfo: SearchInfo,
        nextCursor: Long?
    ): Result<AllCoursePaginationResponse> {
        return try {
            val cacheKey = generateCacheKey("getAllCourses", searchInfo)
            val isFirstPage = nextCursor == null

            if (isFirstPage) {
                val cachedResponse: AllCoursePaginationResponse? = AppMemoryCache.get(cacheKey)
                if (cachedResponse != null && cachedResponse.data.isNotEmpty()) {
                    return Result.success(cachedResponse)
                }
            }

            val response = apiService.getCourses(
                query = searchInfo.query,
                sort = searchInfo.sortBy,
                order = searchInfo.order,
                limit = searchInfo.limit?.toString(),
                next_id = nextCursor?.toString(),
            )
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                val apiPaginationData = body.data ?: throw Exception("Dữ liệu data trong body bị null")
                val newCourses = apiPaginationData.data

                val finalCourses = if (isFirstPage) {
                    newCourses
                } else {
                    val oldCache: AllCoursePaginationResponse? = AppMemoryCache.get(cacheKey)
                    val oldCourses = oldCache?.data ?: emptyList()
                    oldCourses + newCourses
                }
                val updatedResponse = apiPaginationData.copy(data = finalCourses)
                AppMemoryCache.put(cacheKey, updatedResponse)
                Result.success(updatedResponse)
            } else {
                val errorMsg = body?.message ?: "Lỗi từ máy chủ: ${response.code()}"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
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
                Log.d("Đã xoá cache create", "Cache create đã được xoá")
                AppMemoryCache.remove("getAllCourses");
                AppMemoryCache.remove("getMyCourses");
                AppMemoryCache.remove("getLearningCourses");
                AppMemoryCache.remove("getFeaturedCourses");
                AppMemoryCache.remove("getStudyingCourses");
                AppMemoryCache.remove("getTopStudiedCourses");
                val newId = body.data ?: throw Exception("Không lấy được ID")
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
                Log.d("Đã xoá cache update", "Cache update đã được xoá")
                AppMemoryCache.remove("getAllCourses");
                AppMemoryCache.remove("getMyCourses");
                AppMemoryCache.remove("getLearningCourses");
                AppMemoryCache.remove("getFeaturedCourses");
                AppMemoryCache.remove("getStudyingCourses");
                AppMemoryCache.remove("getTopStudiedCourses");
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
                Log.d("Đã xoá cache delete", "Cache delete đã được xoá")
                AppMemoryCache.removePrefix("getAllCourses");
                AppMemoryCache.removePrefix("getMyCourses");
                AppMemoryCache.removePrefix("getLearningCourses");
                AppMemoryCache.remove("getFeaturedCourses");
                AppMemoryCache.remove("getStudyingCourses");
                AppMemoryCache.remove("getTopStudiedCourses");
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
                AppMemoryCache.put("getSpeakingDayCourse_${courseId}", data as Any);
                Result.success(data);
            } else {
                Result.failure(Exception(body?.message ?: "Lỗi từ máy chủ"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối: ${e.message}"))
        }
    }

    override suspend fun getFavoriteCourses(
        searchInfo: SearchInfo,
        nextCursor: Long?
    ): Result<AllCoursePaginationResponse> {
        return try {
            val cacheKey = generateCacheKey("getFavoriteCourses", searchInfo)
            val isFirstPage = nextCursor == null

            if (isFirstPage) {
                val cachedResponse: AllCoursePaginationResponse? = AppMemoryCache.get(cacheKey)
                if (cachedResponse != null && cachedResponse.data.isNotEmpty()) {
                    return Result.success(cachedResponse)
                }
            }

            val response = apiService.getFavoriteCourses(
                query = searchInfo.query,
                sort = searchInfo.sortBy,
                order = searchInfo.order,
                limit = searchInfo.limit?.toString(),
                next_id = nextCursor?.toString(),
            )
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                val apiPaginationData = body.data ?: throw Exception("Dữ liệu data trong body bị null")
                val newCourses = apiPaginationData.data

                val finalCourses = if (isFirstPage) {
                    newCourses
                } else {
                    val oldCache: AllCoursePaginationResponse? = AppMemoryCache.get(cacheKey)
                    val oldCourses = oldCache?.data ?: emptyList()
                    oldCourses + newCourses
                }
                val updatedResponse = apiPaginationData.copy(data = finalCourses)
                AppMemoryCache.put(cacheKey, updatedResponse)
                Result.success(updatedResponse)
            } else {
                val errorMsg = body?.message ?: "Lỗi từ máy chủ: ${response.code()}"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



    override suspend fun getLearningCourses(
        searchInfo: SearchInfo,
        nextCursor: Long?
    ): Result<AllCoursePaginationResponse> {
        return try {
            val cacheKey = generateCacheKey("getLearningCourses", searchInfo)
            val isFirstPage = nextCursor == null

            if (isFirstPage) {
                val cachedResponse: AllCoursePaginationResponse? = AppMemoryCache.get(cacheKey)
                if (cachedResponse != null && cachedResponse.data.isNotEmpty()) {
                    return Result.success(cachedResponse)
                }
            }

            val response = apiService.getLearningCourses(
                query = searchInfo.query,
                sort = searchInfo.sortBy,
                order = searchInfo.order,
                limit = searchInfo.limit?.toString(),
                next_id = nextCursor?.toString(),
            )
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                val apiPaginationData = body.data ?: throw Exception("Dữ liệu data trong body bị null")
                val newCourses = apiPaginationData.data

                val finalCourses = if (isFirstPage) {
                    newCourses
                } else {
                    val oldCache: AllCoursePaginationResponse? = AppMemoryCache.get(cacheKey)
                    val oldCourses = oldCache?.data ?: emptyList()
                    oldCourses + newCourses
                }
                val updatedResponse = apiPaginationData.copy(data = finalCourses)
                AppMemoryCache.put(cacheKey, updatedResponse)
                Result.success(updatedResponse)
            } else {
                val errorMsg = body?.message ?: "Lỗi từ máy chủ: ${response.code()}"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMyCourses(
        searchInfo: SearchInfo,
        nextCursor: Long?
    ): Result<AllCoursePaginationResponse> {
        return try {
            val cacheKey = generateCacheKey("getMyCourses", searchInfo)
            val isFirstPage = nextCursor == null

            if (isFirstPage) {
                val cachedResponse: AllCoursePaginationResponse? = AppMemoryCache.get(cacheKey)
                if (cachedResponse != null && cachedResponse.data.isNotEmpty()) {
                    return Result.success(cachedResponse)
                }
            }

            val response = apiService.getMyCourses(
                query = searchInfo.query,
                sort = searchInfo.sortBy,
                order = searchInfo.order,
                limit = searchInfo.limit?.toString(),
                next_id = nextCursor?.toString(),
            )
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                val apiPaginationData = body.data ?: throw Exception("Dữ liệu data trong body bị null")
                val newCourses = apiPaginationData.data

                val finalCourses = if (isFirstPage) {
                    newCourses
                } else {
                    val oldCache: AllCoursePaginationResponse? = AppMemoryCache.get(cacheKey)
                    val oldCourses = oldCache?.data ?: emptyList()
                    oldCourses + newCourses
                }
                val updatedResponse = apiPaginationData.copy(data = finalCourses)
                AppMemoryCache.put(cacheKey, updatedResponse)
                Result.success(updatedResponse)
            } else {
                val errorMsg = body?.message ?: "Lỗi từ máy chủ: ${response.code()}"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
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

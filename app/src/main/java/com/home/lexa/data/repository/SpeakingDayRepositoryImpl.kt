package com.home.lexa.data.repository

import android.util.Log
import com.home.lexa.data.remote.SpeakingDayApiService
import com.home.lexa.di.AppMemoryCache
import com.home.lexa.domain.models.AllCoursePaginationResponse
import com.home.lexa.domain.models.CreateSpeakingDayRequest
import com.home.lexa.domain.models.EditSpeakingDayRequest
import com.home.lexa.domain.models.ReorderParagraphsRequest
import com.home.lexa.domain.models.ShortCourseDto
import com.home.lexa.domain.models.ShortParagraphSpeakingDayDto
import com.home.lexa.domain.models.SpeakingDayPagination
import com.home.lexa.domain.repository.SpeakingDayRepository

class SpeakingDayRepositoryImpl(
    private val apiService: SpeakingDayApiService
): SpeakingDayRepository {

    override suspend fun getSpeakingDays(
        courseId: Long,
        nextOrder: Long?
    ): Result<SpeakingDayPagination> {
        return try {
            val cacheKey = "getSpeakingDays"
            val isFirstPage = nextOrder == null

            if (isFirstPage) {
                val cachedResponse: SpeakingDayPagination? = AppMemoryCache.get(cacheKey)
                if (cachedResponse != null && cachedResponse.data.isNotEmpty()) {
                    return Result.success(cachedResponse)
                }
            }

            val response = apiService.getSpeakingDays(
                courseId = courseId,
                nextOrder = nextOrder
            )
            val body = response.body()
            if(response.isSuccessful && body?.success == true) {
                val apiPaginationData = body.data ?: throw Exception("Dữ liệu data trong body bị null")
                val newDays = apiPaginationData.data

                val speakingDays = if (isFirstPage) {
                    newDays
                } else {
                    val oldCache: SpeakingDayPagination? = AppMemoryCache.get(cacheKey)
                    val oldDays = oldCache?.data ?: emptyList()
                    oldDays + newDays
                }
                val updatedResponse = apiPaginationData.copy(data = speakingDays)
                AppMemoryCache.put(cacheKey, updatedResponse)
                Result.success(updatedResponse)
            } else {
                val errorMsg = body?.message ?: "Lỗi từ máy chủ: ${response.code()}"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception){
            Result.failure(e)
        }
    }
    override suspend fun getParagraphSpeakingDay(speakingDayId: Long): Result<ShortParagraphSpeakingDayDto?> {
        return try {
            val speakingDay: ShortParagraphSpeakingDayDto? = AppMemoryCache.get("getParagraphSpeakingDay");
            if (speakingDay != null){
                return Result.success(speakingDay);
            }
            val response = apiService.getParagraphSpeakingDay(speakingDayId)
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                val data = body.data;
                AppMemoryCache.put("getParagraphSpeakingDay", data as Any);
                Result.success(body.data ?: null)
            } else {
                Result.failure(Exception(body?.message ?: "Lấy danh sách bài học thất bại"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createSpeakingDay(request: CreateSpeakingDayRequest): Result<Long> {
        return try {
            val response = apiService.createSpeakingDay(request)
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                Log.d("Đã xoá cache create", "Cache create đã được xoá")
                AppMemoryCache.remove("getParagraphSpeakingDay");
                val newId = body.data ?: throw Exception("Không lấy được ID")
                Result.success(newId)
            } else {
                Result.failure(Exception(body?.message ?: "Tạo bài học thất bại"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun editSpeakingDay(speakingDayId: Long, request: EditSpeakingDayRequest): Result<Boolean> {
        return try {
            val response = apiService.editSpeakingDay(speakingDayId, request)
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                Log.d("Đã xoá cache update", "Cache update đã được xoá")
                AppMemoryCache.remove("getParagraphSpeakingDay");
                Result.success(true)
            } else {
                Result.failure(Exception(body?.message ?: "Chỉnh sửa bài học thất bại"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun deleteSpeakingDay(speakingDayId: Long): Result<Boolean> {
        return try {
            val response = apiService.deleteSpeakingDay(speakingDayId)
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                Log.d("Đã xoá cache delete", "Cache delete đã được xoá")
                AppMemoryCache.remove("getParagraphSpeakingDay");
                Result.success(true)
            } else {
                Result.failure(Exception(body?.message ?: "Xóa bài học thất bại"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun reorderParagraphs(speakingDayId: Long, request: ReorderParagraphsRequest): Result<Boolean> {
        return try {
            val response = apiService.reorderParagraphs(speakingDayId, request)
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                Log.d("Đã xoá cache reorder", "Cache getParagraphSpeakingDay đã được xoá")
                AppMemoryCache.remove("getParagraphSpeakingDay")
                Result.success(true)
            } else {
                Result.failure(Exception(body?.message ?: "Cập nhật thứ tự thất bại"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
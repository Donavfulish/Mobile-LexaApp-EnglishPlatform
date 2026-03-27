package com.home.lexa.data.repository

import com.home.lexa.data.remote.CourseApiService
import com.home.lexa.data.remote.SpeakingDayApiService
import com.home.lexa.domain.models.CreateCourseRequest
import com.home.lexa.domain.models.CreateSpeakingDayRequest
import com.home.lexa.domain.models.EditCourseRequest
import com.home.lexa.domain.models.EditSpeakingDayRequest
import com.home.lexa.domain.repository.SpeakingDayRepository

class SpeakingDayRepositoryImpl(
    private val apiService: SpeakingDayApiService
): SpeakingDayRepository {
    override suspend fun createSpeakingDay(request: CreateSpeakingDayRequest): Result<Long> {
        return try {
            val response = apiService.createSpeakingDay(request)
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                val newId = body.data?.get("id") ?: throw Exception("Không lấy được ID")
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
                Result.success(true)
            } else {
                Result.failure(Exception(body?.message ?: "Xóa bài học thất bại"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
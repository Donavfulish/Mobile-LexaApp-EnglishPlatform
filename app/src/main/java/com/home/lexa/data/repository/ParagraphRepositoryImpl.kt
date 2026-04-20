package com.home.lexa.data.repository

import androidx.navigation.ui.AppBarConfiguration
import com.home.lexa.data.remote.ParagraphApiService
import com.home.lexa.di.AppMemoryCache
import com.home.lexa.domain.models.CreateParagraphRequest
import com.home.lexa.domain.models.ParagraphResponseDto
import com.home.lexa.domain.models.ParagraphResultResponseDto
import com.home.lexa.domain.models.SubmitBulkDailyResultRequest
import com.home.lexa.domain.models.UpdateParagraphRequest
import com.home.lexa.domain.models.UpdateParagraphResultRequest
import com.home.lexa.domain.repository.ParagraphRepository

class ParagraphRepositoryImpl (
    private val apiService: ParagraphApiService
) : ParagraphRepository {

    override suspend fun createParagraph(request: CreateParagraphRequest): Result<ParagraphResponseDto> {
        return try {
            val response = apiService.createParagraph(request)
            val body = response.body()

            if (response.isSuccessful && body?.success == true && body.data != null) {
                AppMemoryCache.remove("getParagraphSpeakingDay_${request.speakingDayId}")
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.message ?: "Lỗi từ máy chủ khi tạo đoạn văn"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Không thể kết nối. Vui lòng kiểm tra mạng!"))
        }
    }

    override suspend fun updateParagraph(
        speakingDayId: Long,
        paragraphId: Long,
        request: UpdateParagraphRequest
    ): Result<ParagraphResponseDto> {
        return try {
            val response = apiService.updateParagraphInfo(paragraphId, request)
            val body = response.body()

            if (response.isSuccessful && body?.success == true && body.data != null) {
                AppMemoryCache.remove("getParagraphSpeakingDay_${speakingDayId}")
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.message ?: "Lỗi từ máy chủ khi cập nhật đoạn văn"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Không thể kết nối. Vui lòng kiểm tra mạng!"))
        }
    }

    override suspend fun deleteParagraph(speakingDayId: Long, paragraphId: Long): Result<Unit> {
        return try {
            val response = apiService.deleteParagraph(paragraphId)
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                AppMemoryCache.remove("getParagraphSpeakingDay_${speakingDayId}")
                Result.success(Unit)
            } else {
                Result.failure(Exception(body?.message ?: "Lỗi từ máy chủ khi xóa đoạn văn"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Không thể kết nối. Vui lòng kiểm tra mạng!"))
        }
    }

    override suspend fun updateParagraphResult(request: UpdateParagraphResultRequest): Result<ParagraphResultResponseDto> {
        return try {
            val response = apiService.updateParagraphResult(request)
            val body = response.body()

            if (response.isSuccessful && body?.success == true && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.message ?: "Lỗi từ máy chủ khi cập nhật kết quả"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Không thể kết nối. Vui lòng kiểm tra mạng!"))
        }
    }

    override suspend fun submitBulkParagraphResults(request: SubmitBulkDailyResultRequest): Result<Boolean> {
        return try {
            val response = apiService.submitBulkResults(request)
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                Result.success(true)
            } else {
                Result.failure(Exception(body?.message ?: "Lỗi từ máy chủ khi lưu tiến độ"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Không thể kết nối. Vui lòng kiểm tra mạng!"))
        }
    }
}
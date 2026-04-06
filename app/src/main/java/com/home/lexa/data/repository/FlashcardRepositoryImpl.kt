package com.home.lexa.data.repository

import com.home.lexa.data.remote.FlashcardApiService
import com.home.lexa.di.AppMemoryCache
import com.home.lexa.domain.models.CreateFlashcardRequest
import com.home.lexa.domain.models.DetailFlashcard
import com.home.lexa.domain.models.DetailFlashcardWithResult
import com.home.lexa.domain.models.UpdateFlashcardRequest
import com.home.lexa.domain.models.UpdateFlashcardResultRequest
import com.home.lexa.domain.repository.FlashcardRepository

class FlashcardRepositoryImpl(
    private val apiService: FlashcardApiService
) : FlashcardRepository {

    override suspend fun getAllFlashcard(deckId: Long): Result<List<DetailFlashcard>> {
        return try {
            val flashcards: List<DetailFlashcard>? = AppMemoryCache.get("getAllFlashcard_${deckId}");
            if (flashcards != null){
                return Result.success(flashcards);
            }
            val response = apiService.getAllFlashcard(deckId)
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                val data = body.data ?: emptyList();
                AppMemoryCache.put("getAllFlashcard_${deckId}", data);
                Result.success(data);
            } else {
                Result.failure(Exception(body?.message ?: "Lỗi từ máy chủ"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối: ${e.message}"))
        }
    }

    override suspend fun getAllFlashcardWithResult(deckId: Long): Result<List<DetailFlashcardWithResult>> {
        return try {
            val response = apiService.getAllFlashcardWithResult(deckId)
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                val data = body.data ?: emptyList()

                val cacheKey = "FLASHCARD_DECK_RESULT_$deckId"
                AppMemoryCache.put(cacheKey, data)
                Result.success(data)
            } else {
                Result.failure(Exception(body?.message ?: "Lỗi từ máy chủ"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi: ${e.message}"))
        }
    }

    override suspend fun createFlashcard(request: CreateFlashcardRequest): Result<Long> {
        return try {
            val response = apiService.createFlashcard(request.deckId.toLong(), request)
            val body = response.body()

            if (response.isSuccessful && body?.success == true && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.message ?: "Lỗi khi tạo flashcard"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối: ${e.message}"))
        }
    }

    override suspend fun updateFlashcard(request: UpdateFlashcardRequest): Result<Boolean> {
        return try {
            val response = apiService.updateFlashcard(request.deckId, request)
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                AppMemoryCache.remove("getAllFlashcard_${request.deckId}")
                Result.success(body.data ?: true)
            } else {
                Result.failure(Exception(body?.message ?: "Lỗi khi cập nhật flashcard"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối: ${e.message}"))
        }
    }

    override suspend fun deleteFlashcard(flashcardId: Long): Result<Boolean> {
        return try {
            val response = apiService.deleteFlashcard(flashcardId)
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                Result.success(body.data ?: true)
            } else {
                Result.failure(Exception(body?.message ?: "Lỗi khi xóa flashcard"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối: ${e.message}"))
        }
    }

    override suspend fun updateFlashcardResults(deckId: Long, request: UpdateFlashcardResultRequest): Result<Boolean> {
        return try {
            val response = apiService.updateFlashcardResults(deckId, request)
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                Result.success(true)
            } else {
                Result.failure(Exception(body?.message ?: "Lỗi khi cập nhật kết quả flashcard"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối: ${e.message}"))
        }
    }
}

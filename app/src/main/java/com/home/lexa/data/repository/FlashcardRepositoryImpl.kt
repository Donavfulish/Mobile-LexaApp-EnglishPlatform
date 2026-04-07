package com.home.lexa.data.repository

import android.util.Log
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
            Log.d("Flashcard Cache", "result: $flashcards")
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
            Log.e("Error Cache Flashcard", "Error", e);
            Result.failure(Exception("Lỗi kết nối: ${e.message}"))
        }
    }

    override suspend fun getAllFlashcardWithResult(deckId: Long): Result<List<DetailFlashcardWithResult>> {
        return try {
            val flashcardResults: List<DetailFlashcardWithResult>? = AppMemoryCache.get("getAllFlashcardWithResult_${deckId}");
            if (flashcardResults != null){
                return Result.success(flashcardResults);
            }
            val response = apiService.getAllFlashcardWithResult(deckId)
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                val data = body.data ?: emptyList()

                val cacheKey = "getAllFlashcardWithResult_$deckId"
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
            Log.d("FlashcardRepositoryImpl", "Response body: $body")

            if (response.isSuccessful && body?.success == true && body.data != null) {
                Log.d("Đã xoá cache create", "Cache create đã được xoá_${request.deckId}")
                AppMemoryCache.remove("getAllFlashcard_${request.deckId}");
                AppMemoryCache.remove("getAllDecks");
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.message ?: "Lỗi khi tạo flashcard"))
            }
        } catch (e: Exception) {
            Log.e("ERROR_CATCH_FLASHCARD", "Cache create đã được xoá", e)
            Result.failure(Exception("Lỗi kết nối: ${e.message}"))
        }
    }

    override suspend fun updateFlashcard(request: UpdateFlashcardRequest): Result<Boolean> {
        return try {
            val response = apiService.updateFlashcard(request.deckId, request)
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                Log.d("Đã xoá cache update", "Cache update đã được xoá")
                AppMemoryCache.remove("getAllFlashcard_${request.deckId}");
                Result.success(body.data ?: true)
            } else {
                Result.failure(Exception(body?.message ?: "Lỗi khi cập nhật flashcard"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối: ${e.message}"))
        }
    }

    override suspend fun deleteFlashcard(flashcardId: Long, deckId: Long): Result<Boolean> {
        return try {
            val response = apiService.deleteFlashcard(flashcardId)
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                Log.d("Đã xoá cache delete", "Cache delete: getAllFlashcard_${flashcardId}")
                AppMemoryCache.remove("getAllFlashcard_${deckId}");
                AppMemoryCache.remove("getAllDecks");
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
                Log.d("Đã xoá cache update result", "Cache update result đã được xoá")
                AppMemoryCache.remove("getAllFlashcardWithResult_${deckId}");
                Result.success(true)
            } else {
                Result.failure(Exception(body?.message ?: "Lỗi khi cập nhật kết quả flashcard"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối: ${e.message}"))
        }
    }
}

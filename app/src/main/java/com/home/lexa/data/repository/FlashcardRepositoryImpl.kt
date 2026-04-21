package com.home.lexa.data.repository

import android.util.Log
import com.home.lexa.data.remote.FlashcardApiService
import com.home.lexa.di.AppMemoryCache
import com.home.lexa.domain.models.AllCoursePaginationResponse
import com.home.lexa.domain.models.AllFlashcardPaginationResponse
import com.home.lexa.domain.models.AllFlashcardResultPaginationResponse
import com.home.lexa.domain.models.CreateFlashcardRequest
import com.home.lexa.domain.models.DetailFlashcard
import com.home.lexa.domain.models.DetailFlashcardWithResult
import com.home.lexa.domain.models.SearchInfo
import com.home.lexa.domain.models.UpdateFlashcardRequest
import com.home.lexa.domain.models.UpdateFlashcardResultRequest
import com.home.lexa.domain.repository.FlashcardRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class FlashcardRepositoryImpl(
    private val apiService: FlashcardApiService
) : FlashcardRepository {

    private fun generateCacheKey(type: String, searchInfo: SearchInfo, deckId: Long): String {
        val q = searchInfo.query ?: ""
        val sort = searchInfo.sortBy ?: ""
        val order = searchInfo.order ?: ""
        return "${type}_${deckId}_${q}_${sort}_${order}"
    }

    override suspend fun getFlashcardSuggestions(query: String?): Result<List<String>?> {
        return try {
            val response = apiService.getFlashcardSuggestions(query)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Lỗi lấy gợi ý Flashcard"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllFlashcard(
        deckId: Long,
        searchInfo: SearchInfo,
        nextCursor: Long?): Result<AllFlashcardPaginationResponse> {
        return try {
            val cacheKey = generateCacheKey("getAllFlashcard", searchInfo, deckId)
            val isFirstPage = nextCursor == null

            if (isFirstPage) {
                val cachedResponse: AllFlashcardPaginationResponse? = AppMemoryCache.get(cacheKey)
                if (cachedResponse != null && cachedResponse.data.isNotEmpty()) {
                    return Result.success(cachedResponse)
                }
            }

            val response = apiService.getAllFlashcard(
                deckId = deckId,
                query = searchInfo.query,
                sort = searchInfo.sortBy,
                order = searchInfo.order,
                limit = searchInfo.limit?.toString(),
                next_id = nextCursor?.toString(),
            )
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                val apiPaginationData = body.data ?: throw Exception("Dữ liệu data trong body bị null")
                val newFlashcards = apiPaginationData.data

                val finalFlashcards = if (isFirstPage) {
                    newFlashcards
                } else {
                    val oldCache: AllFlashcardPaginationResponse? = AppMemoryCache.get(cacheKey)
                    val oldFlashcards = oldCache?.data ?: emptyList()
                    oldFlashcards + newFlashcards
                }
                val updatedResponse = apiPaginationData.copy(data = finalFlashcards)
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

    override suspend fun getAllFlashcardWithResult(
        deckId: Long,
        searchInfo: SearchInfo,
        nextCursor: Long?
    ): Result<AllFlashcardResultPaginationResponse> {
        return try {
            val cacheKey = generateCacheKey("getAllFlashcardWithResult", searchInfo, deckId)
            val isFirstPage = nextCursor == null

            if (isFirstPage) {
                val cachedResponse: AllFlashcardResultPaginationResponse? = AppMemoryCache.get(cacheKey)
                if (cachedResponse != null && cachedResponse.data.isNotEmpty()) {
                    return Result.success(cachedResponse)
                }
            }

            val response = apiService.getAllFlashcardWithResult(
                deckId = deckId,
                query = searchInfo.query,
                sort = searchInfo.sortBy,
                order = searchInfo.order,
                limit = searchInfo.limit?.toString(),
                next_id = nextCursor?.toString(),
            )
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                val apiPaginationData = body.data ?: throw Exception("Dữ liệu data trong body bị null")
                val newFlashcards = apiPaginationData.data

                val finalResults = if (isFirstPage) {
                    newFlashcards
                } else {
                    val oldCache: AllFlashcardResultPaginationResponse? = AppMemoryCache.get(cacheKey)
                    val oldFlashcards = oldCache?.data ?: emptyList()
                    oldFlashcards + newFlashcards
                }
                val updatedResponse = apiPaginationData.copy(data = finalResults)
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

    override suspend fun createFlashcard(deckId: Long, request: RequestBody, imageUri: MultipartBody.Part?): Result<Long> {
        return try {
            val response = apiService.createFlashcard(deckId, request, imageUri)
            val body = response.body()
            Log.d("FlashcardRepositoryImpl", "Response body: $body")

            if (response.isSuccessful && body?.success == true && body.data != null) {
                Log.d("Đã xoá cache create", "Cache create đã được xoá_${deckId}")
                AppMemoryCache.removePrefix("getAllFlashcard_${deckId}");
                AppMemoryCache.removePrefix("getAllFlashcardWithResult_${deckId}");
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

    override suspend fun updateFlashcard(deckId: Long, request: RequestBody, imageUri: MultipartBody.Part?): Result<Boolean> {
        return try {
            val response = apiService.updateFlashcard(deckId, request, imageUri)
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                Log.d("Đã xoá cache update", "Cache update đã được xoá")
                AppMemoryCache.removePrefix("getAllFlashcard_${deckId}");
                AppMemoryCache.removePrefix("getAllFlashcardWithResult_${deckId}");
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
            val response = apiService.deleteFlashcard(deckId, flashcardId)
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                Log.d("Đã xoá cache delete", "Cache delete: getAllFlashcard_${flashcardId}")
                AppMemoryCache.removePrefix("getAllFlashcard_${deckId}");
                AppMemoryCache.removePrefix("getAllFlashcardWithResult_${deckId}");
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
                AppMemoryCache.removePrefix("getAllFlashcardWithResult_${deckId}");
                Result.success(true)
            } else {
                Result.failure(Exception(body?.message ?: "Lỗi khi cập nhật kết quả flashcard"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối: ${e.message}"))
        }
    }
}

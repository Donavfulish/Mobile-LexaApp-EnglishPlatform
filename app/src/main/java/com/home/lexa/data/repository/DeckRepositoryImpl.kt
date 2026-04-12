package com.home.lexa.data.repository

import android.util.Log
import com.home.lexa.data.remote.DeckApiService
import com.home.lexa.di.AppMemoryCache
import com.home.lexa.domain.models.AllCoursePaginationResponse
import com.home.lexa.domain.models.AllDeckPaginationResponse
import com.home.lexa.domain.models.CreateDeckRequest
import com.home.lexa.domain.models.CreateDeckResultRequest
import com.home.lexa.domain.models.DeckDto
import com.home.lexa.domain.models.DeckResult
import com.home.lexa.domain.models.SearchInfo
import com.home.lexa.domain.models.Topic
import com.home.lexa.domain.models.UpdateDeckRequest
import com.home.lexa.domain.models.UpdateDeckResultRequest
import com.home.lexa.domain.repository.DeckRepository

class DeckRepositoryImpl(
    private val apiService: DeckApiService
) : DeckRepository {

    private fun generateCacheKey(type: String, searchInfo: SearchInfo): String {
        val q = searchInfo.query ?: ""
        val sort = searchInfo.sortBy ?: ""
        val order = searchInfo.order ?: ""
        return "${type}_${q}_${sort}_${order}"
    }

    override suspend fun getAllDecks(
        searchInfo: SearchInfo,
        nextCursor: Long?
    ): Result<AllDeckPaginationResponse> {
        return try {
            val cacheKey = generateCacheKey("getAllDecks", searchInfo)
            val isFirstPage = nextCursor == null

            if (isFirstPage) {
                val cachedResponse: AllDeckPaginationResponse? = AppMemoryCache.get(cacheKey)
                if (cachedResponse != null && cachedResponse.data.isNotEmpty()) {
                    return Result.success(cachedResponse)
                }
            }

            val response = apiService.getAllDecks(
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
                    val oldCache: AllDeckPaginationResponse? = AppMemoryCache.get(cacheKey)
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

    override suspend fun getFavoriteDecks(
        searchInfo: SearchInfo,
        nextCursor: Long?
    ): Result<AllCoursePaginationResponse> {
        return try {
            val cacheKey = generateCacheKey("getFavoriteDecks", searchInfo)
            val isFirstPage = nextCursor == null

            if (isFirstPage) {
                val cachedResponse: AllCoursePaginationResponse? = AppMemoryCache.get(cacheKey)
                if (cachedResponse != null && cachedResponse.data.isNotEmpty()) {
                    return Result.success(cachedResponse)
                }
            }

            val response = apiService.getFavoriteDecks(
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

    override suspend fun getDeckResult(deckId: Long): Result<DeckResult?> {
        return try {
            val decks: DeckResult? = AppMemoryCache.get("getDeckResult_${deckId}");
            if (decks != null){
                return Result.success(decks);
            }
            val response = apiService.getDeckResult(deckId)
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                val data = body.data;
                AppMemoryCache.put("getDeckResult_${deckId}", data as Any);
                Result.success(data);
            } else {
                Result.failure(Exception(body?.message ?: "Lỗi từ máy chủ"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối: ${e.message}"))
        }
    }
    override suspend fun favoriteDeck(deckId: Long): Result<Boolean> {
        return try {
            val response = apiService.favoriteDeck(deckId)
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                Result.success(true)
            } else {
                Result.failure(Exception(body?.message ?: "Yêu thích bộ từ vựng thất bại"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun disFavoriteDeck(deckId: Long): Result<Boolean> {
        return try {
            val response = apiService.disFavoriteDeck(deckId)
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {

                Result.success(true)
            } else {
                Result.failure(Exception(body?.message ?: "Bỏ yêu thích bộ từ vựng thất bại"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createDeck(request: CreateDeckRequest): Result<Long> {
        return try {

            Log.d("Trang thai create", "Di vao")
            val response = apiService.createDeck(request)
            Log.d("Trang thai create", response.toString() )

            if (response.isSuccessful ) {
                Log.d("Đã xoá cache create", "Cache create đã được xoá")
                AppMemoryCache.removePrefix("getAllDecks");
                val newId: Long = 1;
                Result.success(newId)
            } else {
                Result.failure(Exception( "Lỗi khi tạo deck"))
            }
        } catch (e: Exception) {
            Log.e("CREATE_DECK_ERROR", "Error here", e)
            Result.failure(Exception("Lỗi kết nối: ${e.message}"))
        }
    }

    override suspend fun updateDeck(request: UpdateDeckRequest): Result<Boolean> {
        return try {
            val response = apiService.updateDeck(request.deckId, request)
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                Log.d("Đã xoá cache update", "Cache update đã được xoá")
                AppMemoryCache.removePrefix("getAllDecks");
                Result.success(body.data ?: true)
            } else {

                Result.failure(Exception(body?.message ?: "Lỗi khi cập nhật deck"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối: ${e.message}"))
        }
    }

    override suspend fun deleteDeck(deckId: Long): Result<Boolean> {
        return try {
            val response = apiService.deleteDeck(deckId)
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                Log.d("Đã xoá cache delete", "Cache delete đã được xoá")
                AppMemoryCache.removePrefix("getAllDecks");
                Result.success(body.data ?: true)
            } else {
                Result.failure(Exception(body?.message ?: "Lỗi khi xóa deck"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối: ${e.message}"))
        }
    }

    override suspend fun createDeckResult(request: CreateDeckResultRequest): Result<Boolean> {
        return try {
            val response = apiService.createDeckResult(request.deckId, request)
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                Log.d("Đã xoá cache create result", "Cache create result đã được xoá")
                AppMemoryCache.remove("getDeckResult_${request.deckId}");
                Result.success(body.data ?: true)
            } else {
                Result.failure(Exception(body?.message ?: "Lỗi khi tạo kết quả deck"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối: ${e.message}"))
        }
    }

    override suspend fun updateDeckResult(request: UpdateDeckResultRequest): Result<Boolean> {
        return try {
            val response = apiService.updateDeckResult(request.deckId, request)
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                Log.d("Đã xoá cache update  result", "Cache update result đã được xoá")
                AppMemoryCache.remove("getDeckResult_${request.deckId}");
                Result.success(body.data ?: true)
            } else {
                Result.failure(Exception(body?.message ?: "Lỗi khi cập nhật kết quả deck"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối: ${e.message}"))
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
}

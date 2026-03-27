package com.home.lexa.data.repository

import com.home.lexa.data.remote.DeckApiService
import com.home.lexa.domain.models.DeckDto
import com.home.lexa.domain.models.DeckResult
import com.home.lexa.domain.repository.DeckRepository

class DeckRepositoryImpl(private val apiService: DeckApiService
) : DeckRepository {
    override suspend fun getAllDecks(): Result<List<DeckDto>>{
        return try {
            val response = apiService.getAllDecks()
            val body = response.body()

            if (response.isSuccessful && body?.success == true) {
                Result.success(body.data ?: emptyList())
            } else {
                Result.failure(Exception(body?.message ?: "Lỗi từ máy chủ"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối: ${e.message}"))
        }
    }

    override suspend fun getDeckResult(deckId: Long): Result<DeckResult?>{
        return try {
            val response = apiService.getDeckResult(deckId)
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
}
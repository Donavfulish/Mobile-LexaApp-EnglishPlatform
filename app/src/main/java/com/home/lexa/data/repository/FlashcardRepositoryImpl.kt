package com.home.lexa.data.repository

import com.home.lexa.data.remote.DeckApiService
import com.home.lexa.data.remote.FlashcardApiService
import com.home.lexa.domain.models.DetailFlashcard
import com.home.lexa.domain.repository.DeckRepository
import com.home.lexa.domain.repository.FlashcardRepository

class FlashcardRepositoryImpl(private val apiService: FlashcardApiService
) : FlashcardRepository {
    override suspend fun getAllFlashcard(deckId: Long): Result<List<DetailFlashcard>>{
        return try {
            val response = apiService.getAllFlashcard(deckId)
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
}

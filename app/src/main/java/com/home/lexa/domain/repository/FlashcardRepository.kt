package com.home.lexa.domain.repository

import com.home.lexa.domain.models.CreateFlashcardRequest
import com.home.lexa.domain.models.DetailFlashcard
import com.home.lexa.domain.models.UpdateFlashcardRequest

interface FlashcardRepository {
    suspend fun getAllFlashcard(deckId: Long): Result<List<DetailFlashcard>>
//    suspend fun updateFlashcard(request: UpdateFlashcardRequest): Result<Boolean>
//    suspend fun deleteFlashcard(flashcardId: Long):  Result<Boolean>
//    suspend fun createFlashcard(request: CreateFlashcardRequest) : Result<Long>
}
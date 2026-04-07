package com.home.lexa.domain.repository

import com.home.lexa.domain.models.CreateFlashcardRequest
import com.home.lexa.domain.models.DetailFlashcard
import com.home.lexa.domain.models.DetailFlashcardWithResult
import com.home.lexa.domain.models.UpdateFlashcardRequest
import com.home.lexa.domain.models.UpdateFlashcardResultRequest

interface FlashcardRepository {
    suspend fun getAllFlashcard(deckId: Long): Result<List<DetailFlashcard>>
    suspend fun getAllFlashcardWithResult(deckId: Long): Result<List<DetailFlashcardWithResult>>
    suspend fun updateFlashcard(request: UpdateFlashcardRequest): Result<Boolean>
    suspend fun deleteFlashcard(flashcardId: Long, deckId: Long):  Result<Boolean>
    suspend fun createFlashcard(request: CreateFlashcardRequest) : Result<Long>
    suspend fun updateFlashcardResults(deckId: Long, request: UpdateFlashcardResultRequest): Result<Boolean>
}
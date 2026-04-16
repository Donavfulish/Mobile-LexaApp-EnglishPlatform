package com.home.lexa.domain.repository

import com.home.lexa.domain.models.AllFlashcardPaginationResponse
import com.home.lexa.domain.models.AllFlashcardResultPaginationResponse
import com.home.lexa.domain.models.CreateFlashcardRequest
import com.home.lexa.domain.models.DetailFlashcard
import com.home.lexa.domain.models.DetailFlashcardWithResult
import com.home.lexa.domain.models.SearchInfo
import com.home.lexa.domain.models.UpdateFlashcardRequest
import com.home.lexa.domain.models.UpdateFlashcardResultRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface FlashcardRepository {
    suspend fun getAllFlashcard(deckId: Long, searchInfo: SearchInfo, nextCursor: Long?): Result<AllFlashcardPaginationResponse>
    suspend fun getAllFlashcardWithResult(deckId: Long, searchInfo: SearchInfo, nextCursor: Long?): Result<AllFlashcardResultPaginationResponse>
    suspend fun updateFlashcard(deckId: Long, request: RequestBody, imageUri: MultipartBody.Part?): Result<Boolean>
    suspend fun deleteFlashcard(flashcardId: Long, deckId: Long):  Result<Boolean>
    suspend fun createFlashcard(deckId: Long, request: RequestBody, imageUri: MultipartBody.Part?) : Result<Long>
    suspend fun updateFlashcardResults(deckId: Long, request: UpdateFlashcardResultRequest): Result<Boolean>
}

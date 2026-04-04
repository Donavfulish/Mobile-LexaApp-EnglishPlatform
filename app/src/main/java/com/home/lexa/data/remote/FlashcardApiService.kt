package com.home.lexa.data.remote

import com.home.lexa.core.network.ApiResponse
import com.home.lexa.domain.models.CreateFlashcardRequest
import com.home.lexa.domain.models.DetailFlashcard
import com.home.lexa.domain.models.DetailFlashcardWithResult
import com.home.lexa.domain.models.UpdateFlashcardRequest
import com.home.lexa.domain.models.UpdateFlashcardResultRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface FlashcardApiService {
    @GET("/api/decks/{deckId}/flashcards")
    suspend fun getAllFlashcard(
        @Path("deckId") deckId: Long
    ): Response<ApiResponse<List<DetailFlashcard>>>

    @GET("api/decks/{deckId}/flashcards/result")
    suspend fun getAllFlashcardWithResult(
        @Path("deckId") deckId: Long
    ): Response<ApiResponse<List<DetailFlashcardWithResult>>>

    @POST("/api/decks/{deckId}/flashcards")
    suspend fun createFlashcard(
        @Path("deckId") deckId: Long,
        @Body request: CreateFlashcardRequest
    ): Response<ApiResponse<Long>>

    @PATCH("/api/decks/{deckId}/flashcards")
    suspend fun updateFlashcard(
        @Path("deckId") id: Long,
        @Body request: UpdateFlashcardRequest
    ): Response<ApiResponse<Boolean>>

    @DELETE("/api/decks/{deckId}/flashcards/{flashcardId}")
    suspend fun deleteFlashcard(
        @Path("flashcardId") flashcardId: Long
    ): Response<ApiResponse<Boolean>>

    @PATCH("api/decks/{deckId}/flashcards/result")
    suspend fun updateFlashcardResults(
        @Path("deckId") deckId: Long,
        @Body request: UpdateFlashcardResultRequest
    ): Response<ApiResponse<Boolean>>
}

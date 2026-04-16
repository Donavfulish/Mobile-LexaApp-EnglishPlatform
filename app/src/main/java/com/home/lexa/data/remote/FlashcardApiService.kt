package com.home.lexa.data.remote

import com.home.lexa.core.network.ApiResponse
import com.home.lexa.domain.models.AllFlashcardPaginationResponse
import com.home.lexa.domain.models.AllFlashcardResultPaginationResponse
import com.home.lexa.domain.models.CreateFlashcardRequest
import com.home.lexa.domain.models.DetailFlashcard
import com.home.lexa.domain.models.DetailFlashcardWithResult
import com.home.lexa.domain.models.UpdateFlashcardRequest
import com.home.lexa.domain.models.UpdateFlashcardResultRequest
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface FlashcardApiService {
    @GET("/api/decks/{deckId}/flashcards")
    suspend fun getAllFlashcard(
        @Path("deckId") deckId: Long,
        @Query("query") query: String? = null,
        @Query("sort") sort: String? = null,
        @Query("order") order: String? = null,
        @Query("limit") limit: String? = null,
        @Query("next_id") next_id: String? = null,
    ): Response<ApiResponse<AllFlashcardPaginationResponse>>

    @GET("api/decks/{deckId}/flashcards/result")
    suspend fun getAllFlashcardWithResult(
        @Path("deckId") deckId: Long,
        @Query("query") query: String? = null,
        @Query("sort") sort: String? = null,
        @Query("order") order: String? = null,
        @Query("limit") limit: String? = null,
        @Query("next_id") next_id: String? = null,
    ): Response<ApiResponse<AllFlashcardResultPaginationResponse>>

    @Multipart
    @POST("/api/decks/{deckId}/flashcards")
    suspend fun createFlashcard(
        @Path("deckId") deckId: Long,
        @Part("data") request: RequestBody,
        @Part imageUri: MultipartBody.Part?
    ): Response<ApiResponse<Long>>

    @Multipart
    @PATCH("/api/decks/{deckId}/flashcards")
    suspend fun updateFlashcard(
        @Path("deckId") deckId: Long,
        @Part("data") request: RequestBody,
        @Part imageUri: MultipartBody.Part?
    ): Response<ApiResponse<Boolean>>

    @DELETE("/api/decks/{deckId}/flashcards/{flashcardId}")
    suspend fun deleteFlashcard(
        @Path("deckId") deckId: Long,
        @Path("flashcardId") flashcardId: Long
    ): Response<ApiResponse<Boolean>>

    @PATCH("api/decks/{deckId}/flashcards/result")
    suspend fun updateFlashcardResults(
        @Path("deckId") deckId: Long,
        @Body request: UpdateFlashcardResultRequest
    ): Response<ApiResponse<Boolean>>
}

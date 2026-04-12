package com.home.lexa.data.remote

import com.home.lexa.core.network.ApiResponse
import com.home.lexa.domain.models.AllCoursePaginationResponse
import com.home.lexa.domain.models.AllDeckPaginationResponse
import com.home.lexa.domain.models.CreateDeckRequest
import com.home.lexa.domain.models.CreateDeckResultRequest
import com.home.lexa.domain.models.DeckDto
import com.home.lexa.domain.models.DeckResult
import com.home.lexa.domain.models.Topic
import com.home.lexa.domain.models.UpdateDeckRequest
import com.home.lexa.domain.models.UpdateDeckResultRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface DeckApiService {
    @GET("/api/user/me/decks")
    suspend fun getAllDecks(
        @Query("query") query: String? = null,
        @Query("sort") sort: String? = null,
        @Query("order") order: String? = null,
        @Query("limit") limit: String? = null,
        @Query("next_id") next_id: String? = null
    ): Response<ApiResponse<AllDeckPaginationResponse>>

    @GET("/api/user/me/decks/favorite")
    suspend fun getFavoriteDecks(
        @Query("query") query: String? = null,
        @Query("sort") sort: String? = null,
        @Query("order") order: String? = null,
        @Query("limit") limit: String? = null,
        @Query("next_id") next_id: String? = null,
    ): Response<ApiResponse<AllCoursePaginationResponse>>

    @GET("api/user/me/decks/result/{deckId}")
    suspend fun getDeckResult(
        @Path("deckId") deckId: Long
    ): Response<ApiResponse<DeckResult>>


    @POST("/api/user/me/decks/result/{deckId}")
    suspend fun createDeckResult(
        @Path("deckId") deckId: Long,
        @Body request: CreateDeckResultRequest
    ): Response<ApiResponse<Boolean>>

    @PATCH("/api/user/me/decks/result/{deckId}")
    suspend fun updateDeckResult(
        @Path("deckId") deckId: Long,
        @Body request: UpdateDeckResultRequest
    ): Response<ApiResponse<Boolean>>

    @POST("/api/user/me/decks")
    suspend fun createDeck(
        @Body request: CreateDeckRequest
    ): Response<ApiResponse<Long>>

    @PATCH("/api/user/me/decks/{deckId}")
    suspend fun updateDeck(
        @Path("deckId") deckId: Long,
        @Body request: UpdateDeckRequest
    ): Response<ApiResponse<Boolean>>

    @DELETE("/api/user/me/decks/{deckId}")
    suspend fun deleteDeck(
        @Path("deckId") deckId: Long
    ): Response<ApiResponse<Boolean>>

    @POST("api/decks/{deckId}/favorite")
    suspend fun favoriteDeck(@Path("deckId") deckId: Long): Response<ApiResponse<Map<String, Any>>>

    @DELETE("api/decks/{deckId}/favorite")
    suspend fun disFavoriteDeck(@Path("deckId") deckId: Long): Response<ApiResponse<Map<String, Any>>>

    // ==== TOPIC ====
    @GET("api/topics")
    suspend fun getTopics(): Response<ApiResponse<List<Topic>>>
}

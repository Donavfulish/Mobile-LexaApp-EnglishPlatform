package com.home.lexa.domain.repository

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

interface DeckRepository {
    suspend fun getAllDecks(searchInfo: SearchInfo, nextCursor: Long?): Result<AllDeckPaginationResponse>
    suspend fun getFavoriteDecks(searchInfo: SearchInfo, nextCursor: Long?): Result<AllCoursePaginationResponse>

    suspend fun getDeckResult(deckId: Long): Result<DeckResult?>
    suspend fun favoriteDeck(deckId: Long ): Result<Boolean>
    suspend fun disFavoriteDeck(deckId: Long): Result<Boolean>
    suspend fun createDeck(request: CreateDeckRequest): Result<Long>
    suspend fun updateDeck(request: UpdateDeckRequest): Result<Boolean>
    suspend fun deleteDeck(deckId: Long): Result<Boolean>
    suspend fun createDeckResult(request: CreateDeckResultRequest): Result<Boolean>
    suspend fun updateDeckResult(request: UpdateDeckResultRequest): Result<Boolean>
    suspend fun getDeckSuggestions(query: String?): Result<List<String>?>

    // ==== TOPIC ====
    suspend fun getAllTopics(): Result<List<Topic>>
}

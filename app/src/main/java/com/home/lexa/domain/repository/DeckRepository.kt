package com.home.lexa.domain.repository

import com.home.lexa.domain.models.Course
import com.home.lexa.domain.models.CreateDeckRequest
import com.home.lexa.domain.models.DeckDto
import com.home.lexa.domain.models.DeckResult
import com.home.lexa.domain.models.UpdateDeckRequest

interface DeckRepository {
    suspend fun getAllDecks(): Result<List<DeckDto>>
    suspend fun getDeckResult(deckId: Long): Result<DeckResult?>
//    suspend fun createDeck(request: CreateDeckRequest): Result<Long>
//    suspend fun updateDeck(request: UpdateDeckRequest): Result<Boolean>
//    suspend fun deleteDeck(deckId: Long): Result<Boolean>
}
package com.home.lexa.domain.models

import kotlinx.serialization.Serializable
enum class VocabType { NONE, A1, A2, B1, B2, C1, C2 }

@Serializable
data class DetailFlashcard(
    val id: Long,
    val word: String,
    val transcription: String,
    val type: String,
    val deckId: Long,
    val imageUrl: String?,
    val audioUrl: String?,
    val meaning: String,
    val example: String?,
    val partOfSpeech: String
)

@Serializable
data class AllFlashcardPaginationResponse(
    val data: List<DetailFlashcard>,
    val searchInfo: SearchInfo,
    val nextCursor: Long?= null,
    val totalItem: Long)


@Serializable
data class AllFlashcardResultPaginationResponse(
    val data: List<DetailFlashcardWithResult>,
    val searchInfo: SearchInfo,
    val nextCursor: Long?= null,
    val totalItem: Long)




@Serializable
data class DetailFlashcardWithResult (
    val flashCard: DetailFlashcard,
    val result: String,
)

@Serializable
data class CreateFlashcardRequest(
    val word: String,
    val transcription: String,
    val typeId: Int,
    val deckId: Long,
    val imageUrl: String? = null,
    val audioUrl: String? = null,
    val meaning: String,
    val example: String? = null,
    val partOfSpeechId: Int
)

@Serializable
data class UpdateFlashcardRequest(
    val flashcardId: Long,
    @Transient
    val deckId: Long,
    val word: String? = null,
    val transcription: String? = null,
    val typeId: Int? = null,
    val imageUrl: String? = null,
    val audioUrl: String? = null,
    val meaning: String? = null,
    val example: String? = null,
    val partOfSpeechId: Int? = null
)

@Serializable
data class FlashcardResultItem(
    val flashcardId: Long,
    val status: String?
)

@Serializable
data class UpdateFlashcardResultRequest(
    val deckId: Long,
    val results: List<FlashcardResultItem>
)



sealed class WordUiState {
    object Idle : WordUiState()
    object Loading : WordUiState()
    data class Success(val data: String) : WordUiState()
    data class Error(val message: String) : WordUiState()
}
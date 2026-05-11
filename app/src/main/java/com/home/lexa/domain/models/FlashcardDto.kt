package com.home.lexa.domain.models

import android.content.Context
import androidx.annotation.StringRes
import com.home.lexa.R
import kotlinx.serialization.Serializable
enum class VocabType(@StringRes val nameRes: Int) {
    NONE(R.string.level_none),
    A1(R.string.level_a1),
    A2(R.string.level_a2),
    B1(R.string.level_b1),
    B2(R.string.level_b2),
    C1(R.string.level_c1),
    C2(R.string.level_c2);

    companion object {

        fun getLocalizedNames(context: Context): List<String> {
            return entries.map { context.getString(it.nameRes) }
        }

        fun fromLocalizedName(context: Context, name: String): VocabType {
            return entries.find { context.getString(it.nameRes) == name } ?: NONE
        }
    }
}

enum class PartOfSpeech(val id: Int, @StringRes val nameRes: Int) {
    NOUN(1, R.string.pos_noun),
    VERB(2, R.string.pos_verb),
    ADJECTIVE(3, R.string.pos_adjective),
    ADVERB(4, R.string.pos_adverb);

    companion object {

        fun getLocalizedNames(context: Context): List<String> {
            return entries.map { context.getString(it.nameRes) }
        }

        fun getIdFromLocalizedName(context: Context, name: String): Int {
            return entries.find { context.getString(it.nameRes) == name }?.id ?: 1 // Mặc định trả về 1 (Danh từ) nếu không tìm thấy
        }

        fun fromId(id: Int): PartOfSpeech? {
            return entries.find { it.id == id }
        }
    }
}
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
    val partOfSpeechId: Int
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
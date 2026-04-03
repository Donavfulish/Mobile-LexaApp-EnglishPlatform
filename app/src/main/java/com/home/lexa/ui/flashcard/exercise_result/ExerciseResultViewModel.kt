package com.home.lexa.ui.flashcard.exercise_result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.lexa.di.AppMemoryCache
import com.home.lexa.domain.models.DetailFlashcardWithResult
import com.home.lexa.domain.repository.FlashcardRepository
import kotlinx.coroutines.launch

class ExerciseResultViewModel(private val repository: FlashcardRepository) : ViewModel() {
    fun saveProgressToApi(deckId: Long, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            // val cacheKey = "FLASHCARD_DECK_RESULT_$deckId"
            // val allCards = AppMemoryCache.get<List<DetailFlashcardWithResult>>(cacheKey)
            // TODO: Gọi API batch update kết quả lên server tại đây
            onComplete(true)
        }
    }
}
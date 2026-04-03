package com.home.lexa.ui.flashcard.exercise_result

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.lexa.di.AppMemoryCache
import com.home.lexa.domain.models.DetailFlashcardWithResult
import com.home.lexa.domain.models.FlashcardResultItem
import com.home.lexa.domain.models.UpdateDeckResultRequest
import com.home.lexa.domain.models.UpdateFlashcardResultRequest
import com.home.lexa.domain.repository.DeckRepository
import com.home.lexa.domain.repository.FlashcardRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ExerciseResultViewModel(
    private val flashcardRepository: FlashcardRepository,
    private val deckRepository: DeckRepository
) : ViewModel() {

    fun saveProgressToApi(deckId: Long, remembered: Int, forgotten: Int, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val cacheKey = "FLASHCARD_DECK_RESULT_$deckId"
            val allCards = AppMemoryCache.get<List<DetailFlashcardWithResult>>(cacheKey)

            if (allCards.isNullOrEmpty()) {
                onComplete(false)
                return@launch
            }

            Log.d("ExerciseResultViewModel", "Bắt đầu đồng bộ ${allCards.size} cards lên BE...")

            // 1. Chuẩn bị Request cho Flashcard Results
            // Chuyển đổi "NULL" string thành giá trị null thực sự để BE parse thành Enum null
            val flashcardItems = allCards.map {
                FlashcardResultItem(
                    flashcardId = it.flashCard.id,
                    status = if (it.result == "NULL") null else it.result
                )
            }
            val flashcardRequest = UpdateFlashcardResultRequest(deckId, flashcardItems)

            // 2. Chuẩn bị Request cho Deck Results
            val deckRequest = UpdateDeckResultRequest(
                deckId = deckId,
                rememberedCount = remembered,
                forgottenCount = forgotten
            )

            try {
                // 3. Gọi 2 API song song (Concurrent)
                val flashcardDeferred = async { flashcardRepository.updateFlashcardResults(deckId, flashcardRequest) }
                val deckDeferred = async { deckRepository.updateDeckResult(deckRequest) }

                // Chờ cả 2 hoàn thành
                val flashcardResult = flashcardDeferred.await()
                val deckResult = deckDeferred.await()

                // 4. Kiểm tra kết quả
                val isSuccess = flashcardResult.isSuccess && deckResult.isSuccess

                if (isSuccess) {
                    Log.d("ExerciseResultViewModel", "Lưu kết quả thành công cả 2 bảng!")
                } else {
                    Log.e("ExerciseResultViewModel", "Có lỗi xảy ra khi lưu: FC=${flashcardResult.isSuccess}, Deck=${deckResult.isSuccess}")
                }

                onComplete(isSuccess)

            } catch (e: Exception) {
                Log.e("ExerciseResultViewModel", "Lỗi Exception khi lưu progress: ${e.message}")
                onComplete(false)
            }
        }
    }
}
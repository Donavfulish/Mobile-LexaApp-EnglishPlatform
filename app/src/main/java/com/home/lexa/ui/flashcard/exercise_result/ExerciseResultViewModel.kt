package com.home.lexa.ui.flashcard.exercise_result

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.lexa.data.local.UserManager
import com.home.lexa.di.AppMemoryCache
import com.home.lexa.domain.models.CreateDeckResultRequest
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
    private val deckRepository: DeckRepository,
    private val userManager: UserManager
) : ViewModel() {

    fun saveProgressToApi(deckId: Long, remembered: Int, forgotten: Int, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val cacheKey = "FLASHCARD_DECK_RESULT_$deckId"
            val allCards = AppMemoryCache.get<List<DetailFlashcardWithResult>>(cacheKey)
            val hasFlashcardPayload = !allCards.isNullOrEmpty()
            val rememberedFromCards = allCards?.count { it.result == "REMEMBER" } ?: 0
            val forgottenFromCards = allCards?.count { it.result == "FORGOTTEN" } ?: 0
            val finalRemembered = if (hasFlashcardPayload) rememberedFromCards else remembered
            val finalForgotten = if (hasFlashcardPayload) forgottenFromCards else forgotten

            Log.d(
                "ExerciseResultViewModel",
                "Bắt đầu đồng bộ deckId=$deckId, cards=${allCards?.size ?: 0}, rem=$finalRemembered, forg=$finalForgotten"
            )

            // 1. Chuẩn bị Request cho Flashcard Results
            // Chuyển đổi "NULL" string thành giá trị null thực sự để BE parse thành Enum null
            val flashcardItems = allCards?.map {
                FlashcardResultItem(
                    flashcardId = it.flashCard.id,
                    status = if (it.result == "NULL") null else it.result
                )
            } ?: emptyList()
            val flashcardRequest = UpdateFlashcardResultRequest(deckId, flashcardItems)

            // 2. Chuẩn bị Request cho Deck Results
            val deckRequest = UpdateDeckResultRequest(
                deckId = deckId,
                rememberedCount = finalRemembered,
                forgottenCount = finalForgotten
            )

            try {
                // 3. Gọi 2 API song song (Concurrent)
                val flashcardDeferred = async {
                    if (hasFlashcardPayload) {
                        flashcardRepository.updateFlashcardResults(deckId, flashcardRequest)
                    } else {
                        Log.w("ExerciseResultViewModel", "Không có cache flashcard để sync chi tiết, bỏ qua updateFlashcardResults")
                        Result.success(true)
                    }
                }
                val deckDeferred = async {
                    val updateResult = deckRepository.updateDeckResult(deckRequest)
                    if (updateResult.isSuccess) {
                        updateResult
                    } else {
                        Log.w(
                            "ExerciseResultViewModel",
                            "updateDeckResult thất bại, thử createDeckResult cho deckId=$deckId"
                        )
                        val userId = userManager.getUserId()
                        if (userId <= 0) {
                            Log.e(
                                "ExerciseResultViewModel",
                                "Không thể createDeckResult vì userId không hợp lệ: $userId"
                            )
                            Result.failure(Exception("Invalid userId"))
                        } else {
                        deckRepository.createDeckResult(
                            CreateDeckResultRequest(
                                deckId = deckId,
                                userId = userId,
                                rememberedCount = finalRemembered,
                                forgottenCount = finalForgotten
                            )
                        )
                        }
                    }
                }

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
package com.home.lexa.ui.course.vocabulary_flashcard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.lexa.domain.models.DeckResult
import com.home.lexa.domain.models.DetailFlashcard
import com.home.lexa.domain.repository.DeckRepository
import com.home.lexa.domain.repository.FlashcardRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class VocabularyFlashcardViewModel(
    private val deckRepository: DeckRepository,
    private val flashcardRepository: FlashcardRepository,
) : ViewModel() {
    private val _deckResultData = MutableLiveData<DeckResult>()
    val deckResultData: LiveData<DeckResult> get() = _deckResultData
    
    private val _flashcardDetailData = MutableLiveData<List<DetailFlashcard>>()
    val flashcardDetailData: LiveData<List<DetailFlashcard>> get() = _flashcardDetailData
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun loadFlashcardDetail(deckId: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                // Chạy song song 2 API
                val flashcardsDeferred = async { flashcardRepository.getAllFlashcard(deckId) }
                val deckResultDeferred = async { deckRepository.getDeckResult(deckId) }

                // Đợi cả 2 hoàn thành
                val flashcardsResult = flashcardsDeferred.await()
                val deckResult = deckResultDeferred.await()

                deckResult.onSuccess { data ->
                    data?.let { _deckResultData.value = it }
                }.onFailure {
                    _deckResultData.value = null
                }

                flashcardsResult.onSuccess { list ->
                    _flashcardDetailData.value = list ?: emptyList()
                }.onFailure {
                    _flashcardDetailData.value = emptyList()
                }

            } catch (e: Exception) {

            } finally {
                _isLoading.value = false
            }
        }
    }
    fun deleteFlashcard(flashcardId: Long, deckId: Long) { // Hoặc kiểu Int/String tùy cấu trúc của bạn
        viewModelScope.launch {
            try {


                flashcardRepository.deleteFlashcard(flashcardId) // Gọi hàm xóa từ Repository
                loadFlashcardDetail(deckId)

            } catch (e: Exception) {
                // Xử lý lỗi (báo lỗi qua LiveData/SharedFlow để Fragment hiện Toast)
            } finally {
                // _isLoading.value = false
            }
        }
    }
}

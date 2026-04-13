package com.home.lexa.ui.course.vocabulary_flashcard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.lexa.domain.models.DeckResult
import com.home.lexa.domain.models.DetailFlashcard
import com.home.lexa.domain.models.DetailFlashcardWithResult
import com.home.lexa.domain.models.Topic
import com.home.lexa.domain.models.UpdateDeckRequest
import com.home.lexa.domain.repository.DeckRepository
import com.home.lexa.domain.repository.FlashcardRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class VocabularyFlashcardViewModel(
    private val deckRepository: DeckRepository,
    private val flashcardRepository: FlashcardRepository,
) : ViewModel() {
    private val _deckResultData = MutableLiveData<DeckResult?>()
    val deckResultData: LiveData<DeckResult?> get() = _deckResultData

    private val _deleteResult = MutableLiveData<Result<Unit>>()
    val deleteResult: LiveData<Result<Unit>> get() = _deleteResult

    private val _flashcardDetailData = MutableLiveData<List<DetailFlashcard>>()
    val flashcardDetailData: LiveData<List<DetailFlashcard>> get() = _flashcardDetailData
    private val _flashcardWithResultData = MutableLiveData<List<DetailFlashcardWithResult>>()
    val flashcardWithResultData: LiveData<List<DetailFlashcardWithResult>> get() = _flashcardWithResultData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _topicData = MutableLiveData<List<Topic>>()
    val topicData: LiveData<List<Topic>> get() = _topicData

    fun loadFlashcardDetail(deckId: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                Log.d("loadFlashcardDetail", "detailid: $deckId")
                val flashcardsDeferred = async { flashcardRepository.getAllFlashcard(deckId) }
                val deckResultDeferred = async { deckRepository.getDeckResult(deckId) }

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

    fun updateDeck(request: UpdateDeckRequest) {
        viewModelScope.launch {
            try {
                deckRepository.updateDeck(request)
            } catch (e: Exception) {
                // Xử lý lỗi
            } finally {
                // _isLoading.value = false
            }
        }
    }

    fun deleteFlashcard(flashcardId: Long, deckId: Long) {
        viewModelScope.launch {
            try {
                _deleteResult.postValue(Result.success(Unit))

                flashcardRepository.deleteFlashcard(flashcardId, deckId)
                loadFlashcardDetail(deckId)

            } catch (e: Exception) {
                _deleteResult.postValue(Result.failure(e))
            }
        }
    }

    fun loadTopics() {
        viewModelScope.launch {
            val result = deckRepository.getAllTopics()
            try {
                result.onSuccess { list ->
                    _topicData.value = list
                }.onFailure {
                    _topicData.value = emptyList()
                    Log.e("DEBUG_VM", "Load Topics failed: ${it.message}")
                }
            } catch (e: Exception){
                _topicData.value = emptyList()
                _isLoading.value = false
            }
        }
    }
    fun loadFlashcardsWithResult(deckId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = flashcardRepository.getAllFlashcardWithResult(deckId)

                result.onSuccess { list ->
                    _flashcardWithResultData.value = list ?: emptyList()
                }.onFailure {
                    _flashcardWithResultData.value = emptyList()
                    Log.e("DEBUG_VM", "Lỗi load FlashcardWithResult: ${it.message}")
                }
            } catch (e: Exception) {
                _flashcardWithResultData.value = emptyList()
                Log.e("DEBUG_VM", "Exception load FlashcardWithResult: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}

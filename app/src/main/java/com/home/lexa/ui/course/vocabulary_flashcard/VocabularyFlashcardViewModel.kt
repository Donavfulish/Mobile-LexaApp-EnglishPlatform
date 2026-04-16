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
import com.home.lexa.domain.models.SearchInfo
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

    private val _paginationLoading = MutableLiveData<Boolean>()
    val paginationLoading: LiveData<Boolean> get() = _paginationLoading

    var lastId: Long? = null
    var isLastPage = false
    var currentPages = 0
    var totalPages = 0

    fun loadFlashcardDetail(deckId: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                Log.d("loadFlashcardDetail", "detailid: $deckId")
                val flashcardsDeferred = async { flashcardRepository.getAllFlashcard(deckId, SearchInfo(null, null, null), null) }
                val deckResultDeferred = async { deckRepository.getDeckResult(deckId) }

                val flashcardsResult = flashcardsDeferred.await()
                val deckResult = deckResultDeferred.await()

                deckResult.onSuccess { data ->
                    data?.let { _deckResultData.value = it }
                }.onFailure {
                    _deckResultData.value = null
                }

                flashcardsResult.onSuccess { list ->
                    _flashcardDetailData.value = list.data ?: emptyList()
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
                // Xử lý lỗi (báo lỗi qua LiveData/SharedFlow để Fragment hiện Toast)
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
    fun loadFlashcardsWithResult(isLoadMore: Boolean, deckId: Long, searchInfo: SearchInfo, nextCursor: Long?) {
        if (isLoadMore && (isLastPage || _isLoading.value == true)) return

        if(!isLoadMore){
            lastId =  null
            isLastPage = false
            currentPages = 0
            totalPages = 0
        }
        viewModelScope.launch {
            _paginationLoading.value = true
            try {
                // Gọi API từ Repository
                val result = flashcardRepository.getAllFlashcardWithResult(deckId, searchInfo, nextCursor)
                result.onSuccess { list ->
                    if(!list.data.isNullOrEmpty()){
                        currentPages += list.data.size
                        totalPages = list.totalItem.toInt()
                        lastId = list.data[list.data.size - 1].flashCard.id
                        if(currentPages == totalPages || list.nextCursor == null){
                            isLastPage = true
                        }
                        _flashcardWithResultData.value = list.data
                    } else {
                        _flashcardWithResultData.value = emptyList()
                        isLastPage = true
                    }
                }.onFailure {
                    _flashcardWithResultData.value = emptyList()
                    Log.e("DEBUG_VM", "Lỗi load FlashcardWithResult: ${it.message}")
                }
            } catch (e: Exception) {
                _flashcardWithResultData.value = emptyList()
                Log.e("DEBUG_VM", "Exception load FlashcardWithResult: ${e.message}")
            } finally {
                _paginationLoading.value = false
            }
        }
    }
}

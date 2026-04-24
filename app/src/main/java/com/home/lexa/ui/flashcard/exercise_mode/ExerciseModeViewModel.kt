package com.home.lexa.ui.flashcard.exercise_mode

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.lexa.di.AppMemoryCache
import com.home.lexa.domain.models.ColorLabel
import com.home.lexa.domain.models.DetailFlashcardWithResult
import com.home.lexa.domain.models.SearchInfo
import com.home.lexa.domain.models.Vocabulary
import com.home.lexa.domain.repository.FlashcardRepository
import kotlinx.coroutines.launch

class ExerciseModeViewModel(
    private val repository: FlashcardRepository
) : ViewModel() {

    companion object {
        const val RESULT_REMEMBER = "REMEMBER"
        const val RESULT_FORGOTTEN = "FORGOTTEN"
        const val RESULT_NULL = "NULL"
    }

    private var deckId: Long = -1
    private var cacheKey: String = ""

    private var allCards = mutableListOf<DetailFlashcardWithResult>()
    private var practiceQueue = mutableListOf<DetailFlashcardWithResult>()
    private var currentQueueIndex = 0

    private val _currentCard = MutableLiveData<DetailFlashcardWithResult?>()
    val currentCard: LiveData<DetailFlashcardWithResult?> = _currentCard

    private val _rememberedCount = MutableLiveData<Int>()
    val rememberedCount: LiveData<Int> = _rememberedCount

    private val _forgottenCount = MutableLiveData<Int>()
    val forgottenCount: LiveData<Int> = _forgottenCount

    private val _totalCards = MutableLiveData<Int>()
    val totalCards: LiveData<Int> = _totalCards

    private val _progress = MutableLiveData<Int>()
    val progress: LiveData<Int> = _progress

    private val _isFinished = MutableLiveData<Boolean>()
    val isFinished: LiveData<Boolean> = _isFinished

    // Nhận data khởi tạo từ màn trước và cờ điều hướng
    fun initInitialData(passedDeckId: Long, rem: Int, forg: Int, total: Int, isRetryForgotten: Boolean, isRetryAll: Boolean) {
        deckId = passedDeckId
        cacheKey = "FLASHCARD_DECK_RESULT_$deckId"

        _rememberedCount.value = rem
        _forgottenCount.value = forg
        _totalCards.value = total
        _progress.value = rem

        // Điều hướng dữ liệu: Đọc từ Cache hay Gọi API?
        if (isRetryForgotten) {
            loadFromCache()
            startNewSession(onlyForgotten = true)
        } else if (isRetryAll) {
            loadFromCache()
            resetAndPracticeAll()
        } else {
            // Mới vào lần đầu tiên -> Fetch dữ liệu mới nhất từ Server
            fetchFlashcardsFromApi()
        }
    }

    private fun loadFromCache() {
        val cachedData = AppMemoryCache.get<List<DetailFlashcardWithResult>>(cacheKey)
        if (cachedData != null) {
            allCards = cachedData.toMutableList()
        }
    }

    // GỌI API Ở ĐÂY
    public fun fetchFlashcardsFromApi() {
        viewModelScope.launch {
            try {
                val result = repository.getAllFlashcardWithResult(deckId, SearchInfo(null, null, null, totalCards.value), null)

                result.onSuccess { data ->
                    allCards = data.data.toMutableList()

                    // Lấy về xong thì mới cất vào Cache để quản lý việc vuốt thẻ
                    AppMemoryCache.put(cacheKey, allCards.toList())

                    // Tính toán lại để đảm bảo đồng bộ tuyệt đối với API
                    updateStatsFromAllCards()

                    // Bắt đầu luyện (chỉ các từ chưa thuộc)
                    startNewSession(onlyForgotten = true)
                }.onFailure {
                    // Xử lý lỗi load API nếu cần
                }
            } catch (e: Exception) {
                // Xử lý exception
            }
        }
    }

    private fun startNewSession(onlyForgotten: Boolean) {
        practiceQueue = if (onlyForgotten) {
            allCards.filter { it.result != RESULT_REMEMBER }.toMutableList()
        } else {
            allCards.toMutableList()
        }

        currentQueueIndex = 0

        if (practiceQueue.isEmpty()) {
            _isFinished.value = true
        } else {
            _isFinished.value = false
            loadCardAt(currentQueueIndex)
        }
    }

    private fun loadCardAt(index: Int) {
        if (index < practiceQueue.size) {
            val item = practiceQueue[index]
            _currentCard.value = item

            val alreadyLearnedOutsideQueue = allCards.size - practiceQueue.size
            _progress.value = alreadyLearnedOutsideQueue + index
        } else {
            _progress.value = allCards.size
            _isFinished.value = true
        }
    }

    fun handleSwipe(isRemembered: Boolean) {
        if (currentQueueIndex >= practiceQueue.size) return

        val currentItem = practiceQueue[currentQueueIndex]
        val newResult = if (isRemembered) RESULT_REMEMBER else RESULT_FORGOTTEN

        val indexInAll = allCards.indexOfFirst { it.flashCard.id == currentItem.flashCard.id }
        if (indexInAll != -1) {
            allCards[indexInAll] = allCards[indexInAll].copy(result = newResult)
        }

        AppMemoryCache.put(cacheKey, allCards.toList())
        updateStatsFromAllCards()

        currentQueueIndex++
        loadCardAt(currentQueueIndex)
    }

    private fun updateStatsFromAllCards() {
        _rememberedCount.value = allCards.count { it.result == RESULT_REMEMBER }
        _forgottenCount.value = allCards.count { it.result == RESULT_FORGOTTEN }
        _totalCards.value = allCards.size
    }

    private fun resetAndPracticeAll() {
        allCards = allCards.map { it.copy(result = RESULT_NULL) }.toMutableList()
        AppMemoryCache.put(cacheKey, allCards.toList())
        updateStatsFromAllCards()
        startNewSession(onlyForgotten = false)
    }


}
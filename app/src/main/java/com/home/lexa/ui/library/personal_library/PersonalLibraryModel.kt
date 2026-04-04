package com.home.lexa.ui.library.personal_library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.lexa.domain.models.CreateDeckRequest
import com.home.lexa.domain.models.DeckDto
import com.home.lexa.domain.models.mockUserInfo
import com.home.lexa.domain.repository.DeckRepository
import kotlinx.coroutines.launch

class PersonalLibraryModel(private val repository: DeckRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // LiveData để chứa danh sách Decks
    private val _decks = MutableLiveData<List<DeckDto>>(emptyList())
    val decks: LiveData<List<DeckDto>> get() = _decks

    // Hàm này không cần suspend, gọi phát chạy luôn
    fun fetchAllDecks() {
        if (_decks.value.isNotEmpty()) return
        viewModelScope.launch {
            _isLoading.value = true

            val result = repository.getAllDecks()

            result.onSuccess { list ->
                _decks.value = list
            }.onFailure {
                _decks.value = emptyList()
            }

            _isLoading.value = false
        }
    }
    fun createDeck(title: String) {
        viewModelScope.launch {
            _isLoading.value = true

            val request = CreateDeckRequest(
                title = title,
                creatorId = mockUserInfo.id
            )

            val result = repository.createDeck(request)

            fetchAllDecks()


            _isLoading.value = false
        }
    }
}
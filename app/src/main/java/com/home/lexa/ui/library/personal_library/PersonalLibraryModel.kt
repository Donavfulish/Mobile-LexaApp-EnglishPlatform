package com.home.lexa.ui.library.personal_library

import android.database.Cursor
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.query
import com.home.lexa.domain.models.CreateDeckRequest
import com.home.lexa.domain.models.DeckDto
import com.home.lexa.domain.models.SearchInfo
import com.home.lexa.domain.models.mockUserInfo
import com.home.lexa.domain.repository.DeckRepository
import kotlinx.coroutines.launch

class PersonalLibraryModel(private val repository: DeckRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _decks = MutableLiveData<List<DeckDto>>(emptyList())
    val decks: LiveData<List<DeckDto>> get() = _decks

    var lastId: Long? = null
    var isLastPage = false
    var currentPages = 0
    var totalPages = 0

    fun fetchAllDecks(isLoadMore: Boolean, searchInfo: SearchInfo, nextCursor: Long?) {
        if (isLoadMore && (isLastPage || _isLoading.value == true)) return
        viewModelScope.launch {
            if(!isLoadMore){
                lastId =  null
                isLastPage = false
                currentPages = 0
                totalPages = 0
            }
            _isLoading.value = true
            val result = repository.getAllDecks(searchInfo, nextCursor)

            result.onSuccess { list ->
                currentPages += list.data.size
                totalPages = list.totalItem.toInt()
                lastId = list.nextCursor

                if(currentPages.toLong() == list.totalItem){
                    isLastPage = true
                    lastId = null
                }
                _decks.value = list.data
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

            Log.d("Da fetch nhe ban", "fetching")
            val result = repository.createDeck(request)

            fetchAllDecks(false, SearchInfo(
                query= "",
                sortBy= "",
                order= "",
                limit = 10
            ), null)


            _isLoading.value = false
        }
    }
}
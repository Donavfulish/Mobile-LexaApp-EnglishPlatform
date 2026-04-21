package com.home.lexa.ui.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.lexa.domain.models.SearchInfo
import com.home.lexa.domain.repository.DeckRepository
import com.home.lexa.ui.components.SearchbarFilter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LibraryModel(private val deckRepository: DeckRepository) : ViewModel() {
    private val _searchInfo = MutableLiveData<SearchInfo>(SearchInfo(
        query = "",
        limit = 10
    ))
    val searchInfo: LiveData<SearchInfo> get() = _searchInfo

    private val _suggestions = MutableLiveData<List<String>>(emptyList())
    val suggestions: LiveData<List<String>> get() = _suggestions

    private var searchJob: Job? = null

    fun getSuggestions(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(200)
            val result = deckRepository.getDeckSuggestions(query)
            result.onSuccess { list ->
                _suggestions.value = list
            }.onFailure {
                _suggestions.value = emptyList()
            }
        }
    }

    fun updateSearch(q: String){
        _searchInfo.value = _searchInfo.value?.copy(query = q)
    }

    fun updateFilter(options: SearchbarFilter.FilterOptions){
        _searchInfo.value = _searchInfo.value?.copy(sortBy = options.sortBy, order = options.order)
    }
}

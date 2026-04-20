package com.home.lexa.ui.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.room.util.query
import com.home.lexa.domain.models.SearchInfo
import com.home.lexa.ui.components.SearchbarFilter

class LibraryModel : ViewModel() {
    private val _searchInfo = MutableLiveData<SearchInfo>(SearchInfo(
        query = "",
        limit = 10
    ))
    val searchInfo: LiveData<SearchInfo> get() = _searchInfo

    fun updateSearch(q: String){
        _searchInfo.value = _searchInfo.value.copy(query = q)
    }
    fun updateFilter(options: SearchbarFilter.FilterOptions){
        _searchInfo.value = _searchInfo.value.copy(sortBy = options.sortBy, order = options.order)
    }
}
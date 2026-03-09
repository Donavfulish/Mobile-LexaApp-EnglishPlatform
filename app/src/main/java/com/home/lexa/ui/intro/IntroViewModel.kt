package com.home.lexa.ui.intro

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.lexa.domain.models.IntroData
import com.home.lexa.domain.repository.IntroRepository
import kotlinx.coroutines.launch

class IntroViewModel(private val repository: IntroRepository) : ViewModel() {

    private val _introData = MutableLiveData<IntroData>()
    val introData: LiveData<IntroData> get() = _introData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun loadIntro() {
        viewModelScope.launch {
            _isLoading.value = true
            val data = repository.fetchIntroContent()
            _introData.value = data
            _isLoading.value = false
        }
    }
}

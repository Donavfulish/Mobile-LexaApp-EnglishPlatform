package com.home.lexa.ui.speaking.speaking_practice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.lexa.di.AppMemoryCache
import com.home.lexa.domain.models.ShortParagraphSpeakingDayDto
import com.home.lexa.domain.repository.SpeakingDayRepository
import kotlinx.coroutines.launch

class SpeakingPracticeViewModel(
    private val speakingDayRepository: SpeakingDayRepository
) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _paragraphDetailData = MutableLiveData<ShortParagraphSpeakingDayDto?>()
    val paragraphDetailData: LiveData<ShortParagraphSpeakingDayDto?> get() = _paragraphDetailData

    fun loadParagraphList(speakingDayId: Long){
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val paragraph = speakingDayRepository.getParagraphSpeakingDay(speakingDayId)
                paragraph.onSuccess { data ->
                    _paragraphDetailData.value = data ?: null
                    _isLoading.value = false
                }.onFailure {
                    _paragraphDetailData.value = null
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _paragraphDetailData.value = null
                _isLoading.value = false
            }
        }
    }
}

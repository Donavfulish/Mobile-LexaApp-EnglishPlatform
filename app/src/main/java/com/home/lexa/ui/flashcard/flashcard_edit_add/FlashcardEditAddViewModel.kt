package com.home.lexa.ui.flashcard.flashcard_edit_add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.lexa.domain.models.CreateFlashcardRequest
import com.home.lexa.domain.models.DetailFlashcard
import com.home.lexa.domain.models.UpdateFlashcardRequest
import com.home.lexa.domain.repository.FlashcardRepository
import kotlinx.coroutines.launch

class FlashcardEditAddViewModel(

        private val flashcardRepository: FlashcardRepository,
    ) : ViewModel() {

        private val _flashcardDetailData = MutableLiveData<DetailFlashcard>()
        val flashcardDetailData: LiveData<DetailFlashcard> get() = _flashcardDetailData

        private val _isLoading = MutableLiveData<Boolean>()
        val isLoading: LiveData<Boolean> get() = _isLoading


    // Biến lắng nghe trạng thái lưu thành công hay thất bại
        private val _saveSuccess = MutableLiveData<Boolean>()
        val saveSuccess: LiveData<Boolean> get() = _saveSuccess
    fun createFlashcard(request: CreateFlashcardRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Gọi tới Repository (Giả sử hàm createFlashcard trong repo là suspend function)
                flashcardRepository.createFlashcard(request)
                _saveSuccess.value = true

            } catch (e: Exception) {
                e.printStackTrace()
                _saveSuccess.value = false

            } finally {
                _isLoading.value = false
            }
        }
    }

    // Hàm Cập nhật Flashcard
    fun updateFlashcard(request: UpdateFlashcardRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Gọi tới Repository (Giả sử hàm updateFlashcard trong repo là suspend function)
                flashcardRepository.updateFlashcard(request)
                _saveSuccess.value = true
            } catch (e: Exception) {
                e.printStackTrace()
                _saveSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }
    }
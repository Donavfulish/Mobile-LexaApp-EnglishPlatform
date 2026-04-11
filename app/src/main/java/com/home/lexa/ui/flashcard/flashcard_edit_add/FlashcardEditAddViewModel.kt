package com.home.lexa.ui.flashcard.flashcard_edit_add

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.home.lexa.domain.models.CreateFlashcardRequest
import com.home.lexa.domain.models.DetailFlashcard
import com.home.lexa.domain.models.UpdateFlashcardRequest
import com.home.lexa.domain.repository.FlashcardRepository
import com.home.lexa.ui.utils.MediaUtils
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class FlashcardEditAddViewModel(
    application: Application,
    private val flashcardRepository: FlashcardRepository,
) : AndroidViewModel(application) {

    private val _flashcardDetailData = MutableLiveData<DetailFlashcard>()
    val flashcardDetailData: LiveData<DetailFlashcard> get() = _flashcardDetailData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // Biến lắng nghe trạng thái lưu thành công hay thất bại
    private val _saveSuccess = MutableLiveData<Boolean>()
    val saveSuccess: LiveData<Boolean> get() = _saveSuccess
    fun createFlashcard(request: CreateFlashcardRequest, imageUri: Uri?) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Gọi tới Repository (Giả sử hàm createFlashcard trong repo là suspend function)
                val context = getApplication<Application>().applicationContext
                val dataPart = Gson().toJson(request).toRequestBody("application/json".toMediaTypeOrNull())
                val imagePart = imageUri?.let { MediaUtils.prepareFilePart(context, "flashcardImage", it) }

                // Gọi tới Repository (Giả sử hàm updateFlashcard trong repo là suspend function)
                flashcardRepository.createFlashcard(request.deckId, dataPart, imagePart)
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
    fun updateFlashcard(request: UpdateFlashcardRequest, imageUri: Uri?) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val context = getApplication<Application>().applicationContext
                val dataPart = Gson().toJson(request).toRequestBody("application/json".toMediaTypeOrNull())
                val imagePart = imageUri?.let { MediaUtils.prepareFilePart(context, "flashcardImage", it) }

                // Gọi tới Rep  ository (Giả sử hàm updateFlashcard trong repo là suspend function)
                flashcardRepository.updateFlashcard(request.deckId, dataPart, imagePart)
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
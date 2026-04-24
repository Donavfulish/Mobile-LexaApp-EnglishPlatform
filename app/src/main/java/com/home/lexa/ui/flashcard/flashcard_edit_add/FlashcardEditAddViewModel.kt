package com.home.lexa.ui.flashcard.flashcard_edit_add

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.gson.Gson
import com.home.lexa.domain.models.CreateFlashcardRequest
import com.home.lexa.domain.models.DetailFlashcard
import com.home.lexa.domain.models.UpdateFlashcardRequest
import com.home.lexa.domain.repository.FlashcardRepository
import com.home.lexa.ui.utils.MediaUtils
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import com.home.lexa.BuildConfig
import com.home.lexa.domain.models.WordUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FlashcardEditAddViewModel(
    application: Application,
    private val flashcardRepository: FlashcardRepository,
) : AndroidViewModel(application) {

    private val _flashcardDetailData = MutableLiveData<DetailFlashcard>()
    val flashcardDetailData: LiveData<DetailFlashcard> get() = _flashcardDetailData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _saveSuccess = MutableLiveData<Boolean>()
    val saveSuccess: LiveData<Boolean> get() = _saveSuccess

    val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = BuildConfig.GEMINI_KEY
    )


    private val _uiState = MutableLiveData<WordUiState>(WordUiState.Idle)
    val uiState: LiveData<WordUiState> get() = _uiState

    private val _phoneticState = MutableLiveData<String?>(null)
    val phoneticState: LiveData<String?> get() = _phoneticState
    fun createFlashcard(request: CreateFlashcardRequest, imageUri: Uri?) {
        viewModelScope.launch {
            _isLoading.value = true
            try {

                val context = getApplication<Application>().applicationContext
                val dataPart = Gson().toJson(request).toRequestBody("application/json".toMediaTypeOrNull())
                val imagePart = imageUri?.let { MediaUtils.prepareFilePart(context, "flashcardImage", it) }

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


    fun updateFlashcard(request: UpdateFlashcardRequest, imageUri: Uri?) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val context = getApplication<Application>().applicationContext
                val dataPart = Gson().toJson(request).toRequestBody("application/json".toMediaTypeOrNull())
                val imagePart = imageUri?.let { MediaUtils.prepareFilePart(context, "flashcardImage", it) }

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


    fun fetchAiExampleSuggestion(word: String, meaning: String, partOfSpeech: String) {
        viewModelScope.launch {
            _uiState.value = WordUiState.Loading
            try {

                val result = flashcardRepository.getExampleSuggestion(generativeModel, word, meaning, partOfSpeech)

                if (result != "INVALID_CONTEXT" ) {
                    _uiState.value = WordUiState.Success(result)
                } else {
                    _uiState.value = WordUiState.Error("Không thể tạo câu ví dụ")
                }
            } catch (e: Exception) {
                _uiState.value = WordUiState.Error("Lỗi kết nối: ${e.localizedMessage}")
            }
        }
    }
    fun fetchPhonetic(word: String, type: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = flashcardRepository.getPhoneticFromApi(word, type)
            result.fold(
                onSuccess = { phonetic ->
                    _phoneticState.postValue(phonetic)
                },
                onFailure = {
                    _phoneticState.postValue("ERROR")
                }
            )
        }
    }
}
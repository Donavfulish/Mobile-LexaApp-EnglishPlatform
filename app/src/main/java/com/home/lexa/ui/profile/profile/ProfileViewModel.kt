package com.home.lexa.ui.profile.profile

import android.app.Application
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.lexa.data.local.UserManager
import com.home.lexa.domain.models.Profile
import com.home.lexa.domain.models.UpdateProfileRequest
import com.home.lexa.domain.repository.ProfileRepository
import com.home.lexa.ui.utils.MediaUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

enum class AVATAR_ACTION(val value: String) {
    UPDATE("update"),
    DELETE("delete");
}

class ProfileViewModel(
    application: Application,
    private val repository: ProfileRepository,
    private val userManager: UserManager
) : AndroidViewModel(application) {
    private val _isLoading = MutableLiveData<Boolean>()

    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _profileData = MutableLiveData<Profile>()
    val profileData: LiveData<Profile> = _profileData

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _updateSuccess = MutableLiveData<Boolean>()
    val updateSuccess: LiveData<Boolean> = _updateSuccess

    fun fetchProfile() {
        val userId = userManager.getUserId()
        if (userId == -1) {
            _error.value = "Unable to get User ID"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getProfile()
            result.onSuccess { profile ->
                _profileData.value = profile
            }.onFailure { exception ->
                _error.value = exception.message ?: "Unidentified Error"
            }
            _isLoading.value = false
        }
    }

    fun updateProfile(data: UpdateProfileRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.updateProfile(data)
            result.onSuccess {
                _updateSuccess.value = true
                userManager.updateUserName(data.fullName ?: "");
            }.onFailure { exception ->
                _error.value = exception.message ?: "Unidentified Error"
            }
            _isLoading.value = false
        }
    }

    fun updateAvatar(avatarUri: Uri?, action: AVATAR_ACTION) {
        val context = getApplication<Application>().applicationContext

        val avatarPart = if (action == AVATAR_ACTION.DELETE) {
            // Tạo Part rỗng để tránh lỗi Retrofit Multipart
            val emptyBody = "".toRequestBody("text/plain".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("avatar", "", emptyBody)
        } else {
            // Tạo Part từ file thật
            avatarUri?.let { MediaUtils.prepareFilePart(context, "avatar", it) }
        }

        viewModelScope.launch {
            val result = repository.updateAvatar(avatarPart, action)
            if (result.isSuccess) {
                fetchProfile()
            }
        }
    }

    fun resetUpdateStatus() {
        _updateSuccess.value = false
    }
}
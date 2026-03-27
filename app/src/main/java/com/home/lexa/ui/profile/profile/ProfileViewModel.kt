package com.home.lexa.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.lexa.data.local.UserManager
import com.home.lexa.domain.models.Profile
import com.home.lexa.domain.models.UpdateProfileRequest
import com.home.lexa.domain.repository.ProfileRepository
import com.home.lexa.ui.auth.login.AuthState
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: ProfileRepository,
    private val userManager: UserManager
) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _profileData = MutableLiveData<Profile>()
    val profileData: LiveData<Profile> = _profileData

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchProfile() {
        val userId = userManager.getUserId()
        if (userId == -1) {
            _error.value = "Không tìm thấy User ID"
            return
        }

        viewModelScope.launch {
            // repository.getProfile giờ trả về Result<Profile>
            val result = repository.getProfile(userId)

            result.onSuccess { profile ->
                _profileData.value = profile
            }.onFailure { exception ->
                _error.value = exception.message ?: "Lỗi không xác định"
            }
        }
    }

    fun updateProfile(data: UpdateProfileRequest) {
        viewModelScope.launch {
             _isLoading.value = true

            val result = repository.updateProfile(data)

            result.onSuccess {
                val currentProfile = _profileData.value ?: return@onSuccess

                val updatedProfile = currentProfile.copy(
                    fullName = data.fullName,
                    DoB = data.DoB,
                    address = data.address
                )

                _profileData.value = updatedProfile
            }.onFailure { exception ->
                _error.value = exception.message ?: "Lỗi không xác định"
            }

            _isLoading.value = false
        }
    }
}
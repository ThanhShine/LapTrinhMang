package com.example.laptrinhmang.feature.account.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laptrinhmang.core.model.NetworkResult
import com.example.laptrinhmang.core.model.User
import com.example.laptrinhmang.feature.account.data.AuthRepository
import com.example.laptrinhmang.feature.account.domain.AuthValidation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val showEditProfileDialog: Boolean = false,
    val showChangePasswordDialog: Boolean = false,
    val editFullName: String = "",
    val editPhone: String = "",
    val editAvatarUrl: String = "",
    val oldPassword: String = "",
    val newPassword: String = "",
    val confirmNewPassword: String = ""
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        observeUser()
        loadProfile()
    }

    private fun observeUser() {
        viewModelScope.launch {
            authRepository.currentUserFlow.collect { user ->
                _uiState.update { it.copy(user = user) }
            }
        }
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = authRepository.getProfile()) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, user = result.data) }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.apiError.message) }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun openEditProfileDialog() {
        val user = _uiState.value.user
        _uiState.update {
            it.copy(
                showEditProfileDialog = true,
                editFullName = user?.fullName ?: "",
                editPhone = user?.phone ?: "",
                editAvatarUrl = user?.avatarUrl ?: ""
            )
        }
    }

    fun closeEditProfileDialog() = _uiState.update { it.copy(showEditProfileDialog = false) }

    fun onEditFullNameChanged(v: String) = _uiState.update { it.copy(editFullName = v) }
    fun onEditPhoneChanged(v: String) = _uiState.update { it.copy(editPhone = v) }
    fun onEditAvatarUrlChanged(v: String) = _uiState.update { it.copy(editAvatarUrl = v) }

    fun saveProfile() {
        val current = _uiState.value
        if (current.editFullName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Họ và tên không được để trống.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = authRepository.updateProfile(current.editFullName, current.editPhone, current.editAvatarUrl)) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            showEditProfileDialog = false,
                            user = result.data,
                            successMessage = "Cập nhật hồ sơ thành công!"
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.apiError.message) }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun openChangePasswordDialog() = _uiState.update {
        it.copy(
            showChangePasswordDialog = true,
            oldPassword = "",
            newPassword = "",
            confirmNewPassword = ""
        )
    }

    fun closeChangePasswordDialog() = _uiState.update { it.copy(showChangePasswordDialog = false) }

    fun onOldPasswordChanged(v: String) = _uiState.update { it.copy(oldPassword = v) }
    fun onNewPasswordChanged(v: String) = _uiState.update { it.copy(newPassword = v) }
    fun onConfirmNewPasswordChanged(v: String) = _uiState.update { it.copy(confirmNewPassword = v) }

    fun submitChangePassword() {
        val current = _uiState.value
        if (current.oldPassword.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Vui lòng nhập mật khẩu cũ.") }
            return
        }
        if (!AuthValidation.isValidPassword(current.newPassword)) {
            _uiState.update { it.copy(errorMessage = "Mật khẩu mới phải từ 8 ký tự trở lên, gồm chữ cái và số/ký tự đặc biệt.") }
            return
        }
        if (current.newPassword != current.confirmNewPassword) {
            _uiState.update { it.copy(errorMessage = "Mật khẩu xác nhận không khớp.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = authRepository.changePassword(current.oldPassword, current.newPassword)) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            showChangePasswordDialog = false,
                            successMessage = "Đổi mật khẩu thành công!"
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.apiError.message) }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onLoggedOut()
        }
    }
}

package com.example.laptrinhmang.feature.account.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laptrinhmang.core.model.NetworkResult
import com.example.laptrinhmang.core.model.User
import com.example.laptrinhmang.feature.account.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val identifier: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loggedInUser: User? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onIdentifierChanged(value: String) {
        _uiState.update { it.copy(identifier = value, errorMessage = null) }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
    }

    fun login(onSuccess: (User) -> Unit = {}) {
        val current = _uiState.value
        if (current.identifier.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Vui lòng nhập mã định danh hoặc email trường.") }
            return
        }
        if (current.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Vui lòng nhập mật khẩu.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = authRepository.login(current.identifier, current.password)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, loggedInUser = result.data) }
                    onSuccess(result.data)
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.apiError.message
                        )
                    }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    // Quick mock fill helper for 4 required main roles (Admin, Quản lý phòng, Giảng viên, Sinh viên)
    fun fillMockRoleCredentials(roleKey: String) {
        when (roleKey) {
            "ADMIN" -> {
                _uiState.update { it.copy(identifier = "admin", password = "Password123@") }
            }
            "ROOM_MANAGER" -> {
                _uiState.update { it.copy(identifier = "quanly", password = "Password123@") }
            }
            "LECTURER" -> {
                _uiState.update { it.copy(identifier = "giangvien", password = "Password123@") }
            }
            "STUDENT" -> {
                _uiState.update { it.copy(identifier = "2051220001", password = "Password123@") }
            }
        }
    }
}

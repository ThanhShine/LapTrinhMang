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
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showContactAdminDialog: Boolean = false,
    val loggedInUser: User? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChanged(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null) }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
    }

    fun openContactAdminDialog() {
        _uiState.update { it.copy(showContactAdminDialog = true) }
    }

    fun closeContactAdminDialog() {
        _uiState.update { it.copy(showContactAdminDialog = false) }
    }

    fun login(onSuccess: (User) -> Unit = {}) {
        val current = _uiState.value
        if (current.email.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Vui lòng nhập Email trường.") }
            return
        }
        if (current.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Vui lòng nhập mật khẩu.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = authRepository.login(current.email, current.password)) {
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

    // Quick mock fill helper for test accounts
    fun fillMockRoleCredentials(roleKey: String) {
        when (roleKey) {
            "ADMIN" -> {
                _uiState.update { it.copy(email = "admin@dau.edu.vn", password = "Password123@") }
            }
            "ROOM_MANAGER" -> {
                _uiState.update { it.copy(email = "quanly@dau.edu.vn", password = "Password123@") }
            }
            "LECTURER" -> {
                _uiState.update { it.copy(email = "giangvien@dau.edu.vn", password = "Password123@") }
            }
            "STUDENT" -> {
                _uiState.update { it.copy(email = "sv.2051220001@dau.edu.vn", password = "Password123@") }
            }
            "MUST_CHANGE_PASS" -> {
                _uiState.update { it.copy(email = "newuser@dau.edu.vn", password = "TempPass123@") }
            }
        }
    }
}

package com.example.laptrinhmang.feature.account.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laptrinhmang.core.model.NetworkResult
import com.example.laptrinhmang.feature.account.data.AuthRepository
import com.example.laptrinhmang.feature.account.domain.AuthValidation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val userCode: String = "",
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onUserCodeChanged(value: String) = _uiState.update { it.copy(userCode = value, errorMessage = null) }
    fun onFullNameChanged(value: String) = _uiState.update { it.copy(fullName = value, errorMessage = null) }
    fun onEmailChanged(value: String) = _uiState.update { it.copy(email = value, errorMessage = null) }
    fun onPasswordChanged(value: String) = _uiState.update { it.copy(password = value, errorMessage = null) }
    fun onConfirmPasswordChanged(value: String) = _uiState.update { it.copy(confirmPassword = value, errorMessage = null) }

    fun register(onSuccess: (email: String) -> Unit) {
        val current = _uiState.value
        if (!AuthValidation.isValidUserCode(current.userCode)) {
            _uiState.update { it.copy(errorMessage = "MSSV/MSGV tối thiểu 4 ký tự.") }
            return
        }
        if (current.fullName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Vui lòng nhập họ và tên.") }
            return
        }
        if (!AuthValidation.isValidEmail(current.email)) {
            _uiState.update { it.copy(errorMessage = "Email phải có tên miền @dau.edu.vn.") }
            return
        }
        if (!AuthValidation.isValidPassword(current.password)) {
            _uiState.update { it.copy(errorMessage = "Mật khẩu phải tối thiểu 8 ký tự, bao gồm chữ cái và số/ký tự đặc biệt.") }
            return
        }
        if (current.password != current.confirmPassword) {
            _uiState.update { it.copy(errorMessage = "Mật khẩu xác nhận không khớp.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = authRepository.register(current.userCode, current.email, current.password, current.fullName)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess(current.email)
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.apiError.message) }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }
}

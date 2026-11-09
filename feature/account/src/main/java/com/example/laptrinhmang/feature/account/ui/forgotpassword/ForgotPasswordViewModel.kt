package com.example.laptrinhmang.feature.account.ui.forgotpassword

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

data class ForgotPasswordUiState(
    val step: Int = 1, // Step 1: Request Email -> OTP, Step 2: Input OTP & New Password
    val email: String = "",
    val otpCode: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun onEmailChanged(value: String) = _uiState.update { it.copy(email = value, errorMessage = null) }
    fun onOtpCodeChanged(value: String) = _uiState.update { it.copy(otpCode = value, errorMessage = null) }
    fun onNewPasswordChanged(value: String) = _uiState.update { it.copy(newPassword = value, errorMessage = null) }
    fun onConfirmPasswordChanged(value: String) = _uiState.update { it.copy(confirmPassword = value, errorMessage = null) }

    fun requestOtp() {
        val email = _uiState.value.email
        if (!AuthValidation.isValidEmail(email)) {
            _uiState.update { it.copy(errorMessage = "Vui lòng nhập email trường hợp lệ (@dau.edu.vn).") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = authRepository.requestForgotPassword(email)) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            step = 2,
                            successMessage = "Mã OTP khôi phục đã được gửi về email của bạn."
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

    fun resetPassword(onSuccess: () -> Unit) {
        val current = _uiState.value
        if (!AuthValidation.isValidOtp(current.otpCode)) {
            _uiState.update { it.copy(errorMessage = "Mã OTP phải gồm 6 chữ số.") }
            return
        }
        if (!AuthValidation.isValidPassword(current.newPassword)) {
            _uiState.update { it.copy(errorMessage = "Mật khẩu mới phải từ 8 ký tự trở lên, gồm chữ cái và số/ký tự đặc biệt.") }
            return
        }
        if (current.newPassword != current.confirmPassword) {
            _uiState.update { it.copy(errorMessage = "Mật khẩu xác nhận không khớp.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = authRepository.resetPassword(current.email, current.otpCode, current.newPassword)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, successMessage = "Đặt lại mật khẩu thành công!") }
                    onSuccess()
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.apiError.message) }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }
}

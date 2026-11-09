package com.example.laptrinhmang.feature.account.ui.otp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laptrinhmang.core.model.NetworkResult
import com.example.laptrinhmang.feature.account.data.AuthRepository
import com.example.laptrinhmang.feature.account.domain.AuthValidation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OtpUiState(
    val email: String = "",
    val otpCode: String = "",
    val expirySeconds: Int = 300, // 5 minutes countdown
    val resendCooldownSeconds: Int = 60, // 60 seconds resend cooldown
    val failedAttempts: Int = 0,
    val maxAttempts: Int = 5,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OtpUiState())
    val uiState: StateFlow<OtpUiState> = _uiState.asStateFlow()

    private var countdownJob: Job? = null

    fun init(email: String) {
        _uiState.update { it.copy(email = email) }
        startTimers()
    }

    private fun startTimers() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (_uiState.value.expirySeconds > 0) {
                delay(1000L)
                _uiState.update { current ->
                    current.copy(
                        expirySeconds = (current.expirySeconds - 1).coerceAtLeast(0),
                        resendCooldownSeconds = (current.resendCooldownSeconds - 1).coerceAtLeast(0)
                    )
                }
            }
        }
    }

    fun onOtpCodeChanged(value: String) {
        if (value.length <= 6 && value.all { it.isDigit() }) {
            _uiState.update { it.copy(otpCode = value, errorMessage = null) }
        }
    }

    fun verifyOtp(onSuccess: () -> Unit) {
        val current = _uiState.value
        if (current.failedAttempts >= current.maxAttempts) {
            _uiState.update { it.copy(errorMessage = "Bạn đã nhập sai quá 5 lần. Vui lòng yêu cầu lại OTP.") }
            return
        }
        if (current.expirySeconds <= 0) {
            _uiState.update { it.copy(errorMessage = "Mã OTP đã hết hạn 5 phút. Vui lòng gửi lại OTP.") }
            return
        }
        if (!AuthValidation.isValidOtp(current.otpCode)) {
            _uiState.update { it.copy(errorMessage = "Mã OTP phải gồm 6 chữ số.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = authRepository.activateAccount(current.email, current.otpCode)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, successMessage = "Kích hoạt tài khoản thành công!") }
                    onSuccess()
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            failedAttempts = it.failedAttempts + 1,
                            errorMessage = result.apiError.message
                        )
                    }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun resendOtp() {
        val current = _uiState.value
        if (current.resendCooldownSeconds > 0) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = authRepository.requestForgotPassword(current.email)) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            expirySeconds = 300,
                            resendCooldownSeconds = 60,
                            failedAttempts = 0,
                            successMessage = "Mã OTP mới đã được gửi!"
                        )
                    }
                    startTimers()
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.apiError.message) }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}

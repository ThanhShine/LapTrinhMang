package com.example.laptrinhmang.feature.admin.ui.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laptrinhmang.core.model.NetworkResult
import com.example.laptrinhmang.core.model.User
import com.example.laptrinhmang.core.model.UserStatus
import com.example.laptrinhmang.feature.admin.data.AdminRepository
import com.example.laptrinhmang.feature.admin.data.CreateUserRequest
import com.example.laptrinhmang.feature.admin.data.UpdateUserRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminUsersUiState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val showAddDialog: Boolean = false,
    val editingUser: User? = null,
    val resettingPasswordUser: User? = null
)

@HiltViewModel
class AdminUsersViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUsersUiState())
    val uiState: StateFlow<AdminUsersUiState> = _uiState.asStateFlow()

    init {
        loadUsers()
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val query = _uiState.value.searchQuery.ifBlank { null }
            when (val result = adminRepository.getUsers(page = 1, search = query)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, users = result.data.items) }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.apiError.message) }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun openAddDialog() = _uiState.update { it.copy(showAddDialog = true) }
    fun closeAddDialog() = _uiState.update { it.copy(showAddDialog = false) }

    fun createUser(username: String, fullName: String, email: String, role: String, departmentId: Long, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val req = CreateUserRequest(username, fullName, email, role, departmentId, password)
            when (val result = adminRepository.createUser(req)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, showAddDialog = false, successMessage = "Thêm người dùng thành công!") }
                    loadUsers()
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.apiError.message) }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun toggleLockUser(user: User) {
        val newStatus = if (user.status == UserStatus.LOCKED) UserStatus.ACTIVE else UserStatus.LOCKED
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = adminRepository.updateUserStatus(user.id, newStatus)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, successMessage = "Đã cập nhật trạng thái tài khoản!") }
                    loadUsers()
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.apiError.message) }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun openResetPasswordDialog(user: User) = _uiState.update { it.copy(resettingPasswordUser = user) }
    fun closeResetPasswordDialog() = _uiState.update { it.copy(resettingPasswordUser = null) }

    fun resetPassword(newPassword: String) {
        val user = _uiState.value.resettingPasswordUser ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = adminRepository.resetUserPassword(user.id, newPassword)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, resettingPasswordUser = null, successMessage = "Đã đặt lại mật khẩu thành công!") }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.apiError.message) }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }
}

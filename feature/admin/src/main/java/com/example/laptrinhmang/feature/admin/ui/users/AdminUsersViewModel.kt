package com.example.laptrinhmang.feature.admin.ui.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laptrinhmang.core.model.NetworkResult
import com.example.laptrinhmang.core.model.User
import com.example.laptrinhmang.core.model.UserStatus
import com.example.laptrinhmang.feature.admin.data.AdminRepository
import com.example.laptrinhmang.feature.admin.data.CreateUserRequest
import com.example.laptrinhmang.feature.admin.data.ImportCsvResult
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
    val showImportCsvDialog: Boolean = false,
    val createdTempPassword: String? = null,
    val resetTempPassword: String? = null,
    val csvImportResult: ImportCsvResult? = null
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

    fun openImportCsvDialog() = _uiState.update { it.copy(showImportCsvDialog = true, csvImportResult = null) }
    fun closeImportCsvDialog() = _uiState.update { it.copy(showImportCsvDialog = false, csvImportResult = null) }

    fun dismissTempPasswordDialog() = _uiState.update { it.copy(createdTempPassword = null, resetTempPassword = null) }

    fun createUser(email: String, fullName: String, role: String, departmentId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val req = CreateUserRequest(email = email, fullName = fullName, role = role, departmentId = departmentId)
            when (val result = adminRepository.createUser(req)) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            showAddDialog = false,
                            createdTempPassword = result.data.temporaryPassword
                        )
                    }
                    loadUsers()
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.apiError.message) }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun importCsv(csvContent: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = adminRepository.importUsersCsv(csvContent)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, csvImportResult = result.data) }
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

    fun resetPassword(user: User) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = adminRepository.resetUserPassword(user.id)) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            resetTempPassword = result.data.temporaryPassword
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
}

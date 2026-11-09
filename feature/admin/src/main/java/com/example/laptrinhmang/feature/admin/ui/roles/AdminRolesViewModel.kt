package com.example.laptrinhmang.feature.admin.ui.roles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laptrinhmang.core.model.NetworkResult
import com.example.laptrinhmang.feature.admin.data.AdminRepository
import com.example.laptrinhmang.feature.admin.data.RolePermissionItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminRolesUiState(
    val roles: List<RolePermissionItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class AdminRolesViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminRolesUiState())
    val uiState: StateFlow<AdminRolesUiState> = _uiState.asStateFlow()

    init {
        loadRoles()
    }

    fun loadRoles() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = adminRepository.getRoles()) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, roles = result.data) }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.apiError.message) }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun updateRolePermission(roleId: String, permissionCode: String, isChecked: Boolean) {
        val currentRoles = _uiState.value.roles
        val targetRole = currentRoles.find { it.id == roleId } ?: return

        val newPermissions = if (isChecked) {
            targetRole.permissions + permissionCode
        } else {
            targetRole.permissions - permissionCode
        }

        viewModelScope.launch {
            when (val result = adminRepository.updateRolePermissions(roleId, newPermissions)) {
                is NetworkResult.Success -> {
                    _uiState.update { current ->
                        val updated = current.roles.map {
                            if (it.id == roleId) it.copy(permissions = newPermissions) else it
                        }
                        current.copy(roles = updated, successMessage = "Đã cập nhật quyền!")
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(errorMessage = result.apiError.message) }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }
}

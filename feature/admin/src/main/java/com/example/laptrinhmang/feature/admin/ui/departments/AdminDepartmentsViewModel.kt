package com.example.laptrinhmang.feature.admin.ui.departments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laptrinhmang.core.model.NetworkResult
import com.example.laptrinhmang.feature.admin.data.AdminRepository
import com.example.laptrinhmang.feature.admin.data.DepartmentItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminDepartmentsUiState(
    val departments: List<DepartmentItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val showAddDialog: Boolean = false
)

@HiltViewModel
class AdminDepartmentsViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminDepartmentsUiState())
    val uiState: StateFlow<AdminDepartmentsUiState> = _uiState.asStateFlow()

    init {
        loadDepartments()
    }

    fun loadDepartments() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = adminRepository.getDepartments()) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, departments = result.data) }
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

    fun createDepartment(name: String, parentId: Long?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = adminRepository.createDepartment(name, parentId)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, showAddDialog = false, successMessage = "Thêm đơn vị thành công!") }
                    loadDepartments()
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.apiError.message) }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }
}

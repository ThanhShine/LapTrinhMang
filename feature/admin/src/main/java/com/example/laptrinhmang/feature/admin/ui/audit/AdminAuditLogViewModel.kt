package com.example.laptrinhmang.feature.admin.ui.audit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laptrinhmang.core.model.NetworkResult
import com.example.laptrinhmang.feature.admin.data.AdminRepository
import com.example.laptrinhmang.feature.admin.data.AuditLogItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminAuditLogUiState(
    val auditLogs: List<AuditLogItem> = emptyList(),
    val actionFilter: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class AdminAuditLogViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminAuditLogUiState())
    val uiState: StateFlow<AdminAuditLogUiState> = _uiState.asStateFlow()

    init {
        loadAuditLogs()
    }

    fun onActionFilterChanged(action: String) {
        _uiState.update { it.copy(actionFilter = action) }
        loadAuditLogs()
    }

    fun loadAuditLogs() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val filter = _uiState.value.actionFilter.ifBlank { null }
            when (val result = adminRepository.getAuditLogs(page = 1, action = filter)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, auditLogs = result.data.items) }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.apiError.message) }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }
}

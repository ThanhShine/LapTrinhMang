package com.example.laptrinhmang.feature.account.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laptrinhmang.core.model.NetworkResult
import com.example.laptrinhmang.feature.account.data.NotificationItem
import com.example.laptrinhmang.feature.account.data.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class NotificationFilter { ALL, UNREAD, READ }

data class NotificationUiState(
    val items: List<NotificationItem> = emptyList(),
    val filter: NotificationFilter = NotificationFilter.ALL,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    init {
        loadNotifications()
    }

    fun setFilter(filter: NotificationFilter) {
        _uiState.update { it.copy(filter = filter) }
        loadNotifications()
    }

    fun loadNotifications() {
        val isReadParam = when (_uiState.value.filter) {
            NotificationFilter.ALL -> null
            NotificationFilter.UNREAD -> false
            NotificationFilter.READ -> true
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = notificationRepository.getNotifications(page = 1, isReadFilter = isReadParam)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, items = result.data.items) }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.apiError.message) }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun markAsRead(item: NotificationItem) {
        viewModelScope.launch {
            notificationRepository.markAsRead(item.id)
            _uiState.update { current ->
                val updated = current.items.map {
                    if (it.id == item.id) it.copy(isRead = true) else it
                }
                current.copy(items = updated)
            }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            notificationRepository.markAllAsRead()
            _uiState.update { current ->
                val updated = current.items.map { it.copy(isRead = true) }
                current.copy(items = updated)
            }
        }
    }
}

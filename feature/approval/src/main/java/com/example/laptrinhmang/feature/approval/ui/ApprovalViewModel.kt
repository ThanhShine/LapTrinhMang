package com.example.laptrinhmang.feature.approval.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laptrinhmang.core.model.NetworkResult
import com.example.laptrinhmang.feature.approval.domain.model.ApprovalItem
import com.example.laptrinhmang.feature.approval.domain.model.ApprovalRule
import com.example.laptrinhmang.feature.approval.domain.model.RoomCategory
import com.example.laptrinhmang.feature.approval.domain.usecase.AdjustScheduleUseCase
import com.example.laptrinhmang.feature.approval.domain.usecase.ApprovalRulesUseCases
import com.example.laptrinhmang.feature.approval.domain.usecase.BatchDecideApprovalsUseCase
import com.example.laptrinhmang.feature.approval.domain.usecase.DecideApprovalUseCase
import com.example.laptrinhmang.feature.approval.domain.usecase.GetCampusScheduleUseCase
import com.example.laptrinhmang.feature.approval.domain.usecase.GetPendingApprovalsUseCase
import com.example.laptrinhmang.feature.approval.domain.usecase.LockRoomSlotUseCase
import com.example.laptrinhmang.feature.approval.domain.usecase.RoomCategoryUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApprovalViewModel @Inject constructor(
    private val getPendingApprovalsUseCase: GetPendingApprovalsUseCase,
    private val decideApprovalUseCase: DecideApprovalUseCase,
    private val batchDecideApprovalsUseCase: BatchDecideApprovalsUseCase,
    private val getCampusScheduleUseCase: GetCampusScheduleUseCase,
    private val adjustScheduleUseCase: AdjustScheduleUseCase,
    private val lockRoomSlotUseCase: LockRoomSlotUseCase,
    private val approvalRulesUseCases: ApprovalRulesUseCases,
    private val roomCategoryUseCases: RoomCategoryUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(ApprovalUiState())
    val uiState: StateFlow<ApprovalUiState> = _uiState.asStateFlow()

    init {
        loadPendingApprovals()
        loadCampusSchedule()
        loadApprovalRules()
        loadRoomCategories()
    }

    fun selectTab(tab: ApprovalTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    // --- TAB 1: PENDING APPROVALS ---

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        loadPendingApprovals(query)
    }

    fun loadPendingApprovals(search: String? = _uiState.value.searchQuery) {
        viewModelScope.launch {
            getPendingApprovalsUseCase(search).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is NetworkResult.Success -> _uiState.update {
                        it.copy(
                            isLoading = false,
                            pendingList = result.data,
                            errorMessage = null
                        )
                    }
                    is NetworkResult.Error -> _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.apiError.message
                        )
                    }
                }
            }
        }
    }

    fun approveSingle(item: ApprovalItem) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = decideApprovalUseCase(
                id = item.id,
                isApproved = true
            )
            when (result) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            userMessage = "Đã phê duyệt đơn ${item.id} thành công!"
                        )
                    }
                    loadPendingApprovals()
                    loadCampusSchedule()
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = result.apiError.message)
                    }
                }
                is NetworkResult.Loading -> Unit
            }
        }
    }

    fun openRejectDialog(item: ApprovalItem) {
        _uiState.update { it.copy(rejectingItem = item) }
    }

    fun closeRejectDialog() {
        _uiState.update { it.copy(rejectingItem = null) }
    }

    fun rejectSingle(reason: String) {
        val item = _uiState.value.rejectingItem ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = decideApprovalUseCase(
                id = item.id,
                isApproved = false,
                reason = reason
            )
            when (result) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            rejectingItem = null,
                            userMessage = "Đã từ chối đơn ${item.id} với lý do: $reason"
                        )
                    }
                    loadPendingApprovals()
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = result.apiError.message)
                    }
                }
                is NetworkResult.Loading -> Unit
            }
        }
    }

    fun toggleBatchSelectionMode() {
        _uiState.update {
            it.copy(
                isBatchSelectionMode = !it.isBatchSelectionMode,
                selectedBookingIds = emptySet()
            )
        }
    }

    fun toggleSelectBooking(id: String) {
        _uiState.update { state ->
            val set = state.selectedBookingIds.toMutableSet()
            if (set.contains(id)) set.remove(id) else set.add(id)
            state.copy(selectedBookingIds = set)
        }
    }

    fun selectAllBookings() {
        _uiState.update { state ->
            val allIds = state.pendingList.map { it.id }.toSet()
            state.copy(selectedBookingIds = allIds)
        }
    }

    fun batchApproveSelected() {
        val ids = _uiState.value.selectedBookingIds.toList()
        if (ids.isEmpty()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = batchDecideApprovalsUseCase(
                bookingIds = ids,
                isApproved = true
            )
            when (result) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            batchResult = result.data,
                            selectedBookingIds = emptySet(),
                            isBatchSelectionMode = false,
                            userMessage = "Đã xử lý duyệt hàng loạt ${result.data.successCount}/${result.data.total} đơn thành công"
                        )
                    }
                    loadPendingApprovals()
                    loadCampusSchedule()
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = result.apiError.message)
                    }
                }
                is NetworkResult.Loading -> Unit
            }
        }
    }

    fun dismissBatchResultDialog() {
        _uiState.update { it.copy(batchResult = null) }
    }

    fun openDetailDialog(item: ApprovalItem) {
        _uiState.update { it.copy(viewingDetailItem = item) }
    }

    fun closeDetailDialog() {
        _uiState.update { it.copy(viewingDetailItem = null) }
    }

    fun sendReminder(item: ApprovalItem) {
        viewModelScope.launch {
            approvalRulesUseCases.sendReminder(item.id)
            _uiState.update {
                it.copy(userMessage = "Đã gửi thông báo nhắc duyệt cho đơn ${item.id}")
            }
        }
    }

    // --- TAB 2: CAMPUS SCHEDULE ---

    fun onDateSelected(date: String) {
        _uiState.update { it.copy(selectedDate = date) }
        loadCampusSchedule(date, _uiState.value.selectedBuilding)
    }

    fun onBuildingSelected(building: String) {
        _uiState.update { it.copy(selectedBuilding = building) }
        val bldgFilter = if (building == "Tất cả") null else building
        loadCampusSchedule(_uiState.value.selectedDate, bldgFilter)
    }

    fun loadCampusSchedule(
        date: String = _uiState.value.selectedDate,
        building: String? = if (_uiState.value.selectedBuilding == "Tất cả") null else _uiState.value.selectedBuilding
    ) {
        viewModelScope.launch {
            getCampusScheduleUseCase(date, building).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is NetworkResult.Success -> _uiState.update {
                        it.copy(isLoading = false, campusSchedule = result.data)
                    }
                    is NetworkResult.Error -> _uiState.update {
                        it.copy(isLoading = false, errorMessage = result.apiError.message)
                    }
                }
            }
        }
    }

    fun openAdjustDialog(item: ApprovalItem) {
        _uiState.update { it.copy(adjustingBooking = item) }
    }

    fun closeAdjustDialog() {
        _uiState.update { it.copy(adjustingBooking = null) }
    }

    fun adjustSchedule(newRoomId: String, newStartTime: String, newEndTime: String, reason: String) {
        val item = _uiState.value.adjustingBooking ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = adjustScheduleUseCase(
                bookingId = item.id,
                newRoomId = newRoomId,
                newStartTime = newStartTime,
                newEndTime = newEndTime,
                adjustmentReason = reason
            )
            when (result) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            adjustingBooking = null,
                            userMessage = "Đã điều chuyển lịch đơn sang phòng $newRoomId thành công!"
                        )
                    }
                    loadCampusSchedule()
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.apiError.message) }
                }
                is NetworkResult.Loading -> Unit
            }
        }
    }

    fun openLockRoomDialog() {
        _uiState.update { it.copy(isLockRoomDialogOpen = true) }
    }

    fun closeLockRoomDialog() {
        _uiState.update { it.copy(isLockRoomDialogOpen = false) }
    }

    fun lockRoomSlot(roomId: String, startTime: String, endTime: String, reason: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = lockRoomSlotUseCase(
                roomId = roomId,
                startTime = startTime,
                endTime = endTime,
                reason = reason,
                notifyAffected = true
            )
            when (result) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLockRoomDialogOpen = false,
                            lockedSlotResult = result.data,
                            userMessage = "Đã tạm khóa phòng $roomId! Phát hiện ${result.data.affectedBookings.size} đơn bị ảnh hưởng."
                        )
                    }
                    loadCampusSchedule()
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.apiError.message) }
                }
                is NetworkResult.Loading -> Unit
            }
        }
    }

    fun dismissLockResultDialog() {
        _uiState.update { it.copy(lockedSlotResult = null) }
    }

    // --- TAB 3: RULES & WORKFLOW ---

    fun loadApprovalRules() {
        viewModelScope.launch {
            approvalRulesUseCases.getRules().collect { result ->
                when (result) {
                    is NetworkResult.Success -> _uiState.update { it.copy(rules = result.data) }
                    else -> Unit
                }
            }
        }
    }

    fun toggleRule(rule: ApprovalRule) {
        viewModelScope.launch {
            val updated = rule.copy(isEnabled = !rule.isEnabled)
            approvalRulesUseCases.updateRule(updated)
            loadApprovalRules()
            _uiState.update {
                it.copy(userMessage = "Đã ${if (updated.isEnabled) "bật" else "tắt"} luật: ${rule.name}")
            }
        }
    }

    // --- TAB 4: ROOM CATEGORIES (PH5) ---

    fun loadRoomCategories() {
        viewModelScope.launch {
            roomCategoryUseCases.getCategories().collect { result ->
                when (result) {
                    is NetworkResult.Success -> _uiState.update { it.copy(categories = result.data) }
                    else -> Unit
                }
            }
        }
    }

    fun openAddCategoryDialog() {
        _uiState.update { it.copy(isCategoryDialogOpen = true, editingCategory = null) }
    }

    fun openEditCategoryDialog(category: RoomCategory) {
        _uiState.update { it.copy(isCategoryDialogOpen = true, editingCategory = category) }
    }

    fun closeCategoryDialog() {
        _uiState.update { it.copy(isCategoryDialogOpen = false, editingCategory = null) }
    }

    fun saveCategory(name: String, description: String) {
        viewModelScope.launch {
            val editing = _uiState.value.editingCategory
            val result = if (editing != null) {
                roomCategoryUseCases.updateCategory(editing.id, name, description)
            } else {
                roomCategoryUseCases.createCategory(name, description)
            }

            when (result) {
                is NetworkResult.Success -> {
                    closeCategoryDialog()
                    loadRoomCategories()
                    _uiState.update {
                        it.copy(userMessage = "Lưu danh mục phòng '$name' thành công!")
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(errorMessage = result.apiError.message) }
                }
                is NetworkResult.Loading -> Unit
            }
        }
    }

    fun deleteCategory(id: String) {
        viewModelScope.launch {
            roomCategoryUseCases.deleteCategory(id)
            loadRoomCategories()
            _uiState.update { it.copy(userMessage = "Đã xóa danh mục phòng!") }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, userMessage = null) }
    }
}

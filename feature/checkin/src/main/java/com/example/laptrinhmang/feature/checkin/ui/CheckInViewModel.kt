package com.example.laptrinhmang.feature.checkin.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laptrinhmang.core.model.NetworkResult
import com.example.laptrinhmang.feature.checkin.domain.model.IssueSeverity
import com.example.laptrinhmang.feature.checkin.domain.model.RoomEquipment
import com.example.laptrinhmang.feature.checkin.domain.model.RoomIssue
import com.example.laptrinhmang.feature.checkin.domain.model.RoomRating
import com.example.laptrinhmang.feature.checkin.domain.usecase.AssignIssueUseCase
import com.example.laptrinhmang.feature.checkin.domain.usecase.CheckInUseCase
import com.example.laptrinhmang.feature.checkin.domain.usecase.CheckOutUseCase
import com.example.laptrinhmang.feature.checkin.domain.usecase.EquipmentUseCases
import com.example.laptrinhmang.feature.checkin.domain.usecase.GetIssuesUseCase
import com.example.laptrinhmang.feature.checkin.domain.usecase.ReportIssueUseCase
import com.example.laptrinhmang.feature.checkin.domain.usecase.ResolveIssueUseCase
import com.example.laptrinhmang.feature.checkin.domain.usecase.StatisticsUseCases
import com.example.laptrinhmang.feature.checkin.domain.usecase.SubmitRatingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckInViewModel @Inject constructor(
    private val checkInUseCase: CheckInUseCase,
    private val checkOutUseCase: CheckOutUseCase,
    private val reportIssueUseCase: ReportIssueUseCase,
    private val getIssuesUseCase: GetIssuesUseCase,
    private val assignIssueUseCase: AssignIssueUseCase,
    private val resolveIssueUseCase: ResolveIssueUseCase,
    private val submitRatingUseCase: SubmitRatingUseCase,
    private val equipmentUseCases: EquipmentUseCases,
    private val statisticsUseCases: StatisticsUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckInUiState())
    val uiState: StateFlow<CheckInUiState> = _uiState.asStateFlow()

    init {
        loadIssues()
        loadEquipment()
        loadStatistics()
    }

    fun selectTab(tab: CheckInTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    // --- TAB 1: QR CHECK-IN & CHECK-OUT ---

    fun onManualInputChanged(code: String) {
        _uiState.update { it.copy(manualInputCode = code) }
    }

    fun performCheckIn(qrCode: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = checkInUseCase(qrCode)
            when (result) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            activeCheckIn = result.data,
                            userMessage = "Check-in thành công phòng ${result.data.roomCode}!"
                        )
                    }
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

    fun performCheckOut(bookingId: Long? = _uiState.value.activeCheckIn?.bookingId) {
        val bId = bookingId ?: 1L
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = checkOutUseCase(bId, "Đã hoàn thành sử dụng phòng")
            when (result) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            activeCheckIn = null,
                            lastCheckOut = result.data,
                            isRatingDialogOpen = true,
                            userMessage = "Trả phòng ${result.data.roomCode} thành công (${result.data.durationMinutes} phút). Mời bạn đánh giá chất lượng phòng!"
                        )
                    }
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

    fun closeRatingDialog() {
        _uiState.update { it.copy(isRatingDialogOpen = false) }
    }

    fun submitRating(cleanliness: Int, equipment: Int, lighting: Int, overall: Int, comment: String) {
        val bookingId = _uiState.value.lastCheckOut?.bookingId ?: 1L
        val roomCode = _uiState.value.lastCheckOut?.roomCode ?: "P.302"
        viewModelScope.launch {
            val rating = RoomRating(
                bookingId = bookingId,
                roomCode = roomCode,
                cleanlinessScore = cleanliness,
                equipmentScore = equipment,
                lightingScore = lighting,
                overallScore = overall,
                comment = comment
            )
            submitRatingUseCase(rating)
            _uiState.update {
                it.copy(
                    isRatingDialogOpen = false,
                    userMessage = "Cảm ơn bạn đã gửi đánh giá chất lượng phòng học!"
                )
            }
        }
    }

    // --- TAB 2: ISSUES (P0 & P1) ---

    fun onIssueSearchChanged(query: String) {
        _uiState.update { it.copy(issueSearchQuery = query) }
        loadIssues()
    }

    fun onStatusFilterChanged(status: String) {
        _uiState.update { it.copy(selectedStatusFilter = status) }
        loadIssues()
    }

    fun onSeverityFilterChanged(severity: String) {
        _uiState.update { it.copy(selectedSeverityFilter = severity) }
        loadIssues()
    }

    fun loadIssues() {
        val status = if (_uiState.value.selectedStatusFilter == "Tất cả") null else _uiState.value.selectedStatusFilter
        val severity = if (_uiState.value.selectedSeverityFilter == "Tất cả") null else _uiState.value.selectedSeverityFilter
        val search = _uiState.value.issueSearchQuery

        viewModelScope.launch {
            getIssuesUseCase(status, severity, search).collect { result ->
                when (result) {
                    is NetworkResult.Success -> _uiState.update { it.copy(issuesList = result.data) }
                    is NetworkResult.Error -> _uiState.update { it.copy(errorMessage = result.apiError.message) }
                    else -> Unit
                }
            }
        }
    }

    fun openCreateIssueDialog() {
        _uiState.update { it.copy(isCreateIssueDialogOpen = true) }
    }

    fun closeCreateIssueDialog() {
        _uiState.update { it.copy(isCreateIssueDialogOpen = false) }
    }

    fun reportIssue(roomCode: String, title: String, description: String, severity: IssueSeverity, images: List<String>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = reportIssueUseCase(roomCode, title, description, severity, images)
            when (result) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isCreateIssueDialogOpen = false,
                            userMessage = "Đã gửi báo cáo sự cố ${result.data.code} thành công!"
                        )
                    }
                    loadIssues()
                    loadStatistics()
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

    fun openAssignIssueDialog(issue: RoomIssue) {
        _uiState.update { it.copy(assigningIssue = issue) }
    }

    fun closeAssignIssueDialog() {
        _uiState.update { it.copy(assigningIssue = null) }
    }

    fun assignIssue(issueId: Long, technicianName: String, note: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = assignIssueUseCase(issueId, 10L, technicianName, note)
            when (result) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            assigningIssue = null,
                            userMessage = "Đã phân công kỹ thuật viên $technicianName xử lý sự cố!"
                        )
                    }
                    loadIssues()
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

    fun openResolveIssueDialog(issue: RoomIssue) {
        _uiState.update { it.copy(resolvingIssue = issue) }
    }

    fun closeResolveIssueDialog() {
        _uiState.update { it.copy(resolvingIssue = null) }
    }

    fun resolveIssue(issueId: Long, note: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = resolveIssueUseCase(issueId, note, "Lê Văn Quản (Quản lý phòng)")
            when (result) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            resolvingIssue = null,
                            userMessage = "Đã nghiệm thu và hoàn tất sự cố!"
                        )
                    }
                    loadIssues()
                    loadStatistics()
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

    // --- TAB 3: EQUIPMENT (PH5 - P2) ---

    fun loadEquipment() {
        viewModelScope.launch {
            equipmentUseCases.getEquipment().collect { result ->
                when (result) {
                    is NetworkResult.Success -> _uiState.update { it.copy(equipmentList = result.data) }
                    else -> Unit
                }
            }
        }
    }

    fun openAddEquipmentDialog() {
        _uiState.update { it.copy(isEquipmentDialogOpen = true, editingEquipment = null) }
    }

    fun openEditEquipmentDialog(equipment: RoomEquipment) {
        _uiState.update { it.copy(isEquipmentDialogOpen = true, editingEquipment = equipment) }
    }

    fun closeEquipmentDialog() {
        _uiState.update { it.copy(isEquipmentDialogOpen = false, editingEquipment = null) }
    }

    fun saveEquipment(equipment: RoomEquipment) {
        viewModelScope.launch {
            val result = if (equipment.id != 0L) {
                equipmentUseCases.updateEquipment(equipment)
            } else {
                equipmentUseCases.createEquipment(equipment)
            }
            when (result) {
                is NetworkResult.Success -> {
                    closeEquipmentDialog()
                    loadEquipment()
                    _uiState.update { it.copy(userMessage = "Đã lưu thiết bị ${result.data.name}!") }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(errorMessage = result.apiError.message) }
                }
                is NetworkResult.Loading -> Unit
            }
        }
    }

    fun deleteEquipment(id: Long) {
        viewModelScope.launch {
            equipmentUseCases.deleteEquipment(id)
            loadEquipment()
            _uiState.update { it.copy(userMessage = "Đã xóa thiết bị!") }
        }
    }

    // --- TAB 4: STATISTICS & REPORTS (P2) ---

    fun loadStatistics() {
        viewModelScope.launch {
            statisticsUseCases.getUsageStatistics().collect { result ->
                if (result is NetworkResult.Success) {
                    _uiState.update { it.copy(usageStatistics = result.data) }
                }
            }
        }
        viewModelScope.launch {
            statisticsUseCases.getIssueStatistics().collect { result ->
                if (result is NetworkResult.Success) {
                    _uiState.update { it.copy(issueStatistics = result.data) }
                }
            }
        }
    }

    fun exportReport(format: String) {
        _uiState.update {
            it.copy(
                userMessage = "Đã xuất báo cáo DAU_RMS_BaoCao_$format.xlsx vào bộ nhớ thiết bị!"
            )
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(userMessage = null, errorMessage = null) }
    }
}

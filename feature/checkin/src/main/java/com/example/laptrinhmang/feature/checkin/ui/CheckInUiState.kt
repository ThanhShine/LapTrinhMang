package com.example.laptrinhmang.feature.checkin.ui

import com.example.laptrinhmang.feature.checkin.domain.model.CheckInResult
import com.example.laptrinhmang.feature.checkin.domain.model.CheckOutResult
import com.example.laptrinhmang.feature.checkin.domain.model.IssueSeverity
import com.example.laptrinhmang.feature.checkin.domain.model.IssueStatistics
import com.example.laptrinhmang.feature.checkin.domain.model.RoomEquipment
import com.example.laptrinhmang.feature.checkin.domain.model.RoomIssue
import com.example.laptrinhmang.feature.checkin.domain.model.UsageStatistics

enum class CheckInTab(val title: String) {
    QR_CHECKIN("Quét QR Check-in"),
    ISSUES("Báo cáo Sự cố"),
    EQUIPMENT("Thiết bị phòng"),
    STATISTICS("Thống kê & Báo cáo")
}

data class CheckInUiState(
    val selectedTab: CheckInTab = CheckInTab.QR_CHECKIN,
    val isLoading: Boolean = false,
    val userMessage: String? = null,
    val errorMessage: String? = null,

    // Tab 1: QR Check-in & Check-out
    val manualInputCode: String = "",
    val activeCheckIn: CheckInResult? = null,
    val lastCheckOut: CheckOutResult? = null,
    val isRatingDialogOpen: Boolean = false,

    // Tab 2: Issues
    val issuesList: List<RoomIssue> = emptyList(),
    val issueSearchQuery: String = "",
    val selectedStatusFilter: String = "Tất cả",
    val selectedSeverityFilter: String = "Tất cả",
    val isCreateIssueDialogOpen: Boolean = false,
    val assigningIssue: RoomIssue? = null,
    val resolvingIssue: RoomIssue? = null,

    // Tab 3: Equipment (PH5)
    val equipmentList: List<RoomEquipment> = emptyList(),
    val equipmentSearchQuery: String = "",
    val isEquipmentDialogOpen: Boolean = false,
    val editingEquipment: RoomEquipment? = null,

    // Tab 4: Statistics & Reports (P2)
    val usageStatistics: UsageStatistics? = null,
    val issueStatistics: IssueStatistics? = null,
    val exportedReportMessage: String? = null
)

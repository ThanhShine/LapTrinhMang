package com.example.laptrinhmang.feature.approval.ui

import com.example.laptrinhmang.feature.approval.domain.model.ApprovalItem
import com.example.laptrinhmang.feature.approval.domain.model.ApprovalRule
import com.example.laptrinhmang.feature.approval.domain.model.BatchApprovalResult
import com.example.laptrinhmang.feature.approval.domain.model.CampusSchedule
import com.example.laptrinhmang.feature.approval.domain.model.LockedSlotResult
import com.example.laptrinhmang.feature.approval.domain.model.RoomCategory

enum class ApprovalTab(val title: String) {
    PENDING("Chờ duyệt"),
    SCHEDULE("Lịch toàn trường"),
    RULES("Quy trình & Tự động"),
    CATEGORIES("Danh mục phòng")
}

data class ApprovalUiState(
    val selectedTab: ApprovalTab = ApprovalTab.PENDING,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userMessage: String? = null,

    // Tab 1: Pending Approvals
    val pendingList: List<ApprovalItem> = emptyList(),
    val searchQuery: String = "",
    val selectedBookingIds: Set<String> = emptySet(),
    val isBatchSelectionMode: Boolean = false,
    val viewingDetailItem: ApprovalItem? = null,
    val rejectingItem: ApprovalItem? = null,
    val batchResult: BatchApprovalResult? = null,

    // Tab 2: Campus Schedule
    val selectedDate: String = "2026-11-20",
    val selectedBuilding: String = "Tất cả",
    val campusSchedule: CampusSchedule? = null,
    val adjustingBooking: ApprovalItem? = null,
    val isLockRoomDialogOpen: Boolean = false,
    val lockedSlotResult: LockedSlotResult? = null,

    // Tab 3: Rules
    val rules: List<ApprovalRule> = emptyList(),

    // Tab 4: Room Categories (PH5)
    val categories: List<RoomCategory> = emptyList(),
    val isCategoryDialogOpen: Boolean = false,
    val editingCategory: RoomCategory? = null
)

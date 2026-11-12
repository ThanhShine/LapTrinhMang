package com.example.laptrinhmang.feature.approval

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.laptrinhmang.core.ui.components.DauLoadingState
import com.example.laptrinhmang.feature.approval.ui.ApprovalTab
import com.example.laptrinhmang.feature.approval.ui.ApprovalViewModel
import com.example.laptrinhmang.feature.approval.ui.components.AdjustScheduleDialog
import com.example.laptrinhmang.feature.approval.ui.components.AffectedBookingsWarningDialog
import com.example.laptrinhmang.feature.approval.ui.components.ApprovalDetailDialog
import com.example.laptrinhmang.feature.approval.ui.components.BatchApprovalResultDialog
import com.example.laptrinhmang.feature.approval.ui.components.LockRoomSlotDialog
import com.example.laptrinhmang.feature.approval.ui.components.RejectReasonDialog
import com.example.laptrinhmang.feature.approval.ui.tabs.ApprovalRulesTab
import com.example.laptrinhmang.feature.approval.ui.tabs.CampusScheduleTab
import com.example.laptrinhmang.feature.approval.ui.tabs.PendingApprovalsTab
import com.example.laptrinhmang.feature.approval.ui.tabs.RoomCategoryCrudTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApprovalScreen(
    modifier: Modifier = Modifier,
    viewModel: ApprovalViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { err ->
            snackbarHostState.showSnackbar(err)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Phê duyệt & Quản lý lịch (PH3, PH5)",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tab Header Row
            PrimaryTabRow(
                selectedTabIndex = uiState.selectedTab.ordinal,
                modifier = Modifier.fillMaxWidth()
            ) {
                ApprovalTab.values().forEach { tab ->
                    Tab(
                        selected = uiState.selectedTab == tab,
                        onClick = { viewModel.selectTab(tab) },
                        text = { Text(tab.title, fontWeight = FontWeight.SemiBold) },
                        icon = {
                            when (tab) {
                                ApprovalTab.PENDING -> Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null)
                                ApprovalTab.SCHEDULE -> Icon(Icons.Default.DateRange, contentDescription = null)
                                ApprovalTab.RULES -> Icon(Icons.Default.Settings, contentDescription = null)
                                ApprovalTab.CATEGORIES -> Icon(Icons.Default.Category, contentDescription = null)
                            }
                        }
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when (uiState.selectedTab) {
                    ApprovalTab.PENDING -> PendingApprovalsTab(
                        uiState = uiState,
                        onSearchChanged = viewModel::onSearchQueryChanged,
                        onApproveSingle = viewModel::approveSingle,
                        onRejectSingle = viewModel::openRejectDialog,
                        onDetailClick = viewModel::openDetailDialog,
                        onRemind = viewModel::sendReminder,
                        onToggleBatchMode = viewModel::toggleBatchSelectionMode,
                        onToggleSelectBooking = viewModel::toggleSelectBooking,
                        onSelectAll = viewModel::selectAllBookings,
                        onBatchApprove = viewModel::batchApproveSelected
                    )

                    ApprovalTab.SCHEDULE -> CampusScheduleTab(
                        uiState = uiState,
                        onDateChanged = viewModel::onDateSelected,
                        onBuildingChanged = viewModel::onBuildingSelected,
                        onOpenLockRoomDialog = viewModel::openLockRoomDialog,
                        onOpenAdjustDialog = viewModel::openAdjustDialog
                    )

                    ApprovalTab.RULES -> ApprovalRulesTab(
                        uiState = uiState,
                        onToggleRule = viewModel::toggleRule
                    )

                    ApprovalTab.CATEGORIES -> RoomCategoryCrudTab(
                        uiState = uiState,
                        onOpenAddDialog = viewModel::openAddCategoryDialog,
                        onOpenEditDialog = viewModel::openEditCategoryDialog,
                        onCloseDialog = viewModel::closeCategoryDialog,
                        onSaveCategory = viewModel::saveCategory,
                        onDeleteCategory = viewModel::deleteCategory
                    )
                }

                if (uiState.isLoading) {
                    DauLoadingState(message = "Đang xử lý yêu cầu...")
                }
            }
        }
    }

    // Dialogs
    uiState.rejectingItem?.let { item ->
        RejectReasonDialog(
            item = item,
            onDismiss = viewModel::closeRejectDialog,
            onConfirmReject = viewModel::rejectSingle
        )
    }

    uiState.batchResult?.let { result ->
        BatchApprovalResultDialog(
            result = result,
            onDismiss = viewModel::dismissBatchResultDialog
        )
    }

    uiState.viewingDetailItem?.let { item ->
        ApprovalDetailDialog(
            item = item,
            onDismiss = viewModel::closeDetailDialog
        )
    }

    uiState.adjustingBooking?.let { item ->
        AdjustScheduleDialog(
            item = item,
            onDismiss = viewModel::closeAdjustDialog,
            onConfirm = viewModel::adjustSchedule
        )
    }

    if (uiState.isLockRoomDialogOpen) {
        LockRoomSlotDialog(
            onDismiss = viewModel::closeLockRoomDialog,
            onConfirm = viewModel::lockRoomSlot
        )
    }

    uiState.lockedSlotResult?.let { lockResult ->
        AffectedBookingsWarningDialog(
            result = lockResult,
            onDismiss = viewModel::dismissLockResultDialog
        )
    }
}

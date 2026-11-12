package com.example.laptrinhmang.feature.checkin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ReportProblem
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.laptrinhmang.core.ui.components.DauLoadingState
import com.example.laptrinhmang.feature.checkin.ui.CheckInTab
import com.example.laptrinhmang.feature.checkin.ui.CheckInViewModel
import com.example.laptrinhmang.feature.checkin.ui.components.AssignIssueDialog
import com.example.laptrinhmang.feature.checkin.ui.components.CreateIssueDialog
import com.example.laptrinhmang.feature.checkin.ui.components.RatingDialog
import com.example.laptrinhmang.feature.checkin.ui.components.ResolveIssueDialog
import com.example.laptrinhmang.feature.checkin.ui.tabs.EquipmentTab
import com.example.laptrinhmang.feature.checkin.ui.tabs.IssuesTab
import com.example.laptrinhmang.feature.checkin.ui.tabs.QrCheckInTab
import com.example.laptrinhmang.feature.checkin.ui.tabs.StatisticsTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckInScreen(
    modifier: Modifier = Modifier,
    viewModel: CheckInViewModel = hiltViewModel()
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
                        text = "Check-in, Sự cố & Thống kê (PH4, PH5)",
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
            // Tab Row
            PrimaryTabRow(
                selectedTabIndex = uiState.selectedTab.ordinal,
                modifier = Modifier.fillMaxWidth()
            ) {
                CheckInTab.values().forEach { tab ->
                    Tab(
                        selected = uiState.selectedTab == tab,
                        onClick = { viewModel.selectTab(tab) },
                        text = { Text(tab.title, fontWeight = FontWeight.SemiBold) },
                        icon = {
                            when (tab) {
                                CheckInTab.QR_CHECKIN -> Icon(Icons.Default.QrCodeScanner, contentDescription = null)
                                CheckInTab.ISSUES -> Icon(Icons.Default.ReportProblem, contentDescription = null)
                                CheckInTab.EQUIPMENT -> Icon(Icons.Default.Build, contentDescription = null)
                                CheckInTab.STATISTICS -> Icon(Icons.Default.Assessment, contentDescription = null)
                            }
                        }
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when (uiState.selectedTab) {
                    CheckInTab.QR_CHECKIN -> QrCheckInTab(
                        uiState = uiState,
                        onManualInputChanged = viewModel::onManualInputChanged,
                        onPerformCheckIn = viewModel::performCheckIn,
                        onPerformCheckOut = viewModel::performCheckOut,
                        onOpenRatingDialog = { /* handled when checkout */ }
                    )

                    CheckInTab.ISSUES -> IssuesTab(
                        uiState = uiState,
                        onSearchChanged = viewModel::onIssueSearchChanged,
                        onStatusFilterChanged = viewModel::onStatusFilterChanged,
                        onOpenCreateIssueDialog = viewModel::openCreateIssueDialog,
                        onOpenAssignDialog = viewModel::openAssignIssueDialog,
                        onOpenResolveDialog = viewModel::openResolveIssueDialog
                    )

                    CheckInTab.EQUIPMENT -> EquipmentTab(
                        uiState = uiState,
                        onOpenAddDialog = viewModel::openAddEquipmentDialog,
                        onOpenEditDialog = viewModel::openEditEquipmentDialog,
                        onCloseDialog = viewModel::closeEquipmentDialog,
                        onSaveEquipment = viewModel::saveEquipment,
                        onDeleteEquipment = viewModel::deleteEquipment
                    )

                    CheckInTab.STATISTICS -> StatisticsTab(
                        uiState = uiState,
                        onExportReport = viewModel::exportReport
                    )
                }

                if (uiState.isLoading) {
                    DauLoadingState(message = "Đang xử lý...")
                }
            }
        }
    }

    // Dialogs
    if (uiState.isCreateIssueDialogOpen) {
        CreateIssueDialog(
            onDismiss = viewModel::closeCreateIssueDialog,
            onSubmit = viewModel::reportIssue
        )
    }

    uiState.assigningIssue?.let { issue ->
        AssignIssueDialog(
            issue = issue,
            onDismiss = viewModel::closeAssignIssueDialog,
            onConfirm = { techName, note ->
                viewModel.assignIssue(issue.id, techName, note)
            }
        )
    }

    uiState.resolvingIssue?.let { issue ->
        ResolveIssueDialog(
            issue = issue,
            onDismiss = viewModel::closeResolveIssueDialog,
            onConfirm = { resolutionNote ->
                viewModel.resolveIssue(issue.id, resolutionNote)
            }
        )
    }

    if (uiState.isRatingDialogOpen) {
        RatingDialog(
            onDismiss = viewModel::closeRatingDialog,
            onSubmit = viewModel::submitRating
        )
    }
}

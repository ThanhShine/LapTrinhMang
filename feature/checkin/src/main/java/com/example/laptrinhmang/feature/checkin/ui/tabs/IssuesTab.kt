package com.example.laptrinhmang.feature.checkin.ui.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.laptrinhmang.core.ui.components.DauEmptyState
import com.example.laptrinhmang.feature.checkin.domain.model.IssueSeverity
import com.example.laptrinhmang.feature.checkin.domain.model.IssueStatus
import com.example.laptrinhmang.feature.checkin.domain.model.RoomIssue
import com.example.laptrinhmang.feature.checkin.ui.CheckInUiState

@Composable
fun IssuesTab(
    uiState: CheckInUiState,
    onSearchChanged: (String) -> Unit,
    onStatusFilterChanged: (String) -> Unit,
    onOpenCreateIssueDialog: () -> Unit,
    onOpenAssignDialog: (RoomIssue) -> Unit,
    onOpenResolveDialog: (RoomIssue) -> Unit,
    modifier: Modifier = Modifier
) {
    val statusFilters = listOf("Tất cả", "OPEN", "ASSIGNED", "RESOLVED")

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onOpenCreateIssueDialog) {
                Icon(Icons.Default.Add, contentDescription = "Báo sự cố")
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Search Input
            OutlinedTextField(
                value = uiState.issueSearchQuery,
                onValueChange = onSearchChanged,
                placeholder = { Text("Tìm theo tên sự cố, mã phòng, mã sự cố…") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Status filter chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                statusFilters.forEach { filter ->
                    val displayName = when (filter) {
                        "OPEN" -> "Chờ tiếp nhận"
                        "ASSIGNED" -> "Đang xử lý"
                        "RESOLVED" -> "Đã hoàn thành"
                        else -> "Tất cả"
                    }
                    FilterChip(
                        selected = uiState.selectedStatusFilter == filter,
                        onClick = { onStatusFilterChanged(filter) },
                        label = { Text(displayName, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Issue List
            if (uiState.issuesList.isEmpty()) {
                DauEmptyState(message = "Không có sự cố nào được tìm thấy.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.issuesList, key = { it.id }) { issue ->
                        IssueCard(
                            issue = issue,
                            onAssignClick = { onOpenAssignDialog(issue) },
                            onResolveClick = { onOpenResolveDialog(issue) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IssueCard(
    issue: RoomIssue,
    onAssignClick: () -> Unit,
    onResolveClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Code + Severity
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = issue.code,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• ${issue.roomCode}",
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                SeverityBadge(issue.severity)
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Title
            Text(
                text = issue.title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )

            // Description
            Text(
                text = issue.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Meta info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Báo bởi: ${issue.reporterName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Trạng thái: ${issue.status.displayName}",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodySmall,
                    color = when (issue.status) {
                        IssueStatus.OPEN -> Color(0xFFEF4444)
                        IssueStatus.ASSIGNED -> Color(0xFF3B82F6)
                        else -> Color(0xFF10B981)
                    }
                )
            }

            if (!issue.assignedTo.isNullOrBlank()) {
                Text(
                    text = "Phụ trách: ${issue.assignedTo}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (!issue.resolutionNote.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFECFDF5))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Nghiệm thu: ${issue.resolutionNote}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF065F46)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (issue.status == IssueStatus.OPEN) {
                    OutlinedButton(onClick = onAssignClick) {
                        Icon(Icons.Default.AssignmentInd, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Phân công")
                    }
                }
                if (issue.status == IssueStatus.ASSIGNED) {
                    Button(onClick = onResolveClick) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Nghiệm thu")
                    }
                }
            }
        }
    }
}

@Composable
private fun SeverityBadge(severity: IssueSeverity) {
    val (bgColor, textColor) = when (severity) {
        IssueSeverity.CRITICAL -> Pair(Color(0xFFFEE2E2), Color(0xFFDC2626))
        IssueSeverity.HIGH -> Pair(Color(0xFFFFEDD5), Color(0xFFEA580C))
        IssueSeverity.MEDIUM -> Pair(Color(0xFFFEF3C7), Color(0xFFD97706))
        IssueSeverity.LOW -> Pair(Color(0xFFD1FAE5), Color(0xFF059669))
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = severity.displayName.uppercase(),
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

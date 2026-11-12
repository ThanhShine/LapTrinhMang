package com.example.laptrinhmang.feature.approval.ui.tabs

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.laptrinhmang.core.ui.components.DauEmptyState
import com.example.laptrinhmang.feature.approval.domain.model.ApprovalItem
import com.example.laptrinhmang.feature.approval.ui.ApprovalUiState
import com.example.laptrinhmang.feature.approval.ui.components.PendingApprovalCard

@Composable
fun PendingApprovalsTab(
    uiState: ApprovalUiState,
    onSearchChanged: (String) -> Unit,
    onApproveSingle: (ApprovalItem) -> Unit,
    onRejectSingle: (ApprovalItem) -> Unit,
    onDetailClick: (ApprovalItem) -> Unit,
    onRemind: (ApprovalItem) -> Unit,
    onToggleBatchMode: () -> Unit,
    onToggleSelectBooking: (String) -> Unit,
    onSelectAll: () -> Unit,
    onBatchApprove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search field
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = onSearchChanged,
            label = { Text("Tìm kiếm đơn chờ duyệt") },
            placeholder = { Text("Người đặt, mã đơn, phòng…") },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (uiState.searchQuery.isNotBlank()) {
                    IconButton(onClick = { onSearchChanged("") }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Xóa tìm kiếm")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Batch mode controls header
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (uiState.isBatchSelectionMode) {
                        "Đã chọn: ${uiState.selectedBookingIds.size} đơn"
                    } else {
                        "Tổng số: ${uiState.pendingList.size} đơn chờ"
                    },
                    style = MaterialTheme.typography.bodyMedium
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (uiState.isBatchSelectionMode) {
                        IconButton(onClick = onSelectAll) {
                            Icon(imageVector = Icons.Default.SelectAll, contentDescription = "Chọn tất cả")
                        }

                        Button(
                            onClick = onBatchApprove,
                            enabled = uiState.selectedBookingIds.isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Duyệt (${uiState.selectedBookingIds.size})")
                        }
                    }

                    OutlinedButton(onClick = onToggleBatchMode) {
                        Text(if (uiState.isBatchSelectionMode) "Hủy chọn" else "Duyệt nhiều")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // List of items
        if (uiState.pendingList.isEmpty()) {
            DauEmptyState(message = "Hiện không có yêu cầu đặt phòng nào đang chờ duyệt.")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(uiState.pendingList, key = { it.id }) { item ->
                    PendingApprovalCard(
                        item = item,
                        isSelectionMode = uiState.isBatchSelectionMode,
                        isSelected = uiState.selectedBookingIds.contains(item.id),
                        onToggleSelect = { onToggleSelectBooking(item.id) },
                        onApprove = { onApproveSingle(item) },
                        onReject = { onRejectSingle(item) },
                        onDetailClick = { onDetailClick(item) },
                        onRemind = { onRemind(item) }
                    )
                }
            }
        }
    }
}

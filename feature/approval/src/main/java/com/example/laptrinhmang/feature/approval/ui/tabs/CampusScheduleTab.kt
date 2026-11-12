package com.example.laptrinhmang.feature.approval.ui.tabs

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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.laptrinhmang.core.ui.components.DauEmptyState
import com.example.laptrinhmang.feature.approval.domain.model.ApprovalItem
import com.example.laptrinhmang.feature.approval.domain.model.ApprovalStatus
import com.example.laptrinhmang.feature.approval.domain.model.RoomTimeSlot
import com.example.laptrinhmang.feature.approval.ui.ApprovalUiState

@Composable
fun CampusScheduleTab(
    uiState: ApprovalUiState,
    onDateChanged: (String) -> Unit,
    onBuildingChanged: (String) -> Unit,
    onOpenLockRoomDialog: () -> Unit,
    onOpenAdjustDialog: (ApprovalItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val buildings = listOf("Tất cả", "Nhà A", "Nhà B", "Nhà C", "Nhà Đa năng")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Controls Row: Date + Lock button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Ngày: ${uiState.selectedDate}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = onOpenLockRoomDialog,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Tạm khóa phòng")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Building Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            buildings.forEach { bldg ->
                FilterChip(
                    selected = uiState.selectedBuilding == bldg,
                    onClick = { onBuildingChanged(bldg) },
                    label = { Text(bldg) }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Schedule list
        val schedules = uiState.campusSchedule?.schedules ?: emptyList()
        if (schedules.isEmpty()) {
            DauEmptyState(message = "Không có thông tin lịch phòng nào trong ngày này.")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(schedules, key = { it.roomId }) { roomSchedule ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${roomSchedule.roomName} (${roomSchedule.building} - Tầng ${roomSchedule.floor})",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = roomSchedule.roomId,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (roomSchedule.timeSlots.isEmpty()) {
                                Text(
                                    text = "• Phòng trống cả ngày",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF10B981)
                                )
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    roomSchedule.timeSlots.forEach { slot ->
                                        SlotItemRow(
                                            slot = slot,
                                            onAdjustClick = {
                                                if (slot.bookingId != null) {
                                                    onOpenAdjustDialog(
                                                        ApprovalItem(
                                                            id = slot.bookingId,
                                                            roomId = roomSchedule.roomId,
                                                            roomName = roomSchedule.roomName,
                                                            capacity = 50,
                                                            requesterId = "dummy",
                                                            requesterName = slot.requesterName ?: "Đã duyệt",
                                                            requesterRole = "LECTURER",
                                                            departmentName = "Khoa CNTT",
                                                            purpose = slot.purpose,
                                                            attendeesCount = 30,
                                                            startTime = slot.startTime,
                                                            endTime = slot.endTime,
                                                            status = slot.status
                                                        )
                                                    )
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SlotItemRow(
    slot: RoomTimeSlot,
    onAdjustClick: () -> Unit
) {
    val isLocked = slot.isLocked || slot.status == ApprovalStatus.LOCKED
    val bgColor = if (isLocked) Color(0xFFF3F4F6) else Color(0xFFECFDF5)
    val borderColor = if (isLocked) Color(0xFF9CA3AF) else Color(0xFF34D399)

    Card(
        colors = CardDefaults.cardColors(containerColor = bgColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${slot.startTime.takeLast(14).take(5)} - ${slot.endTime.takeLast(14).take(5)}",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isLocked) "[TẠM KHÓA]" else "[ĐÃ DUYỆT]",
                        fontWeight = FontWeight.Bold,
                        color = if (isLocked) Color(0xFF4B5563) else Color(0xFF059669),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Text(
                    text = "Mục đích: ${slot.purpose}",
                    style = MaterialTheme.typography.bodySmall
                )
                if (!slot.requesterName.isNullOrBlank()) {
                    Text(
                        text = "Người dùng: ${slot.requesterName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (!isLocked && slot.bookingId != null) {
                OutlinedButton(
                    onClick = onAdjustClick,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Đổi lịch", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

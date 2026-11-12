package com.example.laptrinhmang.feature.approval.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.laptrinhmang.feature.approval.domain.model.ApprovalItem
import com.example.laptrinhmang.feature.approval.domain.model.BatchApprovalResult
import com.example.laptrinhmang.feature.approval.domain.model.LockedSlotResult

@Composable
fun RejectReasonDialog(
    item: ApprovalItem,
    onDismiss: () -> Unit,
    onConfirmReject: (reason: String) -> Unit
) {
    var reason by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(text = "Từ chối yêu cầu đặt phòng", fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                Text(
                    text = "Đơn ${item.id} - ${item.roomName}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Người đặt: ${item.requesterName} (${item.departmentName})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = reason,
                    onValueChange = {
                        reason = it
                        if (isError && it.isNotBlank()) isError = false
                    },
                    label = { Text("Lý do từ chối (bắt buộc) *") },
                    placeholder = { Text("Nhập lý do cụ thể vì sao không duyệt…") },
                    isError = isError,
                    supportingText = {
                        if (isError) {
                            Text("Bắt buộc phải nhập lý do khi từ chối đơn!", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (reason.isBlank()) {
                        isError = true
                    } else {
                        onConfirmReject(reason.trim())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Xác nhận từ chối")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}

@Composable
fun BatchApprovalResultDialog(
    result: BatchApprovalResult,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Kết quả duyệt hàng loạt (${result.successCount}/${result.total})",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(result.results) { single ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (single.success) Color(0xFFF0FDF4) else Color(0xFFFEF2F2)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (single.success) Icons.Default.CheckCircle else Icons.Default.Error,
                                contentDescription = null,
                                tint = if (single.success) Color(0xFF10B981) else Color(0xFFEF4444),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = single.bookingId,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = single.message,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (single.success) Color(0xFF15803D) else Color(0xFFB91C1C)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Đóng")
            }
        }
    )
}

@Composable
fun ApprovalDetailDialog(
    item: ApprovalItem,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Chi tiết đơn đặt phòng: ${item.id}", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                DetailRow(label = "Phòng:", value = "${item.roomName} (${item.capacity} chỗ)")
                DetailRow(label = "Người đặt:", value = "${item.requesterName} • ${item.departmentName}")
                DetailRow(label = "Thời gian:", value = "${item.startTime.take(16).replace("T", " ")} - ${item.endTime.take(16).replace("T", " ")}")
                DetailRow(label = "Mục đích:", value = item.purpose)
                DetailRow(label = "Số người:", value = "${item.attendeesCount} người")
                DetailRow(label = "Thiết bị:", value = if (item.requiredEquipment.isEmpty()) "Không yêu cầu thêm" else item.requiredEquipment.joinToString(", "))
                DetailRow(label = "Trạng thái:", value = item.status.name)
                if (!item.rejectionReason.isNullOrBlank()) {
                    DetailRow(label = "Lý do từ chối:", value = item.rejectionReason, isHighlight = true)
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Đóng")
            }
        }
    )
}

@Composable
fun AdjustScheduleDialog(
    item: ApprovalItem,
    onDismiss: () -> Unit,
    onConfirm: (newRoomId: String, newStartTime: String, newEndTime: String, reason: String) -> Unit
) {
    var newRoomId by remember { mutableStateOf("P.304") }
    var newStartTime by remember { mutableStateOf(item.startTime) }
    var newEndTime by remember { mutableStateOf(item.endTime) }
    var reason by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Điều chỉnh lịch đã duyệt: ${item.id}", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Phòng hiện tại: ${item.roomName}")
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = newRoomId,
                    onValueChange = { newRoomId = it },
                    label = { Text("Mã phòng mới điều chuyển") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = newStartTime,
                    onValueChange = { newStartTime = it },
                    label = { Text("Giờ bắt đầu mới") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = newEndTime,
                    onValueChange = { newEndTime = it },
                    label = { Text("Giờ kết thúc mới") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = reason,
                    onValueChange = {
                        reason = it
                        if (isError && it.isNotBlank()) isError = false
                    },
                    label = { Text("Lý do điều chuyển *") },
                    placeholder = { Text("Ví dụ: Phòng 302 sửa máy lạnh đột xuất…") },
                    isError = isError,
                    supportingText = {
                        if (isError) Text("Vui lòng nhập lý do điều chuyển", color = MaterialTheme.colorScheme.error)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (reason.isBlank()) {
                        isError = true
                    } else {
                        onConfirm(newRoomId, newStartTime, newEndTime, reason)
                    }
                }
            ) {
                Text("Lưu điều chỉnh")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        }
    )
}

@Composable
fun LockRoomSlotDialog(
    onDismiss: () -> Unit,
    onConfirm: (roomId: String, startTime: String, endTime: String, reason: String) -> Unit
) {
    var roomId by remember { mutableStateOf("P.302") }
    var startTime by remember { mutableStateOf("2026-11-20T07:00:00+07:00") }
    var endTime by remember { mutableStateOf("2026-11-20T12:00:00+07:00") }
    var reason by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tạm khóa khung giờ phòng", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(
                    text = "Lưu ý: Khóa khung giờ sẽ hiển thị danh sách các đơn bị ảnh hưởng. Hệ thống KHÔNG tự xóa lịch đã duyệt.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = roomId,
                    onValueChange = { roomId = it },
                    label = { Text("Mã phòng cần khóa") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = startTime,
                    onValueChange = { startTime = it },
                    label = { Text("Thời gian bắt đầu khóa") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = endTime,
                    onValueChange = { endTime = it },
                    label = { Text("Thời gian kết thúc khóa") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = reason,
                    onValueChange = {
                        reason = it
                        if (isError && it.isNotBlank()) isError = false
                    },
                    label = { Text("Lý do khóa phòng *") },
                    placeholder = { Text("Ví dụ: Bảo dưỡng hệ thống điện toàn tầng 3") },
                    isError = isError,
                    supportingText = {
                        if (isError) Text("Vui lòng nhập lý do khóa phòng", color = MaterialTheme.colorScheme.error)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (reason.isBlank()) {
                        isError = true
                    } else {
                        onConfirm(roomId, startTime, endTime, reason)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Tiến hành khóa")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        }
    )
}

@Composable
fun AffectedBookingsWarningDialog(
    result: LockedSlotResult,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = Color(0xFFF59E0B),
                modifier = Modifier.size(36.dp)
            )
        },
        title = {
            Text(
                text = "Cảnh báo: ${result.affectedBookings.size} đơn bị ảnh hưởng",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFEF3C7))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Phòng ${result.roomId} đã được khóa. Hệ thống KHÔNG tự xóa các lịch đã duyệt dưới đây. Vui lòng liên hệ người đặt để điều chuyển sang phòng khác:",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF92400E)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(result.affectedBookings) { booking ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "${booking.bookingId} - ${booking.requesterName} (${booking.requesterPhone})",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Mục đích: ${booking.purpose}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Đã hiểu và tiếp tục")
            }
        }
    )
}

@Composable
private fun DetailRow(label: String, value: String, isHighlight: Boolean = false) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Normal,
            color = if (isHighlight) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
        )
        HorizontalDivider(modifier = Modifier.padding(top = 4.dp), color = MaterialTheme.colorScheme.outlineVariant)
    }
}

package com.example.laptrinhmang.feature.checkin.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.laptrinhmang.feature.checkin.domain.model.EquipmentStatus
import com.example.laptrinhmang.feature.checkin.domain.model.IssueSeverity
import com.example.laptrinhmang.feature.checkin.domain.model.RoomEquipment
import com.example.laptrinhmang.feature.checkin.domain.model.RoomIssue

@Composable
fun CreateIssueDialog(
    onDismiss: () -> Unit,
    onSubmit: (roomCode: String, title: String, description: String, severity: IssueSeverity, images: List<String>) -> Unit
) {
    var roomCode by remember { mutableStateOf("P.302") }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var severity by remember { mutableStateOf(IssueSeverity.HIGH) }
    var imageUrl by remember { mutableStateOf("https://dau.edu.vn/uploads/issues/img_sample_error.jpg") }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
        },
        title = { Text("Báo cáo Sự cố Phòng học", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = roomCode,
                    onValueChange = { roomCode = it },
                    label = { Text("Mã phòng xảy ra sự cố *") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        if (isError && it.isNotBlank()) isError = false
                    },
                    label = { Text("Tiêu đề sự cố *") },
                    placeholder = { Text("Ví dụ: Máy chiếu không lên nguồn") },
                    isError = isError,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Mô tả chi tiết *") },
                    placeholder = { Text("Mô tả hiện tượng hỏng hóc để kỹ thuật viên chuẩn bị…") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text("Mức độ nghiêm trọng *", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IssueSeverity.values().forEach { sev ->
                        FilterChip(
                            selected = severity == sev,
                            onClick = { severity = sev },
                            label = { Text(sev.displayName, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank() || roomCode.isBlank() || description.isBlank()) {
                        isError = true
                    } else {
                        onSubmit(roomCode, title, description, severity, listOf(imageUrl))
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Gửi báo cáo")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        }
    )
}

@Composable
fun AssignIssueDialog(
    issue: RoomIssue,
    onDismiss: () -> Unit,
    onConfirm: (technicianName: String, note: String) -> Unit
) {
    var techName by remember { mutableStateOf("Võ Kỹ Thuật (Tổ Điện Lạnh)") }
    var note by remember { mutableStateOf("Cần xử lý gấp trước ca học chiều nay") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Phân công Xử lý Sự cố: ${issue.code}", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Sự cố: ${issue.title} (${issue.roomCode})")
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = techName,
                    onValueChange = { techName = it },
                    label = { Text("Tên Kỹ thuật viên / Tổ phụ trách") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Ghi chú phân công") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(techName, note) }) {
                Text("Xác nhận phân công")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        }
    )
}

@Composable
fun ResolveIssueDialog(
    issue: RoomIssue,
    onDismiss: () -> Unit,
    onConfirm: (resolutionNote: String) -> Unit
) {
    var resolutionNote by remember { mutableStateOf("Đã thay linh kiện mới và vệ sinh thiết bị. Đã kiểm tra chạy thử tốt.") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nghiệm thu Sự cố: ${issue.code}", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Sự cố: ${issue.title} (${issue.roomCode})")
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = resolutionNote,
                    onValueChange = { resolutionNote = it },
                    label = { Text("Biên bản nghiệm thu sửa chữa *") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(resolutionNote) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
            ) {
                Text("Nghiệm thu & Đóng sự cố")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        }
    )
}

@Composable
fun RatingDialog(
    onDismiss: () -> Unit,
    onSubmit: (cleanliness: Int, equipment: Int, lighting: Int, overall: Int, comment: String) -> Unit
) {
    var cleanliness by remember { mutableStateOf(5) }
    var equipment by remember { mutableStateOf(4) }
    var lighting by remember { mutableStateOf(5) }
    var comment by remember { mutableStateOf("Phòng học sạch sẽ, các thiết bị hoạt động tốt.") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Đánh giá Chất lượng Phòng học", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                StarRatingRow("Vệ sinh phòng:", cleanliness) { cleanliness = it }
                Spacer(modifier = Modifier.height(6.dp))
                StarRatingRow("Thiết bị (Máy chiếu/ĐH):", equipment) { equipment = it }
                Spacer(modifier = Modifier.height(6.dp))
                StarRatingRow("Ánh sáng & Âm thanh:", lighting) { lighting = it }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Ý kiến đóng góp") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val overall = (cleanliness + equipment + lighting) / 3
                onSubmit(cleanliness, equipment, lighting, overall, comment)
            }) {
                Text("Gửi đánh giá")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Bỏ qua") }
        }
    )
}

@Composable
private fun StarRatingRow(label: String, rating: Int, onRatingSelected: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Row {
            (1..5).forEach { star ->
                Icon(
                    imageVector = if (star <= rating) Icons.Default.Star else Icons.Default.StarOutline,
                    contentDescription = null,
                    tint = if (star <= rating) Color(0xFFF59E0B) else Color.Gray,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onRatingSelected(star) }
                )
            }
        }
    }
}

@Composable
fun EquipmentAddEditDialog(
    equipment: RoomEquipment?,
    onDismiss: () -> Unit,
    onSave: (RoomEquipment) -> Unit
) {
    var name by remember { mutableStateOf(equipment?.name ?: "") }
    var category by remember { mutableStateOf(equipment?.category ?: "Máy chiếu") }
    var roomCode by remember { mutableStateOf(equipment?.roomCode ?: "P.302") }
    var serialNumber by remember { mutableStateOf(equipment?.serialNumber ?: "") }
    var status by remember { mutableStateOf(equipment?.status ?: EquipmentStatus.ACTIVE) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (equipment != null) "Sửa thiết bị phòng" else "Thêm thiết bị mới (PH5)") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tên thiết bị *") },
                    placeholder = { Text("Ví dụ: Máy chiếu Panasonic PT-VMZ51") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Chủng loại") },
                    placeholder = { Text("Máy chiếu, Điều hòa, Switch, Âm thanh…") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = roomCode,
                    onValueChange = { roomCode = it },
                    label = { Text("Phòng lắp đặt *") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = serialNumber,
                    onValueChange = { serialNumber = it },
                    label = { Text("Số serial / Mã tài sản") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        RoomEquipment(
                            id = equipment?.id ?: 0L,
                            code = equipment?.code ?: "",
                            name = name,
                            category = category,
                            roomCode = roomCode,
                            roomName = "Phòng $roomCode",
                            status = status,
                            serialNumber = serialNumber,
                            installedDate = equipment?.installedDate ?: "2026-11-12"
                        )
                    )
                },
                enabled = name.isNotBlank() && roomCode.isNotBlank()
            ) {
                Text("Lưu thiết bị")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        }
    )
}

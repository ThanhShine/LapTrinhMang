package com.example.laptrinhmang.feature.booking.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.laptrinhmang.core.model.Room
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun CreateBookingDialog(
    room: Room,
    onDismiss: () -> Unit,
    onSubmit: (
        startTime: String,
        endTime: String,
        purpose: String,
        attendeeCount: Int,
        isRecurring: Boolean,
        recurringRule: String?
    ) -> Unit
) {
    var selectedDate by remember {
        mutableStateOf(LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE))
    }
    var startHour by remember { mutableStateOf("08:00") }
    var endHour by remember { mutableStateOf("11:00") }
    var purpose by remember { mutableStateOf("") }
    var attendeeCount by remember { mutableIntStateOf(30) }
    var isRecurring by remember { mutableStateOf(false) }

    var validationError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.MeetingRoom,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Đặt phòng ${room.roomCode}")
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = room.roomName,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = "${room.buildingName} • Tầng ${room.floor} • Sức chứa: ${room.capacity} người",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = selectedDate,
                    onValueChange = { selectedDate = it },
                    label = { Text("Ngày đặt phòng (YYYY-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = startHour,
                        onValueChange = { startHour = it },
                        label = { Text("Giờ bắt đầu") },
                        placeholder = { Text("08:00") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = endHour,
                        onValueChange = { endHour = it },
                        label = { Text("Giờ kết thúc") },
                        placeholder = { Text("11:00") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = purpose,
                    onValueChange = {
                        purpose = it
                        validationError = null
                    },
                    label = { Text("Mục đích sử dụng *") },
                    placeholder = { Text("Học nhóm, bảo vệ đồ án, sinh hoạt CLB...") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Số lượng người tham gia:",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { if (attendeeCount > 1) attendeeCount-- }
                    ) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Giảm")
                    }
                    Text(
                        text = "$attendeeCount",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    IconButton(
                        onClick = { if (attendeeCount < room.capacity) attendeeCount++ }
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Tăng")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = isRecurring,
                        onCheckedChange = { isRecurring = it }
                    )
                    Column {
                        Text(text = "Đặt phòng định kỳ", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = "Lặp lại vào thứ này hằng tuần trong học kỳ",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (validationError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = validationError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (purpose.isBlank()) {
                        validationError = "Vui lòng nhập mục đích sử dụng."
                        return@Button
                    }
                    if (startHour >= endHour) {
                        validationError = "Giờ kết thúc phải sau giờ bắt đầu."
                        return@Button
                    }

                    val startIso = "${selectedDate}T${startHour}:00+07:00"
                    val endIso = "${selectedDate}T${endHour}:00+07:00"
                    val recurringRule = if (isRecurring) "WEEKLY_UNTIL_SEMESTER_END" else null

                    onSubmit(startIso, endIso, purpose, attendeeCount, isRecurring, recurringRule)
                }
            ) {
                Text("Gửi yêu cầu")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}


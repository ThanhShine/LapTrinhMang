package com.example.laptrinhmang.feature.booking.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.example.laptrinhmang.core.model.Booking
import com.example.laptrinhmang.core.ui.components.DauTimeUtils

@Composable
fun ShareBookingDialog(
    booking: Booking,
    onDismiss: () -> Unit,
    onCopied: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val shareText = """
        📢 THÔNG TIN ĐẶT PHÒNG HỌC - DAU RMS
        ━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        🔖 Mã đơn: ${booking.bookingCode}
        🏛️ Phòng: ${booking.roomCode} - ${booking.roomName} (${booking.buildingName ?: "Khuôn viên trường"})
        ⏰ Thời gian: ${DauTimeUtils.formatDisplayDate(booking.startTime)} đến ${DauTimeUtils.formatDisplayDate(booking.endTime)}
        🎯 Mục đích: ${booking.purpose}
        👤 Người đặt: ${booking.userFullName}
        👥 Số lượng: ${booking.attendeeCount} người
        📊 Trạng thái: ${booking.status.displayName}
        ━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        Trường Đại học Kiến trúc Đà Nẵng
    """.trimIndent()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Chia sẻ thông tin đặt phòng")
            }
        },
        text = {
            Column {
                Text(
                    text = "Nội dung chia sẻ nhanh đến các thành viên trong nhóm / lớp:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(12.dp)
                ) {
                    Text(
                        text = shareText,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    shareViaIntent(context, shareText)
                    onDismiss()
                }
            ) {
                Icon(imageVector = Icons.Default.Share, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Gửi qua App")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = {
                    clipboardManager.setText(AnnotatedString(shareText))
                    onCopied()
                    onDismiss()
                }
            ) {
                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Sao chép")
            }
        }
    )
}

private fun shareViaIntent(context: Context, text: String) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Chia sẻ lịch đặt phòng")
    context.startActivity(shareIntent)
}


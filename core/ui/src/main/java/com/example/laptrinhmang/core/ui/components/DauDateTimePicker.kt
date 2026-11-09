package com.example.laptrinhmang.core.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object DauTimeUtils {
    val VIETNAM_ZONE_ID: ZoneId = ZoneId.of("Asia/Ho_Chi_Minh")

    fun formatIsoDateTime(dateTime: ZonedDateTime = ZonedDateTime.now(VIETNAM_ZONE_ID)): String {
        return dateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }

    fun formatDisplayDate(isoString: String?): String {
        if (isoString.isNullOrBlank()) return ""
        return runCatching {
            val zdt = ZonedDateTime.parse(isoString).withZoneSameInstant(VIETNAM_ZONE_ID)
            zdt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        }.getOrDefault(isoString ?: "")
    }
}

@Composable
fun DauDatePickerButton(
    selectedDateTime: ZonedDateTime,
    onDateTimeSelected: (ZonedDateTime) -> Unit,
    label: String = "Chọn ngày giờ",
    modifier: Modifier = Modifier
) {
    val formatted = selectedDateTime.withZoneSameInstant(DauTimeUtils.VIETNAM_ZONE_ID)
        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))

    OutlinedButton(
        onClick = {
            onDateTimeSelected(ZonedDateTime.now(DauTimeUtils.VIETNAM_ZONE_ID))
        },
        modifier = modifier.fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.DateRange, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "$label: $formatted")
        }
    }
}

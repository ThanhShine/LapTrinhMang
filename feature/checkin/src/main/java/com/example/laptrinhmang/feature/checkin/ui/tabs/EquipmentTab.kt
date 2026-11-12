package com.example.laptrinhmang.feature.checkin.ui.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.example.laptrinhmang.core.ui.components.DauConfirmDialog
import com.example.laptrinhmang.core.ui.components.DauEmptyState
import com.example.laptrinhmang.feature.checkin.domain.model.EquipmentStatus
import com.example.laptrinhmang.feature.checkin.domain.model.RoomEquipment
import com.example.laptrinhmang.feature.checkin.ui.CheckInUiState
import com.example.laptrinhmang.feature.checkin.ui.components.EquipmentAddEditDialog

@Composable
fun EquipmentTab(
    uiState: CheckInUiState,
    onOpenAddDialog: () -> Unit,
    onOpenEditDialog: (RoomEquipment) -> Unit,
    onCloseDialog: () -> Unit,
    onSaveEquipment: (RoomEquipment) -> Unit,
    onDeleteEquipment: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var equipmentToDelete by remember { mutableStateOf<RoomEquipment?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onOpenAddDialog) {
                Icon(Icons.Default.Add, contentDescription = "Thêm thiết bị")
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
            Text(
                text = "Quản lý Thiết bị Phòng học (PH5 - P2)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Theo dõi danh mục máy chiếu, điều hòa, switch mạng và trang thiết bị chuyên dụng của các phòng.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (uiState.equipmentList.isEmpty()) {
                DauEmptyState(message = "Chưa có thiết bị nào trong danh mục.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.equipmentList, key = { it.id }) { eq ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Build,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.padding(6.dp))
                                    Column {
                                        Text(
                                            text = eq.name,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                        Text(
                                            text = "Phòng ${eq.roomCode} • Loại: ${eq.category}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "Trạng thái: ${eq.status.displayName}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = when (eq.status) {
                                                EquipmentStatus.ACTIVE -> Color(0xFF10B981)
                                                EquipmentStatus.MAINTENANCE -> Color(0xFFF59E0B)
                                                EquipmentStatus.BROKEN -> Color(0xFFEF4444)
                                            }
                                        )
                                    }
                                }

                                Row {
                                    IconButton(onClick = { onOpenEditDialog(eq) }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Sửa")
                                    }
                                    IconButton(onClick = { equipmentToDelete = eq }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (uiState.isEquipmentDialogOpen) {
        EquipmentAddEditDialog(
            equipment = uiState.editingEquipment,
            onDismiss = onCloseDialog,
            onSave = onSaveEquipment
        )
    }

    equipmentToDelete?.let { eq ->
        DauConfirmDialog(
            title = "Xóa thiết bị",
            message = "Bạn có chắc chắn muốn xóa thiết bị '${eq.name}' (${eq.code}) khỏi danh mục không?",
            onConfirm = {
                onDeleteEquipment(eq.id)
                equipmentToDelete = null
            },
            onDismiss = { equipmentToDelete = null }
        )
    }
}

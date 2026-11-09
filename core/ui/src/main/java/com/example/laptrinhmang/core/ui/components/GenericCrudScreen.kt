package com.example.laptrinhmang.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class CrudItem(
    val id: String,
    val title: String,
    val subtitle: String
)

@Composable
fun GenericCrudScreen(
    title: String,
    items: List<CrudItem>,
    onAddItem: (title: String, subtitle: String) -> Unit,
    onEditItem: (item: CrudItem) -> Unit,
    onDeleteItem: (id: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddEditDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<CrudItem?>(null) }

    var itemToDelete by remember { mutableStateOf<CrudItem?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingItem = null
                showAddEditDialog = true
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Thêm mới")
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
            Text(text = title, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            if (items.isEmpty()) {
                DauEmptyState(message = "Chưa có dữ liệu danh mục nào")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items, key = { it.id }) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    editingItem = item
                                    showAddEditDialog = true
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = item.title, style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        text = item.subtitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Row {
                                    IconButton(onClick = {
                                        editingItem = item
                                        showAddEditDialog = true
                                    }) {
                                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Sửa")
                                    }
                                    IconButton(onClick = { itemToDelete = item }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Xóa",
                                            tint = MaterialTheme.colorScheme.error
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

    if (showAddEditDialog) {
        CrudAddEditDialog(
            item = editingItem,
            onDismiss = { showAddEditDialog = false },
            onSave = { titleText, subtitleText ->
                if (editingItem != null) {
                    onEditItem(editingItem!!.copy(title = titleText, subtitle = subtitleText))
                } else {
                    onAddItem(titleText, subtitleText)
                }
                showAddEditDialog = false
            }
        )
    }

    itemToDelete?.let { item ->
        DauConfirmDialog(
            title = "Xác nhận xóa",
            message = "Bạn có chắc chắn muốn xóa '${item.title}' không?",
            onConfirm = {
                onDeleteItem(item.id)
                itemToDelete = null
            },
            onDismiss = { itemToDelete = null }
        )
    }
}

@Composable
private fun CrudAddEditDialog(
    item: CrudItem?,
    onDismiss: () -> Unit,
    onSave: (title: String, subtitle: String) -> Unit
) {
    var titleText by remember { mutableStateOf(item?.title ?: "") }
    var subtitleText by remember { mutableStateOf(item?.subtitle ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = if (item != null) "Sửa danh mục" else "Thêm danh mục mới") },
        text = {
            Column {
                OutlinedTextField(
                    value = titleText,
                    onValueChange = { titleText = it },
                    label = { Text("Tên danh mục") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = subtitleText,
                    onValueChange = { subtitleText = it },
                    label = { Text("Mô tả / Thông tin phụ") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(titleText, subtitleText) },
                enabled = titleText.isNotBlank()
            ) {
                Text("Lưu")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}

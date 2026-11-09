package com.example.laptrinhmang.feature.admin.ui.departments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.SubdirectoryArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import com.example.laptrinhmang.core.ui.components.DauEmptyState
import com.example.laptrinhmang.core.ui.components.DauErrorState
import com.example.laptrinhmang.core.ui.components.DauLoadingState
import com.example.laptrinhmang.feature.admin.data.DepartmentItem

@Composable
fun AdminDepartmentsScreen(
    uiState: AdminDepartmentsUiState,
    onOpenAddDialog: () -> Unit,
    onCloseAddDialog: () -> Unit,
    onCreateDepartment: (name: String, parentId: Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onOpenAddDialog) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Thêm đơn vị")
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
            Text(text = "Cấu trúc tổ chức & Đơn vị (Cha-Con)", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))

            when {
                uiState.isLoading -> DauLoadingState()
                uiState.errorMessage != null -> DauErrorState(message = uiState.errorMessage)
                uiState.departments.isEmpty() -> DauEmptyState(message = "Chưa có đơn vị nào")
                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(uiState.departments, key = { it.id }) { rootDept ->
                            DepartmentNodeItem(dept = rootDept, level = 0)
                        }
                    }
                }
            }
        }
    }

    if (uiState.showAddDialog) {
        AddDepartmentDialog(onDismiss = onCloseAddDialog, onSave = onCreateDepartment)
    }
}

@Composable
private fun DepartmentNodeItem(dept: DepartmentItem, level: Int) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (level > 0) {
                    Icon(imageVector = Icons.Default.SubdirectoryArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                } else {
                    Icon(imageVector = Icons.Default.Business, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = dept.name,
                    style = if (level == 0) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium
                )
            }

            if (dept.children.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier.padding(start = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    dept.children.forEach { child ->
                        DepartmentNodeItem(dept = child, level = level + 1)
                    }
                }
            }
        }
    }
}

@Composable
private fun AddDepartmentDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, parentId: Long?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var parentIdText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Thêm đơn vị mới") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tên đơn vị / Khoa / Phòng") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = parentIdText,
                    onValueChange = { parentIdText = it },
                    label = { Text("ID đơn vị cha (Để trống nếu là đơn vị gốc)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val pId = parentIdText.toLongOrNull()
                    onSave(name, pId)
                },
                enabled = name.isNotBlank()
            ) {
                Text("Thêm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        }
    )
}

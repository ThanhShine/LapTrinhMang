package com.example.laptrinhmang.feature.admin.ui.users

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
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.laptrinhmang.core.model.User
import com.example.laptrinhmang.core.model.UserStatus
import com.example.laptrinhmang.core.ui.components.DauEmptyState
import com.example.laptrinhmang.core.ui.components.DauErrorState
import com.example.laptrinhmang.core.ui.components.DauLoadingState
import com.example.laptrinhmang.feature.admin.data.ImportCsvResult

@Composable
fun AdminUsersScreen(
    uiState: AdminUsersUiState,
    onSearchChanged: (String) -> Unit,
    onOpenAddDialog: () -> Unit,
    onCloseAddDialog: () -> Unit,
    onCreateUser: (email: String, fullName: String, role: String, deptId: Long) -> Unit,
    onOpenImportCsvDialog: () -> Unit,
    onCloseImportCsvDialog: () -> Unit,
    onImportCsv: (csvContent: String) -> Unit,
    onDismissTempPassword: () -> Unit,
    onToggleLock: (User) -> Unit,
    onResetPassword: (User) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onOpenImportCsvDialog) {
                    Icon(imageVector = Icons.Default.FileUpload, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Nhập từ file CSV")
                }
                FloatingActionButton(onClick = onOpenAddDialog) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Thêm người dùng")
                }
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
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onSearchChanged,
                label = { Text("Tìm kiếm theo email/tên") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                uiState.isLoading -> DauLoadingState()
                uiState.errorMessage != null -> DauErrorState(message = uiState.errorMessage)
                uiState.users.isEmpty() -> DauEmptyState(message = "Không tìm thấy người dùng nào")
                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(uiState.users, key = { it.id }) { user ->
                            val isLocked = user.status == UserStatus.LOCKED
                            val containerColor = if (isLocked) {
                                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                            } else {
                                MaterialTheme.colorScheme.surface
                            }

                            Card(
                                colors = CardDefaults.cardColors(containerColor = containerColor),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = user.fullName, style = MaterialTheme.typography.titleMedium)
                                        Text(
                                            text = "${user.email} | ${user.role.displayName}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        if (user.mustChangePassword) {
                                            Text(
                                                text = "Cần đổi mật khẩu lần đầu",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }

                                    Row {
                                        IconButton(onClick = { onToggleLock(user) }) {
                                            Icon(
                                                imageVector = if (isLocked) Icons.Default.LockOpen else Icons.Default.Lock,
                                                contentDescription = if (isLocked) "Mở khóa" else "Khóa",
                                                tint = if (isLocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                            )
                                        }
                                        IconButton(onClick = { onResetPassword(user) }) {
                                            Icon(
                                                imageVector = Icons.Default.LockReset,
                                                contentDescription = "Reset password"
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

    if (uiState.showAddDialog) {
        AddUserDialog(onDismiss = onCloseAddDialog, onSave = onCreateUser)
    }

    if (uiState.showImportCsvDialog) {
        ImportCsvDialog(
            result = uiState.csvImportResult,
            onDismiss = onCloseImportCsvDialog,
            onImport = onImportCsv
        )
    }

    uiState.createdTempPassword?.let { tempPass ->
        TempPasswordDialog(
            title = "Tài khoản đã tạo thành công",
            message = "Mật khẩu tạm được khởi tạo cho người dùng mới. Vui lòng sao chép và cung cấp cho người dùng. Mật khẩu này chỉ hiển thị đúng MỘT lần:",
            tempPassword = tempPass,
            onDismiss = onDismissTempPassword
        )
    }

    uiState.resetTempPassword?.let { tempPass ->
        TempPasswordDialog(
            title = "Đặt lại mật khẩu thành công",
            message = "Mật khẩu tạm mới đã được tạo. Vui lòng sao chép và cung cấp cho người dùng. Mật khẩu chỉ hiển thị đúng MỘT lần:",
            tempPassword = tempPass,
            onDismiss = onDismissTempPassword
        )
    }
}

@Composable
private fun AddUserDialog(
    onDismiss: () -> Unit,
    onSave: (email: String, fullName: String, role: String, deptId: Long) -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("STUDENT") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Thêm tài khoản mới (Cấp mật khẩu tạm)") },
        text = {
            Column {
                OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text("Họ và tên") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email trường (@dau.edu.vn)") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = role, onValueChange = { role = it }, label = { Text("Vai trò (STUDENT/LECTURER/ROOM_MANAGER/ADMIN)") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "* Hệ thống sẽ tự động sinh mật khẩu tạm ngẫu nhiên và bắt buộc người dùng đổi mật khẩu khi đăng nhập lần đầu.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(email, fullName, role, 10L) },
                enabled = email.isNotBlank() && fullName.isNotBlank()
            ) {
                Text("Tạo tài khoản")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        }
    )
}

@Composable
private fun ImportCsvDialog(
    result: ImportCsvResult?,
    onDismiss: () -> Unit,
    onImport: (csvContent: String) -> Unit
) {
    var csvText by remember {
        mutableStateOf(
            "email,full_name,role,department_id\n" +
            "sv.2051220091@dau.edu.vn,Nguyễn Văn Sinh,STUDENT,10\n" +
            "gv.tranvan@dau.edu.vn,Trần Văn Giảng,LECTURER,10"
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nhập tài khoản từ file CSV") },
        text = {
            Column {
                if (result == null) {
                    Text(
                        text = "Định dạng file CSV (Dòng đầu tiên là tiêu đề):\nemail,full_name,role,department_id",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = csvText,
                        onValueChange = { csvText = it },
                        label = { Text("Nội dung CSV") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    )
                } else {
                    Text(
                        text = "Kết quả nhập CSV (${result.successCount}/${result.totalRows} thành công):",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(modifier = Modifier.height(200.dp)) {
                        items(result.results) { item ->
                            val isSuccess = item.status == "SUCCESS"
                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                Text(
                                    text = "Dòng ${item.line}: ${item.email} - ${if (isSuccess) "THÀNH CÔNG" else "THẤT BẠI"}",
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSuccess) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                                if (isSuccess && item.temporaryPassword != null) {
                                    Text(
                                        text = "Mật khẩu tạm: ${item.temporaryPassword}",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                                if (!isSuccess && item.errorMessage != null) {
                                    Text(
                                        text = "Lỗi: ${item.errorMessage}",
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (result == null) {
                TextButton(
                    onClick = { onImport(csvText) },
                    enabled = csvText.isNotBlank()
                ) {
                    Text("Bắt đầu nhập")
                }
            } else {
                TextButton(onClick = onDismiss) {
                    Text("Đóng")
                }
            }
        },
        dismissButton = {
            if (result == null) {
                TextButton(onClick = onDismiss) { Text("Hủy") }
            }
        }
    )
}

@Composable
private fun TempPasswordDialog(
    title: String,
    message: String,
    tempPassword: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                Text(text = message, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = tempPassword,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Đã sao chép & Đóng") }
        }
    )
}

package com.example.laptrinhmang.feature.admin.ui.users

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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.laptrinhmang.core.model.User
import com.example.laptrinhmang.core.model.UserStatus
import com.example.laptrinhmang.core.ui.components.DauEmptyState
import com.example.laptrinhmang.core.ui.components.DauErrorState
import com.example.laptrinhmang.core.ui.components.DauLoadingState

@Composable
fun AdminUsersScreen(
    uiState: AdminUsersUiState,
    onSearchChanged: (String) -> Unit,
    onOpenAddDialog: () -> Unit,
    onCloseAddDialog: () -> Unit,
    onCreateUser: (username: String, fullName: String, email: String, role: String, deptId: Long, pass: String) -> Unit,
    onToggleLock: (User) -> Unit,
    onOpenResetPasswordDialog: (User) -> Unit,
    onCloseResetPasswordDialog: () -> Unit,
    onResetPassword: (newPassword: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onOpenAddDialog) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Thêm người dùng")
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
                label = { Text("Tìm kiếm theo tên/mã/email") },
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
                                            text = "${user.username} | ${user.role.displayName}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = user.email,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Row {
                                        IconButton(onClick = { onToggleLock(user) }) {
                                            Icon(
                                                imageVector = if (isLocked) Icons.Default.LockOpen else Icons.Default.Lock,
                                                contentDescription = if (isLocked) "Mở khóa" else "Khóa",
                                                tint = if (isLocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                            )
                                        }
                                        IconButton(onClick = { onOpenResetPasswordDialog(user) }) {
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

    uiState.resettingPasswordUser?.let { user ->
        ResetPasswordDialog(
            username = user.username,
            onDismiss = onCloseResetPasswordDialog,
            onConfirm = onResetPassword
        )
    }
}

@Composable
private fun AddUserDialog(
    onDismiss: () -> Unit,
    onSave: (username: String, fullName: String, email: String, role: String, deptId: Long, pass: String) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("STUDENT") }
    var password by remember { mutableStateOf("Password123@") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Thêm người dùng mới") },
        text = {
            Column {
                OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Mã MSSV/MSGV") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text("Họ và tên") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email trường") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = role, onValueChange = { role = it }, label = { Text("Vai trò (STUDENT/LECTURER/ROOM_MANAGER/ADMIN)") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Mật khẩu ban đầu") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(username, fullName, email, role, 10L, password) },
                enabled = username.isNotBlank() && fullName.isNotBlank()
            ) {
                Text("Thêm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        }
    )
}

@Composable
private fun ResetPasswordDialog(
    username: String,
    onDismiss: () -> Unit,
    onConfirm: (newPassword: String) -> Unit
) {
    var newPassword by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Đặt lại mật khẩu") },
        text = {
            Column {
                Text(text = "Đặt lại mật khẩu mới cho tài khoản: $username")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Mật khẩu mới") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(newPassword) },
                enabled = newPassword.length >= 8
            ) {
                Text("Xác nhận")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        }
    )
}

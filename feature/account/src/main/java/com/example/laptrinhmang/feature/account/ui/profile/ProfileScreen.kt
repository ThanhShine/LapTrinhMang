package com.example.laptrinhmang.feature.account.ui.profile

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onOpenEditDialog: () -> Unit,
    onCloseEditDialog: () -> Unit,
    onEditFullNameChanged: (String) -> Unit,
    onEditPhoneChanged: (String) -> Unit,
    onEditAvatarUrlChanged: (String) -> Unit,
    onSaveProfile: () -> Unit,
    onOpenChangePasswordDialog: () -> Unit,
    onCloseChangePasswordDialog: () -> Unit,
    onOldPasswordChanged: (String) -> Unit,
    onNewPasswordChanged: (String) -> Unit,
    onConfirmNewPasswordChanged: (String) -> Unit,
    onSubmitChangePassword: () -> Unit,
    onLogoutClick: (onLoggedOut: () -> Unit) -> Unit,
    onLoggedOut: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hồ sơ cá nhân") }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val user = uiState.user

            // Avatar
            AsyncImage(
                model = user?.avatarUrl ?: "https://dau.edu.vn/uploads/avatars/default.png",
                contentDescription = "Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = user?.fullName ?: "Người dùng DAU",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = user?.role?.displayName ?: "Sinh viên",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // User Info Card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ProfileInfoRow(icon = Icons.Default.Badge, label = "Mã định danh", value = user?.username ?: "-")
                    ProfileInfoRow(icon = Icons.Default.Email, label = "Email trường", value = user?.email ?: "-")
                    ProfileInfoRow(icon = Icons.Default.Phone, label = "Số điện thoại", value = user?.phone ?: "Chưa cập nhật")
                    ProfileInfoRow(icon = Icons.Default.Business, label = "Đơn vị / Khoa", value = user?.departmentName ?: "Khoa CNTT")
                }
            }

            uiState.errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            uiState.successMessage?.let { msg ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = msg, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = onOpenEditDialog,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Chỉnh sửa thông tin")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onOpenChangePasswordDialog,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.Lock, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Đổi mật khẩu")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onNavigateToNotifications,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Xem thông báo & Push FCM")
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { onLogoutClick(onLoggedOut) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Đăng xuất")
            }
        }
    }

    if (uiState.showEditProfileDialog) {
        EditProfileDialog(
            fullName = uiState.editFullName,
            phone = uiState.editPhone,
            avatarUrl = uiState.editAvatarUrl,
            onFullNameChanged = onEditFullNameChanged,
            onPhoneChanged = onEditPhoneChanged,
            onAvatarUrlChanged = onEditAvatarUrlChanged,
            onDismiss = onCloseEditDialog,
            onSave = onSaveProfile
        )
    }

    if (uiState.showChangePasswordDialog) {
        ChangePasswordDialog(
            oldPassword = uiState.oldPassword,
            newPassword = uiState.newPassword,
            confirmNewPassword = uiState.confirmNewPassword,
            onOldPasswordChanged = onOldPasswordChanged,
            onNewPasswordChanged = onNewPasswordChanged,
            onConfirmNewPasswordChanged = onConfirmNewPasswordChanged,
            onDismiss = onCloseChangePasswordDialog,
            onSubmit = onSubmitChangePassword
        )
    }
}

@Composable
private fun ProfileInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = value, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun EditProfileDialog(
    fullName: String,
    phone: String,
    avatarUrl: String,
    onFullNameChanged: (String) -> Unit,
    onPhoneChanged: (String) -> Unit,
    onAvatarUrlChanged: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Chỉnh sửa hồ sơ") },
        text = {
            Column {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = onFullNameChanged,
                    label = { Text("Họ và tên") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = onPhoneChanged,
                    label = { Text("Số điện thoại") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = avatarUrl,
                    onValueChange = onAvatarUrlChanged,
                    label = { Text("URL Ảnh đại diện") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onSave) { Text("Lưu") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        }
    )
}

@Composable
private fun ChangePasswordDialog(
    oldPassword: String,
    newPassword: String,
    confirmNewPassword: String,
    onOldPasswordChanged: (String) -> Unit,
    onNewPasswordChanged: (String) -> Unit,
    onConfirmNewPasswordChanged: (String) -> Unit,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Đổi mật khẩu") },
        text = {
            Column {
                OutlinedTextField(
                    value = oldPassword,
                    onValueChange = onOldPasswordChanged,
                    label = { Text("Mật khẩu hiện tại") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = onNewPasswordChanged,
                    label = { Text("Mật khẩu mới") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = confirmNewPassword,
                    onValueChange = onConfirmNewPasswordChanged,
                    label = { Text("Xác nhận mật khẩu mới") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onSubmit) { Text("Đổi mật khẩu") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        }
    )
}

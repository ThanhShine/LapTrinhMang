package com.example.laptrinhmang.feature.account.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.laptrinhmang.core.model.User

@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onLoginClick: (onSuccess: (User) -> Unit) -> Unit,
    onQuickFillRole: (String) -> Unit,
    onOpenContactAdmin: () -> Unit,
    onCloseContactAdmin: () -> Unit,
    onLoginSuccess: (User) -> Unit,
    modifier: Modifier = Modifier
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "DAU RMS",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Hệ thống quản lý phòng học DAU",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = uiState.email,
            onValueChange = onEmailChanged,
            label = { Text("Email trường (@dau.edu.vn)") },
            leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.password,
            onValueChange = onPasswordChanged,
            label = { Text("Mật khẩu") },
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Ẩn/hiện mật khẩu"
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        uiState.errorMessage?.let { error ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onOpenContactAdmin) {
                Text("Quên mật khẩu?")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onLoginClick(onLoginSuccess) },
            enabled = !uiState.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Đăng nhập")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Dữ liệu thử nghiệm (Thử các vai trò):",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            OutlinedButton(
                onClick = { onQuickFillRole("ADMIN") },
                modifier = Modifier.weight(1f)
            ) { Text("Admin", style = MaterialTheme.typography.labelSmall) }

            OutlinedButton(
                onClick = { onQuickFillRole("ROOM_MANAGER") },
                modifier = Modifier.weight(1f)
            ) { Text("Quản lý", style = MaterialTheme.typography.labelSmall) }

            OutlinedButton(
                onClick = { onQuickFillRole("LECTURER") },
                modifier = Modifier.weight(1f)
            ) { Text("G.Viên", style = MaterialTheme.typography.labelSmall) }

            OutlinedButton(
                onClick = { onQuickFillRole("STUDENT") },
                modifier = Modifier.weight(1f)
            ) { Text("S.Viên", style = MaterialTheme.typography.labelSmall) }
        }

        Spacer(modifier = Modifier.height(4.dp))
        OutlinedButton(
            onClick = { onQuickFillRole("MUST_CHANGE_PASS") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Thử tài khoản cần Đổi mật khẩu lần đầu (mustChangePassword)", style = MaterialTheme.typography.labelSmall)
        }
    }

    if (uiState.showContactAdminDialog) {
        AlertDialog(
            onDismissRequest = onCloseContactAdmin,
            title = { Text("Quên mật khẩu") },
            text = { Text("Vui lòng liên hệ Quản trị viên (Phòng Cơ sở vật chất / Admin) để được cấp lại mật khẩu tạm thời.") },
            confirmButton = {
                TextButton(onClick = onCloseContactAdmin) { Text("Đóng") }
            }
        )
    }
}

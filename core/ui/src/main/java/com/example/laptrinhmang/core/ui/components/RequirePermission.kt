package com.example.laptrinhmang.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.laptrinhmang.core.model.User

/**
 * Checks if user has specific permission code.
 * Admins implicitly have all permissions.
 */
fun User?.hasPermission(permissionCode: String): Boolean {
    if (this == null) return false
    return this.hasPermission(permissionCode)
}

/**
 * Composable wrapper to conditionally render content based on user permission.
 * If user does not have permission, fallback content (or default message) is shown.
 * Note: UI hiding is for UX only; server API performs true enforcement.
 */
@Composable
fun RequirePermission(
    user: User?,
    permissionCode: String,
    fallback: @Composable () -> Unit = { DefaultNoPermissionView() },
    content: @Composable () -> Unit
) {
    if (user?.hasPermission(permissionCode) == true) {
        content()
    } else {
        fallback()
    }
}

@Composable
fun DefaultNoPermissionView(modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Bạn không có quyền truy cập hoặc thực hiện thao tác này.",
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

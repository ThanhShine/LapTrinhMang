package com.example.laptrinhmang.feature.admin.ui.roles

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
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.laptrinhmang.core.model.Permission
import com.example.laptrinhmang.core.ui.components.DauEmptyState
import com.example.laptrinhmang.core.ui.components.DauErrorState
import com.example.laptrinhmang.core.ui.components.DauLoadingState

@Composable
fun AdminRolesScreen(
    uiState: AdminRolesUiState,
    onPermissionToggle: (roleId: String, permissionCode: String, isChecked: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Cấu hình phân quyền vai trò", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))

        when {
            uiState.isLoading -> DauLoadingState()
            uiState.errorMessage != null -> DauErrorState(message = uiState.errorMessage)
            uiState.roles.isEmpty() -> DauEmptyState(message = "Không có vai trò nào")
            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(uiState.roles, key = { it.id }) { roleItem ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = roleItem.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Permission.entries.forEach { p ->
                                    val isChecked = roleItem.permissions.contains(p.code)
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Checkbox(
                                            checked = isChecked,
                                            onCheckedChange = { checked ->
                                                onPermissionToggle(roleItem.id, p.code, checked)
                                            }
                                        )
                                        Column {
                                            Text(text = p.code, style = MaterialTheme.typography.bodyMedium)
                                            Text(text = p.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
}

package com.example.laptrinhmang.feature.admin.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.laptrinhmang.feature.admin.ui.audit.AdminAuditLogScreen
import com.example.laptrinhmang.feature.admin.ui.audit.AdminAuditLogUiState
import com.example.laptrinhmang.feature.admin.ui.departments.AdminDepartmentsScreen
import com.example.laptrinhmang.feature.admin.ui.departments.AdminDepartmentsUiState
import com.example.laptrinhmang.feature.admin.ui.roles.AdminRolesScreen
import com.example.laptrinhmang.feature.admin.ui.roles.AdminRolesUiState
import com.example.laptrinhmang.feature.admin.ui.users.AdminUsersScreen
import com.example.laptrinhmang.feature.admin.ui.users.AdminUsersUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    usersUiState: AdminUsersUiState,
    onSearchUsersChanged: (String) -> Unit,
    onOpenAddUserDialog: () -> Unit,
    onCloseAddUserDialog: () -> Unit,
    onCreateUser: (username: String, fullName: String, email: String, role: String, deptId: Long, pass: String) -> Unit,
    onToggleLockUser: (com.example.laptrinhmang.core.model.User) -> Unit,
    onOpenResetPasswordDialog: (com.example.laptrinhmang.core.model.User) -> Unit,
    onCloseResetPasswordDialog: () -> Unit,
    onResetPassword: (String) -> Unit,

    rolesUiState: AdminRolesUiState,
    onPermissionToggle: (roleId: String, permissionCode: String, isChecked: Boolean) -> Unit,

    deptsUiState: AdminDepartmentsUiState,
    onOpenAddDeptDialog: () -> Unit,
    onCloseAddDeptDialog: () -> Unit,
    onCreateDepartment: (name: String, parentId: Long?) -> Unit,

    auditUiState: AdminAuditLogUiState,
    onAuditActionFilterChanged: (String) -> Unit,

    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Người dùng", "Vai trò & Quyền", "Tổ chức & Đơn vị", "Audit Log")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quản trị hệ thống (PH5)") }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(text = title) }
                    )
                }
            }

            when (selectedTabIndex) {
                0 -> AdminUsersScreen(
                    uiState = usersUiState,
                    onSearchChanged = onSearchUsersChanged,
                    onOpenAddDialog = onOpenAddUserDialog,
                    onCloseAddDialog = onCloseAddUserDialog,
                    onCreateUser = onCreateUser,
                    onToggleLock = onToggleLockUser,
                    onOpenResetPasswordDialog = onOpenResetPasswordDialog,
                    onCloseResetPasswordDialog = onCloseResetPasswordDialog,
                    onResetPassword = onResetPassword
                )
                1 -> AdminRolesScreen(
                    uiState = rolesUiState,
                    onPermissionToggle = onPermissionToggle
                )
                2 -> AdminDepartmentsScreen(
                    uiState = deptsUiState,
                    onOpenAddDialog = onOpenAddDeptDialog,
                    onCloseAddDialog = onCloseAddDeptDialog,
                    onCreateDepartment = onCreateDepartment
                )
                3 -> AdminAuditLogScreen(
                    uiState = auditUiState,
                    onActionFilterChanged = onAuditActionFilterChanged
                )
            }
        }
    }
}

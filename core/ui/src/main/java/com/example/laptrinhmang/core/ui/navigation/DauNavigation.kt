package com.example.laptrinhmang.core.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.laptrinhmang.core.model.Role
import com.example.laptrinhmang.core.model.User

sealed class NavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Booking : NavItem("booking", "Đặt phòng", Icons.Default.DateRange)
    data object Approval : NavItem("approval", "Phê duyệt", Icons.Default.AssignmentTurnedIn)
    data object CheckIn : NavItem("checkin", "Check-in / Sự cố", Icons.Default.QrCodeScanner)
    data object Admin : NavItem("admin", "Quản trị", Icons.Default.AdminPanelSettings)
    data object Profile : NavItem("account", "Tài khoản", Icons.Default.Person)

    companion object {
        fun getNavItemsForUser(user: User?): List<NavItem> {
            if (user == null) return listOf(Booking, Profile)

            return when (user.role) {
                Role.ADMIN -> listOf(Admin, Booking, Approval, CheckIn, Profile)
                Role.ROOM_MANAGER -> listOf(CheckIn, Booking, Profile)
                Role.TRAINING_OFFICER -> listOf(Booking, Approval, Profile) // Có cả đặt phòng lẫn phê duyệt!
                Role.LECTURER -> listOf(Booking, CheckIn, Profile)
                Role.STUDENT -> listOf(Booking, Profile)
                Role.DEAN, Role.APPROVER -> listOf(Approval, Booking, Profile)
                Role.TECHNICIAN -> listOf(CheckIn, Profile)
                Role.EXAM_OFFICE -> listOf(Booking, Profile)
            }
        }
    }
}

@Composable
fun DauBottomNavigationBar(
    user: User?,
    currentRoute: String?,
    onNavigateToRoute: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = NavItem.getNavItemsForUser(user)

    NavigationBar(modifier = modifier) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigateToRoute(item.route) },
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(text = item.title) }
            )
        }
    }
}

package com.example.laptrinhmang.core.ui.navigation

import androidx.navigation.NavController

object NotificationNavigator {

    fun handleNotificationClick(
        targetScreen: String?,
        targetId: String?,
        navController: NavController
    ) {
        if (targetScreen.isNullOrBlank()) return

        when (targetScreen?.uppercase()) {
            "BOOKING_DETAIL" -> {
                val route = if (!targetId.isNullOrBlank()) "booking_detail/$targetId" else "booking"
                navController.navigate(route)
            }
            "ROOM_DETAIL" -> {
                val route = if (!targetId.isNullOrBlank()) "room_detail/$targetId" else "booking"
                navController.navigate(route)
            }
            "APPROVAL_DETAIL" -> {
                val route = if (!targetId.isNullOrBlank()) "approval_detail/$targetId" else "approval"
                navController.navigate(route)
            }
            "ISSUE_DETAIL" -> {
                val route = if (!targetId.isNullOrBlank()) "issue_detail/$targetId" else "checkin"
                navController.navigate(route)
            }
            "NOTIFICATIONS" -> {
                navController.navigate("notifications")
            }
            else -> {
                navController.navigate("booking")
            }
        }
    }
}

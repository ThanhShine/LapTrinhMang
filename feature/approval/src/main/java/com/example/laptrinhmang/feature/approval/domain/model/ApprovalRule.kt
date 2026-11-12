package com.example.laptrinhmang.feature.approval.domain.model

data class ApprovalRule(
    val id: String,
    val name: String,
    val description: String,
    val isEnabled: Boolean = true,
    val targetRole: String,
    val maxCapacity: Int = 100,
    val autoApprove: Boolean = false,
    val escalationHours: Int = 24
)

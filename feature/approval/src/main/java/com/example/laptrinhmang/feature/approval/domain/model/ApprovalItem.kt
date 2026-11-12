package com.example.laptrinhmang.feature.approval.domain.model

data class ApprovalItem(
    val id: String,
    val roomId: String,
    val roomName: String,
    val capacity: Int,
    val requesterId: String,
    val requesterName: String,
    val requesterRole: String,
    val departmentName: String,
    val purpose: String,
    val attendeesCount: Int,
    val startTime: String,
    val endTime: String,
    val status: ApprovalStatus = ApprovalStatus.PENDING,
    val priority: ApprovalPriority = ApprovalPriority.NORMAL,
    val requiredEquipment: List<String> = emptyList(),
    val rejectionReason: String? = null,
    val decidedBy: String? = null,
    val createdAt: String = ""
)

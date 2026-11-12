package com.example.laptrinhmang.feature.approval.domain.model

data class BatchApprovalSingleResult(
    val bookingId: String,
    val success: Boolean,
    val status: ApprovalStatus,
    val message: String
)

data class BatchApprovalResult(
    val total: Int,
    val successCount: Int,
    val failureCount: Int,
    val results: List<BatchApprovalSingleResult> = emptyList()
)

package com.example.laptrinhmang.feature.approval.domain.model

enum class ApprovalStatus {
    PENDING,
    APPROVED,
    REJECTED,
    CANCELLED,
    LOCKED
}

enum class ApprovalPriority {
    NORMAL,
    HIGH,
    URGENT
}

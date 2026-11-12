package com.example.laptrinhmang.feature.approval.domain.model

data class RoomTimeSlot(
    val bookingId: String?,
    val purpose: String,
    val requesterName: String? = null,
    val startTime: String,
    val endTime: String,
    val status: ApprovalStatus,
    val isLocked: Boolean = false,
    val lockReason: String? = null
)

data class RoomSchedule(
    val roomId: String,
    val roomName: String,
    val building: String,
    val floor: Int = 1,
    val timeSlots: List<RoomTimeSlot> = emptyList()
)

data class CampusSchedule(
    val date: String,
    val schedules: List<RoomSchedule> = emptyList()
)

data class AffectedBooking(
    val bookingId: String,
    val requesterName: String,
    val requesterPhone: String = "",
    val purpose: String,
    val status: ApprovalStatus,
    val isCancelled: Boolean = false
)

data class LockedSlotResult(
    val lockId: String,
    val roomId: String,
    val startTime: String,
    val endTime: String,
    val reason: String,
    val affectedBookings: List<AffectedBooking> = emptyList()
)

package com.example.laptrinhmang.feature.approval.data.model

import com.google.gson.annotations.SerializedName

data class ApprovalItemDto(
    @SerializedName("id") val id: String,
    @SerializedName("room_id") val roomId: String,
    @SerializedName("room_name") val roomName: String,
    @SerializedName("capacity") val capacity: Int,
    @SerializedName("requester_id") val requesterId: String,
    @SerializedName("requester_name") val requesterName: String,
    @SerializedName("requester_role") val requesterRole: String,
    @SerializedName("department_name") val departmentName: String,
    @SerializedName("purpose") val purpose: String,
    @SerializedName("attendees_count") val attendeesCount: Int,
    @SerializedName("start_time") val startTime: String,
    @SerializedName("end_time") val endTime: String,
    @SerializedName("status") val status: String,
    @SerializedName("priority") val priority: String? = "NORMAL",
    @SerializedName("required_equipment") val requiredEquipment: List<String>? = emptyList(),
    @SerializedName("reason") val rejectionReason: String? = null,
    @SerializedName("approved_by") val approvedBy: String? = null,
    @SerializedName("created_at") val createdAt: String? = ""
)

data class ApprovalDecisionRequest(
    @SerializedName("action") val action: String, // "APPROVE" or "REJECT"
    @SerializedName("reason") val reason: String? = null,
    @SerializedName("note") val note: String? = null
)

data class BatchApprovalRequest(
    @SerializedName("action") val action: String,
    @SerializedName("booking_ids") val bookingIds: List<String>,
    @SerializedName("reason") val reason: String? = null
)

data class BatchSingleResultDto(
    @SerializedName("booking_id") val bookingId: String,
    @SerializedName("success") val success: Boolean,
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String
)

data class BatchApprovalResponseDto(
    @SerializedName("total") val total: Int,
    @SerializedName("success_count") val successCount: Int,
    @SerializedName("failure_count") val failureCount: Int,
    @SerializedName("results") val results: List<BatchSingleResultDto>
)

data class RoomTimeSlotDto(
    @SerializedName("booking_id") val bookingId: String?,
    @SerializedName("purpose") val purpose: String,
    @SerializedName("requester_name") val requesterName: String?,
    @SerializedName("start_time") val startTime: String,
    @SerializedName("end_time") val endTime: String,
    @SerializedName("status") val status: String,
    @SerializedName("is_locked") val isLocked: Boolean? = false,
    @SerializedName("lock_reason") val lockReason: String? = null
)

data class RoomScheduleDto(
    @SerializedName("room_id") val roomId: String,
    @SerializedName("room_name") val roomName: String,
    @SerializedName("building") val building: String,
    @SerializedName("floor") val floor: Int? = 1,
    @SerializedName("time_slots") val timeSlots: List<RoomTimeSlotDto>
)

data class CampusScheduleResponseDto(
    @SerializedName("date") val date: String,
    @SerializedName("schedules") val schedules: List<RoomScheduleDto>
)

data class AdjustScheduleRequest(
    @SerializedName("new_room_id") val newRoomId: String,
    @SerializedName("new_start_time") val newStartTime: String,
    @SerializedName("new_end_time") val newEndTime: String,
    @SerializedName("adjustment_reason") val adjustmentReason: String
)

data class LockScheduleRequest(
    @SerializedName("room_id") val roomId: String,
    @SerializedName("start_time") val startTime: String,
    @SerializedName("end_time") val endTime: String,
    @SerializedName("reason") val reason: String,
    @SerializedName("notify_affected_users") val notifyAffectedUsers: Boolean = true
)

data class AffectedBookingDto(
    @SerializedName("booking_id") val bookingId: String,
    @SerializedName("requester_name") val requesterName: String,
    @SerializedName("requester_phone") val requesterPhone: String? = "",
    @SerializedName("purpose") val purpose: String,
    @SerializedName("status") val status: String,
    @SerializedName("is_cancelled") val isCancelled: Boolean? = false
)

data class LockScheduleResponseDto(
    @SerializedName("lock_id") val lockId: String,
    @SerializedName("room_id") val roomId: String,
    @SerializedName("start_time") val startTime: String,
    @SerializedName("end_time") val endTime: String,
    @SerializedName("reason") val reason: String,
    @SerializedName("affected_bookings") val affectedBookings: List<AffectedBookingDto>? = emptyList()
)

data class ApprovalRuleDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("is_enabled") val isEnabled: Boolean,
    @SerializedName("target_role") val targetRole: String,
    @SerializedName("max_capacity") val maxCapacity: Int,
    @SerializedName("auto_approve") val autoApprove: Boolean,
    @SerializedName("escalation_hours") val escalationHours: Int
)

data class RoomCategoryDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("room_count") val roomCount: Int? = 0
)

data class ApiResponse<T>(
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: T? = null
)

package com.example.laptrinhmang.core.model

import com.google.gson.annotations.SerializedName

data class Booking(
    @SerializedName("id") val id: Long,
    @SerializedName("booking_code") val bookingCode: String = "",
    @SerializedName("room_id") val roomId: Long,
    @SerializedName("room_code") val roomCode: String = "",
    @SerializedName("room_name") val roomName: String = "",
    @SerializedName("building_name") val buildingName: String? = null,
    @SerializedName("user_id") val userId: Long,
    @SerializedName("user_full_name") val userFullName: String = "",
    @SerializedName("user_phone") val userPhone: String? = null,
    @SerializedName("start_time") val startTime: String,
    @SerializedName("end_time") val endTime: String,
    @SerializedName("purpose") val purpose: String,
    @SerializedName("attendee_count") val attendeeCount: Int = 1,
    @SerializedName("status") val status: BookingStatus = BookingStatus.PENDING,
    @SerializedName("rejection_reason") val rejectionReason: String? = null,
    @SerializedName("is_recurring") val isRecurring: Boolean = false,
    @SerializedName("recurring_rule") val recurringRule: String? = null,
    @SerializedName("created_at") val createdAt: String? = null
)

data class RoomScheduleItem(
    @SerializedName("booking_id") val bookingId: Long,
    @SerializedName("start_time") val startTime: String,
    @SerializedName("end_time") val endTime: String,
    @SerializedName("purpose") val purpose: String,
    @SerializedName("user_full_name") val userFullName: String = "",
    @SerializedName("status") val status: BookingStatus = BookingStatus.APPROVED
)


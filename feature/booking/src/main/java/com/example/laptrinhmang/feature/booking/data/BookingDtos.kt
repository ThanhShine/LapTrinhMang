package com.example.laptrinhmang.feature.booking.data

import com.google.gson.annotations.SerializedName

data class CreateBookingRequest(
    @SerializedName("room_id") val roomId: Long,
    @SerializedName("start_time") val startTime: String,
    @SerializedName("end_time") val endTime: String,
    @SerializedName("purpose") val purpose: String,
    @SerializedName("attendee_count") val attendeeCount: Int = 1,
    @SerializedName("is_recurring") val isRecurring: Boolean = false,
    @SerializedName("recurring_rule") val recurringRule: String? = null
)

data class UpdateBookingRequest(
    @SerializedName("start_time") val startTime: String,
    @SerializedName("end_time") val endTime: String,
    @SerializedName("purpose") val purpose: String,
    @SerializedName("attendee_count") val attendeeCount: Int = 1
)

data class CancelBookingRequest(
    @SerializedName("reason") val reason: String
)

data class SearchAvailableRoomsRequest(
    @SerializedName("start_time") val startTime: String,
    @SerializedName("end_time") val endTime: String,
    @SerializedName("building_id") val buildingId: Long? = null,
    @SerializedName("room_type_id") val roomTypeId: Long? = null,
    @SerializedName("min_capacity") val minCapacity: Int? = null,
    @SerializedName("amenities") val amenities: List<String> = emptyList()
)

data class ToggleFavoriteResponse(
    @SerializedName("room_id") val roomId: Long,
    @SerializedName("is_favorite") val isFavorite: Boolean
)

data class CreateBuildingRequest(
    @SerializedName("name") val name: String,
    @SerializedName("code") val code: String,
    @SerializedName("floors_count") val floorsCount: Int = 1,
    @SerializedName("description") val description: String? = null
)

data class CreateRoomTypeRequest(
    @SerializedName("name") val name: String,
    @SerializedName("code") val code: String,
    @SerializedName("description") val description: String? = null
)


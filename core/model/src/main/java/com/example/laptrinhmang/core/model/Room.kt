package com.example.laptrinhmang.core.model

import com.google.gson.annotations.SerializedName

enum class RoomStatus(val displayName: String) {
    @SerializedName("AVAILABLE")
    AVAILABLE("Sẵn sàng"),

    @SerializedName("OCCUPIED")
    OCCUPIED("Đang sử dụng"),

    @SerializedName("MAINTENANCE")
    MAINTENANCE("Bảo trì"),

    @SerializedName("LOCKED")
    LOCKED("Đang tạm khóa");

    companion object {
        fun fromString(value: String?): RoomStatus {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: AVAILABLE
        }
    }
}

data class Room(
    @SerializedName("id") val id: Long,
    @SerializedName("room_code") val roomCode: String,
    @SerializedName("room_name") val roomName: String,
    @SerializedName("building_id") val buildingId: Long? = null,
    @SerializedName("building_name") val buildingName: String = "",
    @SerializedName("floor") val floor: Int = 1,
    @SerializedName("capacity") val capacity: Int = 40,
    @SerializedName("room_type_id") val roomTypeId: Long? = null,
    @SerializedName("room_type_name") val roomTypeName: String = "Phòng Lý thuyết",
    @SerializedName("status") val status: RoomStatus = RoomStatus.AVAILABLE,
    @SerializedName("amenities") val amenities: List<String> = emptyList(),
    @SerializedName("description") val description: String? = null,
    @SerializedName("image_url") val imageUrl: String? = null,
    @SerializedName("is_favorite") val isFavorite: Boolean = false
)

data class RoomType(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("code") val code: String,
    @SerializedName("description") val description: String? = null
)

data class Building(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("code") val code: String,
    @SerializedName("floors_count") val floorsCount: Int = 1,
    @SerializedName("description") val description: String? = null
)


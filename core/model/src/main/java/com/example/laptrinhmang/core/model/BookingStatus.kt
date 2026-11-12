package com.example.laptrinhmang.core.model

import com.google.gson.annotations.SerializedName

enum class BookingStatus(val displayName: String) {
    @SerializedName("PENDING")
    PENDING("Chờ duyệt"),

    @SerializedName("APPROVED")
    APPROVED("Đã duyệt"),

    @SerializedName("REJECTED")
    REJECTED("Từ chối"),

    @SerializedName("CANCELLED")
    CANCELLED("Đã hủy"),

    @SerializedName("COMPLETED")
    COMPLETED("Đã hoàn thành");

    companion object {
        fun fromString(value: String?): BookingStatus {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: PENDING
        }
    }
}


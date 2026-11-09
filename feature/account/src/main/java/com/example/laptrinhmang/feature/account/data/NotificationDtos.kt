package com.example.laptrinhmang.feature.account.data

import com.google.gson.annotations.SerializedName

data class NotificationItem(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("message") val message: String,
    @SerializedName("is_read") val isRead: Boolean,
    @SerializedName("target_screen") val targetScreen: String? = null,
    @SerializedName("target_id") val targetId: String? = null,
    @SerializedName("created_at") val createdAt: String
)

data class FcmTokenRequest(
    @SerializedName("fcm_token") val fcmToken: String
)

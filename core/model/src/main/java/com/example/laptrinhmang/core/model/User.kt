package com.example.laptrinhmang.core.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id") val id: Long,
    @SerializedName("username") val username: String,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("role") val role: Role,
    @SerializedName("permissions") val permissions: List<String> = emptyList(),
    @SerializedName("department_id") val departmentId: Long? = null,
    @SerializedName("department_name") val departmentName: String? = null,
    @SerializedName("avatar_url") val avatarUrl: String? = null,
    @SerializedName("status") val status: UserStatus = UserStatus.ACTIVE
) {
    fun hasPermission(permissionCode: String): Boolean {
        if (role == Role.ADMIN) return true
        return permissions.any { it.equals(permissionCode, ignoreCase = true) }
    }
}

enum class UserStatus {
    @SerializedName("PENDING") PENDING,
    @SerializedName("ACTIVE") ACTIVE,
    @SerializedName("LOCKED") LOCKED
}

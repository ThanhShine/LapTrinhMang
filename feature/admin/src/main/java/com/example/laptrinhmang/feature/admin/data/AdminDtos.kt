package com.example.laptrinhmang.feature.admin.data

import com.example.laptrinhmang.core.model.UserStatus
import com.google.gson.annotations.SerializedName

data class CreateUserRequest(
    @SerializedName("email") val email: String,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("role") val role: String,
    @SerializedName("department_id") val departmentId: Long
)

data class CreateUserResponseData(
    @SerializedName("user_id") val userId: Long,
    @SerializedName("temporary_password") val temporaryPassword: String
)

data class UpdateUserRequest(
    @SerializedName("full_name") val fullName: String,
    @SerializedName("role") val role: String,
    @SerializedName("department_id") val departmentId: Long
)

data class UserStatusRequest(
    @SerializedName("status") val status: UserStatus
)

data class ResetUserPasswordRequest(
    @SerializedName("new_password") val newPassword: String? = null
)

data class ResetUserPasswordResponseData(
    @SerializedName("temporary_password") val temporaryPassword: String
)

data class ImportCsvRequest(
    @SerializedName("csv_content") val csvContent: String
)

data class ImportCsvResult(
    @SerializedName("total_rows") val totalRows: Int,
    @SerializedName("success_count") val successCount: Int,
    @SerializedName("failure_count") val failureCount: Int,
    @SerializedName("results") val results: List<CsvLineResult>
)

data class CsvLineResult(
    @SerializedName("line") val line: Int,
    @SerializedName("email") val email: String,
    @SerializedName("status") val status: String, // "SUCCESS" | "FAILED"
    @SerializedName("temporary_password") val temporaryPassword: String? = null,
    @SerializedName("error_message") val errorMessage: String? = null
)

data class RolePermissionItem(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("permissions") val permissions: List<String>
)

data class UpdateRolePermissionsRequest(
    @SerializedName("permissions") val permissions: List<String>
)

data class DepartmentItem(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("parent_id") val parentId: Long? = null,
    @SerializedName("status") val status: String = "ACTIVE",
    @SerializedName("children") val children: List<DepartmentItem> = emptyList()
)

data class CreateDepartmentRequest(
    @SerializedName("name") val name: String,
    @SerializedName("parent_id") val parentId: Long?
)

data class AuditLogItem(
    @SerializedName("id") val id: Long,
    @SerializedName("user_id") val userId: Long,
    @SerializedName("user_name") val userName: String,
    @SerializedName("action") val action: String,
    @SerializedName("description") val description: String,
    @SerializedName("ip_address") val ipAddress: String,
    @SerializedName("created_at") val createdAt: String
)

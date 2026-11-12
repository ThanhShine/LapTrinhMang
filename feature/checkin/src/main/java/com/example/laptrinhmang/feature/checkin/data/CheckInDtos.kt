package com.example.laptrinhmang.feature.checkin.data

import com.google.gson.annotations.SerializedName

data class CheckInRequest(
    @SerializedName("qr_code") val qrCode: String,
    @SerializedName("booking_id") val bookingId: Long? = null,
    @SerializedName("device_info") val deviceInfo: String? = "Android App"
)

data class CheckInResponseDto(
    @SerializedName("booking_id") val bookingId: Long,
    @SerializedName("room_id") val roomId: Long,
    @SerializedName("room_code") val roomCode: String,
    @SerializedName("room_name") val roomName: String,
    @SerializedName("user_id") val userId: Long,
    @SerializedName("user_name") val userName: String,
    @SerializedName("check_in_time") val checkInTime: String,
    @SerializedName("expected_end_time") val expectedEndTime: String,
    @SerializedName("status") val status: String
)

data class CheckOutRequest(
    @SerializedName("booking_id") val bookingId: Long,
    @SerializedName("note") val note: String? = null
)

data class CheckOutResponseDto(
    @SerializedName("booking_id") val bookingId: Long,
    @SerializedName("room_code") val roomCode: String,
    @SerializedName("check_out_time") val checkOutTime: String,
    @SerializedName("duration_minutes") val durationMinutes: Int,
    @SerializedName("status") val status: String
)

data class CreateIssueRequest(
    @SerializedName("room_id") val roomId: Long? = null,
    @SerializedName("room_code") val roomCode: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("severity") val severity: String,
    @SerializedName("images") val images: List<String>? = emptyList()
)

data class RoomIssueDto(
    @SerializedName("id") val id: Long,
    @SerializedName("code") val code: String,
    @SerializedName("room_code") val roomCode: String,
    @SerializedName("room_name") val roomName: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("severity") val severity: String,
    @SerializedName("status") val status: String,
    @SerializedName("reporter_name") val reporterName: String,
    @SerializedName("assigned_to") val assignedTo: String? = null,
    @SerializedName("resolution_note") val resolutionNote: String? = null,
    @SerializedName("images") val images: List<String>? = emptyList(),
    @SerializedName("created_at") val createdAt: String? = ""
)

data class AssignIssueRequest(
    @SerializedName("technician_id") val technicianId: Long,
    @SerializedName("technician_name") val technicianName: String,
    @SerializedName("priority") val priority: String? = "HIGH",
    @SerializedName("note") val note: String? = null
)

data class ResolveIssueRequest(
    @SerializedName("resolution_note") val resolutionNote: String,
    @SerializedName("is_accepted") val isAccepted: Boolean = true,
    @SerializedName("accepted_by") val acceptedBy: String = "Quản lý phòng"
)

data class CreateRatingRequest(
    @SerializedName("booking_id") val bookingId: Long,
    @SerializedName("room_id") val roomId: Long? = null,
    @SerializedName("cleanliness_score") val cleanlinessScore: Int,
    @SerializedName("equipment_score") val equipmentScore: Int,
    @SerializedName("lighting_score") val lightingScore: Int,
    @SerializedName("overall_score") val overallScore: Int,
    @SerializedName("comment") val comment: String
)

data class RoomEquipmentDto(
    @SerializedName("id") val id: Long,
    @SerializedName("code") val code: String,
    @SerializedName("name") val name: String,
    @SerializedName("category") val category: String,
    @SerializedName("room_code") val roomCode: String,
    @SerializedName("room_name") val roomName: String,
    @SerializedName("status") val status: String,
    @SerializedName("serial_number") val serialNumber: String? = "",
    @SerializedName("installed_date") val installedDate: String? = ""
)

data class DepartmentUsageDto(
    @SerializedName("department_name") val departmentName: String,
    @SerializedName("usage_count") val usageCount: Int,
    @SerializedName("percentage") val percentage: Float
)

data class UsageStatisticsDto(
    @SerializedName("total_bookings") val totalBookings: Int,
    @SerializedName("total_hours") val totalHours: Int,
    @SerializedName("average_occupancy_rate") val averageOccupancyRate: Float,
    @SerializedName("top_departments") val topDepartments: List<DepartmentUsageDto>
)

data class IssueStatisticsDto(
    @SerializedName("total_issues") val totalIssues: Int,
    @SerializedName("open_issues") val openIssues: Int,
    @SerializedName("resolved_issues") val resolvedIssues: Int,
    @SerializedName("average_resolution_hours") val averageResolutionHours: Float,
    @SerializedName("category_breakdown") val categoryBreakdown: Map<String, Int>
)

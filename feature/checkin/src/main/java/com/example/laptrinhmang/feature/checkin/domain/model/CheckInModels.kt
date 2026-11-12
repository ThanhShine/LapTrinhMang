package com.example.laptrinhmang.feature.checkin.domain.model

enum class IssueSeverity(val displayName: String) {
    LOW("Thấp"),
    MEDIUM("Trung bình"),
    HIGH("Cao"),
    CRITICAL("Nghiêm trọng")
}

enum class IssueStatus(val displayName: String) {
    OPEN("Chờ tiếp nhận"),
    ASSIGNED("Đang xử lý"),
    RESOLVED("Đã hoàn thành"),
    CLOSED("Đã đóng")
}

data class CheckInResult(
    val bookingId: Long,
    val roomCode: String,
    val roomName: String,
    val checkInTime: String,
    val expectedEndTime: String,
    val status: String
)

data class CheckOutResult(
    val bookingId: Long,
    val roomCode: String,
    val checkOutTime: String,
    val durationMinutes: Int,
    val status: String
)

data class RoomIssue(
    val id: Long,
    val code: String,
    val roomCode: String,
    val roomName: String,
    val title: String,
    val description: String,
    val severity: IssueSeverity,
    val status: IssueStatus,
    val reporterName: String,
    val assignedTo: String? = null,
    val resolutionNote: String? = null,
    val images: List<String> = emptyList(),
    val createdAt: String = ""
)

enum class EquipmentStatus(val displayName: String) {
    ACTIVE("Hoạt động tốt"),
    MAINTENANCE("Bảo trì"),
    BROKEN("Bị hỏng")
}

data class RoomEquipment(
    val id: Long,
    val code: String,
    val name: String,
    val category: String,
    val roomCode: String,
    val roomName: String,
    val status: EquipmentStatus,
    val serialNumber: String = "",
    val installedDate: String = ""
)

data class RoomRating(
    val bookingId: Long,
    val roomCode: String,
    val cleanlinessScore: Int,
    val equipmentScore: Int,
    val lightingScore: Int,
    val overallScore: Int,
    val comment: String
)

data class DepartmentUsage(
    val departmentName: String,
    val usageCount: Int,
    val percentage: Float
)

data class UsageStatistics(
    val totalBookings: Int,
    val totalHours: Int,
    val averageOccupancyRate: Float,
    val topDepartments: List<DepartmentUsage> = emptyList()
)

data class IssueStatistics(
    val totalIssues: Int,
    val openIssues: Int,
    val resolvedIssues: Int,
    val averageResolutionHours: Float,
    val categoryBreakdown: Map<String, Int> = emptyMap()
)

package com.example.laptrinhmang.feature.checkin.data

import com.example.laptrinhmang.feature.checkin.data.AssignIssueRequest
import com.example.laptrinhmang.feature.checkin.data.CheckInRequest
import com.example.laptrinhmang.feature.checkin.data.CheckInResponseDto
import com.example.laptrinhmang.feature.checkin.data.CheckOutRequest
import com.example.laptrinhmang.feature.checkin.data.CheckOutResponseDto
import com.example.laptrinhmang.feature.checkin.data.CreateIssueRequest
import com.example.laptrinhmang.feature.checkin.data.CreateRatingRequest
import com.example.laptrinhmang.feature.checkin.data.DepartmentUsageDto
import com.example.laptrinhmang.feature.checkin.data.IssueStatisticsDto
import com.example.laptrinhmang.feature.checkin.data.ResolveIssueRequest
import com.example.laptrinhmang.feature.checkin.data.RoomEquipmentDto
import com.example.laptrinhmang.feature.checkin.data.RoomIssueDto
import com.example.laptrinhmang.feature.checkin.data.UsageStatisticsDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CheckInMockDataSource @Inject constructor() {

    private val issuesList = mutableListOf(
        RoomIssueDto(
            id = 501L,
            code = "ISS-2026-001",
            roomCode = "P.302",
            roomName = "Phòng Lý thuyết 302 - Nhà A",
            title = "Máy chiếu Panasonic không lên nguồn",
            description = "Bật công tắc nguồn đèn đỏ nhấp nháy liên tục nhưng bóng chiếu không sáng.",
            severity = "HIGH",
            status = "OPEN",
            reporterName = "Trần Thị Giáo",
            assignedTo = null,
            images = listOf("https://dau.edu.vn/uploads/issues/img_projector_error.jpg"),
            createdAt = "2026-11-20T08:15:00+07:00"
        ),
        RoomIssueDto(
            id = 502L,
            code = "ISS-2026-002",
            roomCode = "LAB-501",
            roomName = "Phòng Thực hành Mạng 501 - Nhà C",
            title = "Điều hòa Daikin chảy nước xuống sàn",
            description = "Dàn lạnh máy số 2 chảy nước thành dòng, gây trơn trượt khu vực máy PC học viên.",
            severity = "MEDIUM",
            status = "ASSIGNED",
            reporterName = "Nguyễn Văn Học",
            assignedTo = "Võ Kỹ Thuật (Tổ Điện Lạnh)",
            images = listOf("https://dau.edu.vn/uploads/issues/img_ac_leak.jpg"),
            createdAt = "2026-11-20T09:30:00+07:00"
        ),
        RoomIssueDto(
            id = 503L,
            code = "ISS-2026-003",
            roomCode = "XUONG-204",
            roomName = "Xưởng Đồ án Kiến trúc 204 - Nhà B",
            title = "Micro không dây phát tiếng rít chói tai",
            description = "Micro pin yếu hoặc trùng tần số sóng với phòng bên cạnh.",
            severity = "LOW",
            status = "RESOLVED",
            reporterName = "Lê Hoàng Nam",
            assignedTo = "Nguyễn Kỹ Thuật",
            resolutionNote = "Đã thay pin sạc mới và đổi kênh tần số sóng sang CH-04. Thiết bị hoạt động ổn định.",
            createdAt = "2026-11-19T14:00:00+07:00"
        ),
        RoomIssueDto(
            id = 504L,
            code = "ISS-2026-004",
            roomCode = "LAB-501",
            roomName = "Phòng Thực hành Mạng 501 - Nhà C",
            title = "Switch Cisco mất nguồn 12 cổng mạng phía Tây",
            description = "Sinh viên không kết nối được vào mạng nội bộ để thi thực hành.",
            severity = "CRITICAL",
            status = "ASSIGNED",
            reporterName = "Trần Thị Giáo",
            assignedTo = "Đội Quản trị Mạng DAU",
            createdAt = "2026-11-20T10:00:00+07:00"
        )
    )

    private val equipmentList = mutableListOf(
        RoomEquipmentDto(
            id = 101L,
            code = "EQ-PRJ-302",
            name = "Máy chiếu Panasonic Laser PT-VMZ51",
            category = "Máy chiếu",
            roomCode = "P.302",
            roomName = "Phòng Lý thuyết 302",
            status = "MAINTENANCE",
            serialNumber = "PAN-2024-8899",
            installedDate = "2024-08-15"
        ),
        RoomEquipmentDto(
            id = 102L,
            code = "EQ-AC-302",
            name = "Điều hòa Daikin Inverter 2.5HP",
            category = "Điều hòa",
            roomCode = "P.302",
            roomName = "Phòng Lý thuyết 302",
            status = "ACTIVE",
            serialNumber = "DK-INV-7721",
            installedDate = "2023-05-10"
        ),
        RoomEquipmentDto(
            id = 103L,
            code = "EQ-SW-501",
            name = "Switch Cisco Catalyst 2960-X 48 Port",
            category = "Thiết bị mạng",
            roomCode = "LAB-501",
            roomName = "Phòng Thực hành Mạng 501",
            status = "BROKEN",
            serialNumber = "CSCO-2960X-994",
            installedDate = "2023-11-01"
        ),
        RoomEquipmentDto(
            id = 104L,
            code = "EQ-MIC-204",
            name = "Bộ Micro không dây Shure SVX288",
            category = "Âm thanh",
            roomCode = "XUONG-204",
            roomName = "Xưởng Đồ án Kiến trúc 204",
            status = "ACTIVE",
            serialNumber = "SHR-SVX-441",
            installedDate = "2024-01-20"
        ),
        RoomEquipmentDto(
            id = 105L,
            code = "EQ-LED-HT",
            name = "Màn hình LED P3 sân khấu lớn",
            category = "Màn hình LED",
            roomCode = "HT-LON",
            roomName = "Hội trường lớn",
            status = "ACTIVE",
            serialNumber = "LED-P3-INDOOR-01",
            installedDate = "2022-09-02"
        )
    )

    fun performCheckIn(request: CheckInRequest): CheckInResponseDto {
        val code = request.qrCode.uppercase()
        val roomCode = when {
            code.contains("302") -> "P.302"
            code.contains("501") -> "LAB-501"
            code.contains("204") -> "XUONG-204"
            code.contains("HT") -> "HT-LON"
            else -> if (request.qrCode.isNotBlank()) request.qrCode else "P.302"
        }
        val roomName = when (roomCode) {
            "P.302" -> "Phòng Lý thuyết 302 - Nhà A"
            "LAB-501" -> "Phòng Thực hành Mạng 501 - Nhà C"
            "XUONG-204" -> "Xưởng Đồ án Kiến trúc 204 - Nhà B"
            "HT-LON" -> "Hội trường lớn Tầng 2"
            else -> "Phòng học $roomCode"
        }

        return CheckInResponseDto(
            bookingId = request.bookingId ?: 1L,
            roomId = 302L,
            roomCode = roomCode,
            roomName = roomName,
            userId = 4L,
            userName = "Nguyễn Văn Học",
            checkInTime = "2026-11-20T07:45:10+07:00",
            expectedEndTime = "2026-11-20T11:30:00+07:00",
            status = "CHECKED_IN"
        )
    }

    fun performCheckOut(request: CheckOutRequest): CheckOutResponseDto {
        return CheckOutResponseDto(
            bookingId = request.bookingId,
            roomCode = "P.302",
            checkOutTime = "2026-11-20T11:25:00+07:00",
            durationMinutes = 220,
            status = "COMPLETED"
        )
    }

    fun createIssue(request: CreateIssueRequest): RoomIssueDto {
        val newId = (issuesList.maxOfOrNull { it.id } ?: 500L) + 1L
        val issue = RoomIssueDto(
            id = newId,
            code = "ISS-2026-${newId.toString().takeLast(3).padStart(3, '0')}",
            roomCode = request.roomCode,
            roomName = "Phòng học ${request.roomCode}",
            title = request.title,
            description = request.description,
            severity = request.severity.uppercase(),
            status = "OPEN",
            reporterName = "Người báo cáo",
            assignedTo = null,
            images = request.images ?: emptyList(),
            createdAt = "2026-11-20T11:30:00+07:00"
        )
        issuesList.add(0, issue)
        return issue
    }

    fun getIssues(status: String?, severity: String?, search: String?): List<RoomIssueDto> {
        return issuesList.filter { issue ->
            val matchStatus = status.isNullOrBlank() || issue.status.equals(status, ignoreCase = true)
            val matchSeverity = severity.isNullOrBlank() || issue.severity.equals(severity, ignoreCase = true)
            val matchSearch = search.isNullOrBlank() ||
                    issue.title.contains(search, ignoreCase = true) ||
                    issue.roomCode.contains(search, ignoreCase = true) ||
                    issue.code.contains(search, ignoreCase = true)
            matchStatus && matchSeverity && matchSearch
        }
    }

    fun assignIssue(id: Long, request: AssignIssueRequest): RoomIssueDto {
        val index = issuesList.indexOfFirst { it.id == id }
        if (index == -1) throw IllegalArgumentException("Không tìm thấy sự cố #$id")
        val current = issuesList[index]
        val updated = current.copy(
            status = "ASSIGNED",
            assignedTo = request.technicianName
        )
        issuesList[index] = updated
        return updated
    }

    fun resolveIssue(id: Long, request: ResolveIssueRequest): RoomIssueDto {
        val index = issuesList.indexOfFirst { it.id == id }
        if (index == -1) throw IllegalArgumentException("Không tìm thấy sự cố #$id")
        val current = issuesList[index]
        val updated = current.copy(
            status = "RESOLVED",
            resolutionNote = request.resolutionNote
        )
        issuesList[index] = updated
        return updated
    }

    fun submitRating(request: CreateRatingRequest) {
        // Lưu rating thành công
    }

    fun getEquipment(roomCode: String?, status: String?): List<RoomEquipmentDto> {
        return equipmentList.filter { eq ->
            val matchRoom = roomCode.isNullOrBlank() || eq.roomCode.contains(roomCode, ignoreCase = true)
            val matchStatus = status.isNullOrBlank() || eq.status.equals(status, ignoreCase = true)
            matchRoom && matchStatus
        }
    }

    fun createEquipment(dto: RoomEquipmentDto): RoomEquipmentDto {
        val newId = (equipmentList.maxOfOrNull { it.id } ?: 100L) + 1L
        val created = dto.copy(id = newId, code = "EQ-${newId}")
        equipmentList.add(created)
        return created
    }

    fun updateEquipment(id: Long, dto: RoomEquipmentDto): RoomEquipmentDto {
        val index = equipmentList.indexOfFirst { it.id == id }
        if (index != -1) {
            equipmentList[index] = dto.copy(id = id)
        }
        return dto
    }

    fun deleteEquipment(id: Long) {
        equipmentList.removeAll { it.id == id }
    }

    fun getUsageStatistics(): UsageStatisticsDto {
        return UsageStatisticsDto(
            totalBookings = 142,
            totalHours = 486,
            averageOccupancyRate = 78.5f,
            topDepartments = listOf(
                DepartmentUsageDto("Khoa Công nghệ thông tin", 64, 45.0f),
                DepartmentUsageDto("Khoa Kiến trúc", 49, 34.5f),
                DepartmentUsageDto("Phòng Đào tạo", 29, 20.5f)
            )
        )
    }

    fun getIssueStatistics(): IssueStatisticsDto {
        return IssueStatisticsDto(
            totalIssues = 28,
            openIssues = 5,
            resolvedIssues = 23,
            averageResolutionHours = 2.4f,
            categoryBreakdown = mapOf(
                "Máy chiếu" to 12,
                "Điều hòa" to 7,
                "Thiết bị mạng" to 5,
                "Âm thanh & Khác" to 4
            )
        )
    }
}

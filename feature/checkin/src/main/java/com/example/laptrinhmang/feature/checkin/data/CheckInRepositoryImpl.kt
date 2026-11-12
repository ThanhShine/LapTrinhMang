package com.example.laptrinhmang.feature.checkin.data

import com.example.laptrinhmang.core.model.ApiError
import com.example.laptrinhmang.core.model.NetworkResult
import com.example.laptrinhmang.feature.checkin.domain.model.CheckInResult
import com.example.laptrinhmang.feature.checkin.domain.model.CheckOutResult
import com.example.laptrinhmang.feature.checkin.domain.model.DepartmentUsage
import com.example.laptrinhmang.feature.checkin.domain.model.EquipmentStatus
import com.example.laptrinhmang.feature.checkin.domain.model.IssueSeverity
import com.example.laptrinhmang.feature.checkin.domain.model.IssueStatistics
import com.example.laptrinhmang.feature.checkin.domain.model.IssueStatus
import com.example.laptrinhmang.feature.checkin.domain.model.RoomEquipment
import com.example.laptrinhmang.feature.checkin.domain.model.RoomIssue
import com.example.laptrinhmang.feature.checkin.domain.model.RoomRating
import com.example.laptrinhmang.feature.checkin.domain.model.UsageStatistics
import com.example.laptrinhmang.feature.checkin.domain.repository.CheckInRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CheckInRepositoryImpl @Inject constructor(
    private val api: CheckInApi,
    private val mockDataSource: CheckInMockDataSource
) : CheckInRepository {

    private fun errorResult(msg: String, statusCode: Int = 400) =
        NetworkResult.Error(ApiError(statusCode = statusCode, message = msg))

    override suspend fun checkIn(qrCode: String, bookingId: Long?): NetworkResult<CheckInResult> {
        return try {
            val response = mockDataSource.performCheckIn(CheckInRequest(qrCode = qrCode, bookingId = bookingId))
            NetworkResult.Success(
                CheckInResult(
                    bookingId = response.bookingId,
                    roomCode = response.roomCode,
                    roomName = response.roomName,
                    checkInTime = response.checkInTime,
                    expectedEndTime = response.expectedEndTime,
                    status = response.status
                )
            )
        } catch (e: Exception) {
            errorResult(e.message ?: "Lỗi thực hiện check-in")
        }
    }

    override suspend fun checkOut(bookingId: Long, note: String?): NetworkResult<CheckOutResult> {
        return try {
            val response = mockDataSource.performCheckOut(CheckOutRequest(bookingId = bookingId, note = note))
            NetworkResult.Success(
                CheckOutResult(
                    bookingId = response.bookingId,
                    roomCode = response.roomCode,
                    checkOutTime = response.checkOutTime,
                    durationMinutes = response.durationMinutes,
                    status = response.status
                )
            )
        } catch (e: Exception) {
            errorResult(e.message ?: "Lỗi thực hiện trả phòng")
        }
    }

    override suspend fun createIssue(
        roomCode: String,
        title: String,
        description: String,
        severity: IssueSeverity,
        images: List<String>
    ): NetworkResult<RoomIssue> {
        return try {
            val request = CreateIssueRequest(
                roomCode = roomCode,
                title = title,
                description = description,
                severity = severity.name,
                images = images
            )
            val created = mockDataSource.createIssue(request)
            NetworkResult.Success(created.toDomain())
        } catch (e: Exception) {
            errorResult(e.message ?: "Lỗi gửi báo cáo sự cố")
        }
    }

    override fun getIssues(status: String?, severity: String?, search: String?): Flow<NetworkResult<List<RoomIssue>>> = flow {
        emit(NetworkResult.Loading)
        try {
            val list = mockDataSource.getIssues(status, severity, search).map { it.toDomain() }
            emit(NetworkResult.Success(list))
        } catch (e: Exception) {
            emit(errorResult(e.message ?: "Lỗi tải danh sách sự cố"))
        }
    }

    override suspend fun assignIssue(
        issueId: Long,
        technicianId: Long,
        technicianName: String,
        note: String?
    ): NetworkResult<RoomIssue> {
        return try {
            val request = AssignIssueRequest(technicianId = technicianId, technicianName = technicianName, note = note)
            val updated = mockDataSource.assignIssue(issueId, request)
            NetworkResult.Success(updated.toDomain())
        } catch (e: Exception) {
            errorResult(e.message ?: "Lỗi phân công xử lý sự cố")
        }
    }

    override suspend fun resolveIssue(
        issueId: Long,
        resolutionNote: String,
        acceptedBy: String
    ): NetworkResult<RoomIssue> {
        return try {
            val request = ResolveIssueRequest(resolutionNote = resolutionNote, acceptedBy = acceptedBy)
            val updated = mockDataSource.resolveIssue(issueId, request)
            NetworkResult.Success(updated.toDomain())
        } catch (e: Exception) {
            errorResult(e.message ?: "Lỗi nghiệm thu sự cố")
        }
    }

    override suspend fun submitRating(rating: RoomRating): NetworkResult<Unit> {
        return try {
            val request = CreateRatingRequest(
                bookingId = rating.bookingId,
                cleanlinessScore = rating.cleanlinessScore,
                equipmentScore = rating.equipmentScore,
                lightingScore = rating.lightingScore,
                overallScore = rating.overallScore,
                comment = rating.comment
            )
            mockDataSource.submitRating(request)
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            errorResult(e.message ?: "Lỗi gửi đánh giá phòng")
        }
    }

    override fun getEquipment(roomCode: String?, status: String?): Flow<NetworkResult<List<RoomEquipment>>> = flow {
        emit(NetworkResult.Loading)
        try {
            val list = mockDataSource.getEquipment(roomCode, status).map { it.toDomain() }
            emit(NetworkResult.Success(list))
        } catch (e: Exception) {
            emit(errorResult(e.message ?: "Lỗi tải danh sách thiết bị"))
        }
    }

    override suspend fun createEquipment(equipment: RoomEquipment): NetworkResult<RoomEquipment> {
        return try {
            val dto = RoomEquipmentDto(
                id = equipment.id,
                code = equipment.code,
                name = equipment.name,
                category = equipment.category,
                roomCode = equipment.roomCode,
                roomName = equipment.roomName,
                status = equipment.status.name,
                serialNumber = equipment.serialNumber,
                installedDate = equipment.installedDate
            )
            val created = mockDataSource.createEquipment(dto)
            NetworkResult.Success(created.toDomain())
        } catch (e: Exception) {
            errorResult(e.message ?: "Lỗi tạo thiết bị")
        }
    }

    override suspend fun updateEquipment(equipment: RoomEquipment): NetworkResult<RoomEquipment> {
        return try {
            val dto = RoomEquipmentDto(
                id = equipment.id,
                code = equipment.code,
                name = equipment.name,
                category = equipment.category,
                roomCode = equipment.roomCode,
                roomName = equipment.roomName,
                status = equipment.status.name,
                serialNumber = equipment.serialNumber,
                installedDate = equipment.installedDate
            )
            val updated = mockDataSource.updateEquipment(equipment.id, dto)
            NetworkResult.Success(updated.toDomain())
        } catch (e: Exception) {
            errorResult(e.message ?: "Lỗi cập nhật thiết bị")
        }
    }

    override suspend fun deleteEquipment(id: Long): NetworkResult<Unit> {
        return try {
            mockDataSource.deleteEquipment(id)
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            errorResult(e.message ?: "Lỗi xóa thiết bị")
        }
    }

    override fun getUsageStatistics(): Flow<NetworkResult<UsageStatistics>> = flow {
        emit(NetworkResult.Loading)
        try {
            val dto = mockDataSource.getUsageStatistics()
            val domain = UsageStatistics(
                totalBookings = dto.totalBookings,
                totalHours = dto.totalHours,
                averageOccupancyRate = dto.averageOccupancyRate,
                topDepartments = dto.topDepartments.map {
                    DepartmentUsage(it.departmentName, it.usageCount, it.percentage)
                }
            )
            emit(NetworkResult.Success(domain))
        } catch (e: Exception) {
            emit(errorResult(e.message ?: "Lỗi tải thống kê sử dụng"))
        }
    }

    override fun getIssueStatistics(): Flow<NetworkResult<IssueStatistics>> = flow {
        emit(NetworkResult.Loading)
        try {
            val dto = mockDataSource.getIssueStatistics()
            val domain = IssueStatistics(
                totalIssues = dto.totalIssues,
                openIssues = dto.openIssues,
                resolvedIssues = dto.resolvedIssues,
                averageResolutionHours = dto.averageResolutionHours,
                categoryBreakdown = dto.categoryBreakdown
            )
            emit(NetworkResult.Success(domain))
        } catch (e: Exception) {
            emit(errorResult(e.message ?: "Lỗi tải thống kê sự cố"))
        }
    }

    private fun RoomIssueDto.toDomain() = RoomIssue(
        id = id,
        code = code,
        roomCode = roomCode,
        roomName = roomName,
        title = title,
        description = description,
        severity = when (severity.uppercase()) {
            "CRITICAL" -> IssueSeverity.CRITICAL
            "HIGH" -> IssueSeverity.HIGH
            "MEDIUM" -> IssueSeverity.MEDIUM
            else -> IssueSeverity.LOW
        },
        status = when (status.uppercase()) {
            "ASSIGNED" -> IssueStatus.ASSIGNED
            "RESOLVED" -> IssueStatus.RESOLVED
            "CLOSED" -> IssueStatus.CLOSED
            else -> IssueStatus.OPEN
        },
        reporterName = reporterName,
        assignedTo = assignedTo,
        resolutionNote = resolutionNote,
        images = images ?: emptyList(),
        createdAt = createdAt ?: ""
    )

    private fun RoomEquipmentDto.toDomain() = RoomEquipment(
        id = id,
        code = code,
        name = name,
        category = category,
        roomCode = roomCode,
        roomName = roomName,
        status = when (status.uppercase()) {
            "MAINTENANCE" -> EquipmentStatus.MAINTENANCE
            "BROKEN" -> EquipmentStatus.BROKEN
            else -> EquipmentStatus.ACTIVE
        },
        serialNumber = serialNumber ?: "",
        installedDate = installedDate ?: ""
    )
}

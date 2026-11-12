package com.example.laptrinhmang.feature.checkin.domain.usecase

import com.example.laptrinhmang.core.model.ApiError
import com.example.laptrinhmang.core.model.NetworkResult
import com.example.laptrinhmang.feature.checkin.domain.model.CheckInResult
import com.example.laptrinhmang.feature.checkin.domain.model.CheckOutResult
import com.example.laptrinhmang.feature.checkin.domain.model.IssueSeverity
import com.example.laptrinhmang.feature.checkin.domain.model.IssueStatistics
import com.example.laptrinhmang.feature.checkin.domain.model.RoomEquipment
import com.example.laptrinhmang.feature.checkin.domain.model.RoomIssue
import com.example.laptrinhmang.feature.checkin.domain.model.RoomRating
import com.example.laptrinhmang.feature.checkin.domain.model.UsageStatistics
import com.example.laptrinhmang.feature.checkin.domain.repository.CheckInRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

private fun errorResult(msg: String, statusCode: Int = 400) =
    NetworkResult.Error(ApiError(statusCode = statusCode, message = msg))

class CheckInUseCase @Inject constructor(
    private val repository: CheckInRepository
) {
    suspend operator fun invoke(qrCode: String, bookingId: Long? = null): NetworkResult<CheckInResult> {
        if (qrCode.isBlank() && bookingId == null) {
            return errorResult("Vui lòng quét mã QR hoặc nhập mã phòng!")
        }
        return repository.checkIn(qrCode.trim(), bookingId)
    }
}

class CheckOutUseCase @Inject constructor(
    private val repository: CheckInRepository
) {
    suspend operator fun invoke(bookingId: Long, note: String? = null): NetworkResult<CheckOutResult> {
        return repository.checkOut(bookingId, note?.trim())
    }
}

class ReportIssueUseCase @Inject constructor(
    private val repository: CheckInRepository
) {
    suspend operator fun invoke(
        roomCode: String,
        title: String,
        description: String,
        severity: IssueSeverity,
        images: List<String> = emptyList()
    ): NetworkResult<RoomIssue> {
        if (roomCode.isBlank()) return errorResult("Vui lòng chọn phòng học xảy ra sự cố!")
        if (title.isBlank()) return errorResult("Tiêu đề sự cố không được để trống!")
        if (description.isBlank()) return errorResult("Vui lòng mô tả chi tiết sự cố!")
        return repository.createIssue(roomCode.trim(), title.trim(), description.trim(), severity, images)
    }
}

class GetIssuesUseCase @Inject constructor(
    private val repository: CheckInRepository
) {
    operator fun invoke(
        status: String? = null,
        severity: String? = null,
        search: String? = null
    ): Flow<NetworkResult<List<RoomIssue>>> {
        return repository.getIssues(status, severity, search)
    }
}

class AssignIssueUseCase @Inject constructor(
    private val repository: CheckInRepository
) {
    suspend operator fun invoke(
        issueId: Long,
        technicianId: Long,
        technicianName: String,
        note: String? = null
    ): NetworkResult<RoomIssue> {
        if (technicianName.isBlank()) return errorResult("Vui lòng chọn kỹ thuật viên tiếp nhận!")
        return repository.assignIssue(issueId, technicianId, technicianName.trim(), note?.trim())
    }
}

class ResolveIssueUseCase @Inject constructor(
    private val repository: CheckInRepository
) {
    suspend operator fun invoke(
        issueId: Long,
        resolutionNote: String,
        acceptedBy: String
    ): NetworkResult<RoomIssue> {
        if (resolutionNote.isBlank()) return errorResult("Vui lòng nhập ghi chú nghiệm thu sau sửa chữa!")
        return repository.resolveIssue(issueId, resolutionNote.trim(), acceptedBy)
    }
}

class SubmitRatingUseCase @Inject constructor(
    private val repository: CheckInRepository
) {
    suspend operator fun invoke(rating: RoomRating): NetworkResult<Unit> {
        return repository.submitRating(rating)
    }
}

class EquipmentUseCases @Inject constructor(
    private val repository: CheckInRepository
) {
    fun getEquipment(roomCode: String? = null, status: String? = null): Flow<NetworkResult<List<RoomEquipment>>> {
        return repository.getEquipment(roomCode, status)
    }

    suspend fun createEquipment(equipment: RoomEquipment): NetworkResult<RoomEquipment> {
        if (equipment.name.isBlank()) return errorResult("Tên thiết bị không được để trống!")
        if (equipment.roomCode.isBlank()) return errorResult("Phòng lắp đặt không được để trống!")
        return repository.createEquipment(equipment)
    }

    suspend fun updateEquipment(equipment: RoomEquipment): NetworkResult<RoomEquipment> {
        if (equipment.name.isBlank()) return errorResult("Tên thiết bị không được để trống!")
        return repository.updateEquipment(equipment)
    }

    suspend fun deleteEquipment(id: Long): NetworkResult<Unit> {
        return repository.deleteEquipment(id)
    }
}

class StatisticsUseCases @Inject constructor(
    private val repository: CheckInRepository
) {
    fun getUsageStatistics(): Flow<NetworkResult<UsageStatistics>> = repository.getUsageStatistics()

    fun getIssueStatistics(): Flow<NetworkResult<IssueStatistics>> = repository.getIssueStatistics()
}

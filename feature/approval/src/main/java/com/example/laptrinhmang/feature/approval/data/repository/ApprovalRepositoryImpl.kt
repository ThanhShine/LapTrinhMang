package com.example.laptrinhmang.feature.approval.data.repository

import com.example.laptrinhmang.core.model.ApiError
import com.example.laptrinhmang.core.model.NetworkResult
import com.example.laptrinhmang.feature.approval.data.model.AdjustScheduleRequest
import com.example.laptrinhmang.feature.approval.data.model.ApprovalDecisionRequest
import com.example.laptrinhmang.feature.approval.data.model.ApprovalItemDto
import com.example.laptrinhmang.feature.approval.data.model.ApprovalRuleDto
import com.example.laptrinhmang.feature.approval.data.model.BatchApprovalRequest
import com.example.laptrinhmang.feature.approval.data.model.LockScheduleRequest
import com.example.laptrinhmang.feature.approval.data.model.RoomCategoryDto
import com.example.laptrinhmang.feature.approval.data.remote.ApprovalApiService
import com.example.laptrinhmang.feature.approval.data.remote.MockApprovalDataSource
import com.example.laptrinhmang.feature.approval.domain.model.AffectedBooking
import com.example.laptrinhmang.feature.approval.domain.model.ApprovalItem
import com.example.laptrinhmang.feature.approval.domain.model.ApprovalPriority
import com.example.laptrinhmang.feature.approval.domain.model.ApprovalRule
import com.example.laptrinhmang.feature.approval.domain.model.ApprovalStatus
import com.example.laptrinhmang.feature.approval.domain.model.BatchApprovalResult
import com.example.laptrinhmang.feature.approval.domain.model.BatchApprovalSingleResult
import com.example.laptrinhmang.feature.approval.domain.model.CampusSchedule
import com.example.laptrinhmang.feature.approval.domain.model.LockedSlotResult
import com.example.laptrinhmang.feature.approval.domain.model.RoomCategory
import com.example.laptrinhmang.feature.approval.domain.model.RoomSchedule
import com.example.laptrinhmang.feature.approval.domain.model.RoomTimeSlot
import com.example.laptrinhmang.feature.approval.domain.repository.ApprovalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApprovalRepositoryImpl @Inject constructor(
    private val apiService: ApprovalApiService,
    private val mockDataSource: MockApprovalDataSource
) : ApprovalRepository {

    private fun errorResult(msg: String, statusCode: Int = 400) =
        NetworkResult.Error(ApiError(statusCode = statusCode, message = msg))

    override fun getPendingApprovals(search: String?): Flow<NetworkResult<List<ApprovalItem>>> = flow {
        emit(NetworkResult.Loading)
        try {
            val dtoList = mockDataSource.getPendingApprovals(search)
            val domainList = dtoList.map { it.toDomain() }
            emit(NetworkResult.Success(domainList))
        } catch (e: Exception) {
            emit(errorResult(e.message ?: "Lỗi tải danh sách phê duyệt"))
        }
    }

    override suspend fun decideApproval(
        id: String,
        isApproved: Boolean,
        reason: String?,
        note: String?
    ): NetworkResult<ApprovalItem> {
        return try {
            val action = if (isApproved) "APPROVE" else "REJECT"
            val request = ApprovalDecisionRequest(action = action, reason = reason, note = note)
            val updatedDto = mockDataSource.decideApproval(id, request)
            NetworkResult.Success(updatedDto.toDomain())
        } catch (e: Exception) {
            errorResult(e.message ?: "Lỗi phê duyệt đơn")
        }
    }

    override suspend fun batchDecide(
        bookingIds: List<String>,
        isApproved: Boolean,
        reason: String?
    ): NetworkResult<BatchApprovalResult> {
        return try {
            val action = if (isApproved) "APPROVE" else "REJECT"
            val request = BatchApprovalRequest(action = action, bookingIds = bookingIds, reason = reason)
            val responseDto = mockDataSource.batchDecide(request)
            val domainResult = BatchApprovalResult(
                total = responseDto.total,
                successCount = responseDto.successCount,
                failureCount = responseDto.failureCount,
                results = responseDto.results.map {
                    BatchApprovalSingleResult(
                        bookingId = it.bookingId,
                        success = it.success,
                        status = parseStatus(it.status),
                        message = it.message
                    )
                }
            )
            NetworkResult.Success(domainResult)
        } catch (e: Exception) {
            errorResult(e.message ?: "Lỗi xử lý duyệt hàng loạt")
        }
    }

    override fun getCampusSchedule(date: String, building: String?): Flow<NetworkResult<CampusSchedule>> = flow {
        emit(NetworkResult.Loading)
        try {
            val responseDto = mockDataSource.getCampusSchedule(date, building)
            val domainSchedule = CampusSchedule(
                date = responseDto.date,
                schedules = responseDto.schedules.map { schedDto ->
                    RoomSchedule(
                        roomId = schedDto.roomId,
                        roomName = schedDto.roomName,
                        building = schedDto.building,
                        floor = schedDto.floor ?: 1,
                        timeSlots = schedDto.timeSlots.map { slotDto ->
                            RoomTimeSlot(
                                bookingId = slotDto.bookingId,
                                purpose = slotDto.purpose,
                                requesterName = slotDto.requesterName,
                                startTime = slotDto.startTime,
                                endTime = slotDto.endTime,
                                status = parseStatus(slotDto.status),
                                isLocked = slotDto.isLocked ?: false,
                                lockReason = slotDto.lockReason
                            )
                        }
                    )
                }
            )
            emit(NetworkResult.Success(domainSchedule))
        } catch (e: Exception) {
            emit(errorResult(e.message ?: "Lỗi tải lịch toàn trường"))
        }
    }

    override suspend fun adjustSchedule(
        bookingId: String,
        newRoomId: String,
        newStartTime: String,
        newEndTime: String,
        adjustmentReason: String
    ): NetworkResult<ApprovalItem> {
        return try {
            val request = AdjustScheduleRequest(
                newRoomId = newRoomId,
                newStartTime = newStartTime,
                newEndTime = newEndTime,
                adjustmentReason = adjustmentReason
            )
            val updated = mockDataSource.adjustSchedule(bookingId, request)
            NetworkResult.Success(updated.toDomain())
        } catch (e: Exception) {
            errorResult(e.message ?: "Lỗi điều chỉnh lịch đã duyệt")
        }
    }

    override suspend fun lockRoomSlot(
        roomId: String,
        startTime: String,
        endTime: String,
        reason: String,
        notifyAffected: Boolean
    ): NetworkResult<LockedSlotResult> {
        return try {
            val request = LockScheduleRequest(
                roomId = roomId,
                startTime = startTime,
                endTime = endTime,
                reason = reason,
                notifyAffectedUsers = notifyAffected
            )
            val res = mockDataSource.lockRoomSlot(roomId, request)
            val domain = LockedSlotResult(
                lockId = res.lockId,
                roomId = res.roomId,
                startTime = res.startTime,
                endTime = res.endTime,
                reason = res.reason,
                affectedBookings = res.affectedBookings?.map {
                    AffectedBooking(
                        bookingId = it.bookingId,
                        requesterName = it.requesterName,
                        requesterPhone = it.requesterPhone ?: "",
                        purpose = it.purpose,
                        status = parseStatus(it.status),
                        isCancelled = it.isCancelled ?: false
                    )
                } ?: emptyList()
            )
            NetworkResult.Success(domain)
        } catch (e: Exception) {
            errorResult(e.message ?: "Lỗi khóa khung giờ phòng")
        }
    }

    override fun getApprovalRules(): Flow<NetworkResult<List<ApprovalRule>>> = flow {
        emit(NetworkResult.Loading)
        try {
            val list = mockDataSource.getRules().map { it.toDomain() }
            emit(NetworkResult.Success(list))
        } catch (e: Exception) {
            emit(errorResult(e.message ?: "Lỗi tải quy trình duyệt"))
        }
    }

    override suspend fun updateApprovalRule(rule: ApprovalRule): NetworkResult<ApprovalRule> {
        return try {
            val dto = ApprovalRuleDto(
                id = rule.id,
                name = rule.name,
                description = rule.description,
                isEnabled = rule.isEnabled,
                targetRole = rule.targetRole,
                maxCapacity = rule.maxCapacity,
                autoApprove = rule.autoApprove,
                escalationHours = rule.escalationHours
            )
            val updated = mockDataSource.updateRule(rule.id, dto)
            NetworkResult.Success(updated.toDomain())
        } catch (e: Exception) {
            errorResult(e.message ?: "Lỗi cập nhật cấu hình duyệt")
        }
    }

    override suspend fun sendReminder(bookingId: String): NetworkResult<Unit> {
        return NetworkResult.Success(Unit)
    }

    override fun getRoomCategories(): Flow<NetworkResult<List<RoomCategory>>> = flow {
        emit(NetworkResult.Loading)
        try {
            val list = mockDataSource.getCategories().map { it.toDomain() }
            emit(NetworkResult.Success(list))
        } catch (e: Exception) {
            emit(errorResult(e.message ?: "Lỗi tải danh mục phòng"))
        }
    }

    override suspend fun createRoomCategory(name: String, description: String): NetworkResult<RoomCategory> {
        return try {
            val created = mockDataSource.createCategory(RoomCategoryDto(id = "", name = name, description = description))
            NetworkResult.Success(created.toDomain())
        } catch (e: Exception) {
            errorResult(e.message ?: "Lỗi tạo danh mục phòng")
        }
    }

    override suspend fun updateRoomCategory(id: String, name: String, description: String): NetworkResult<RoomCategory> {
        return try {
            val updated = mockDataSource.updateCategory(id, RoomCategoryDto(id = id, name = name, description = description))
            NetworkResult.Success(updated.toDomain())
        } catch (e: Exception) {
            errorResult(e.message ?: "Lỗi cập nhật danh mục phòng")
        }
    }

    override suspend fun deleteRoomCategory(id: String): NetworkResult<Unit> {
        return try {
            mockDataSource.deleteCategory(id)
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            errorResult(e.message ?: "Lỗi xóa danh mục phòng")
        }
    }

    // Mapper helpers
    private fun ApprovalItemDto.toDomain() = ApprovalItem(
        id = id,
        roomId = roomId,
        roomName = roomName,
        capacity = capacity,
        requesterId = requesterId,
        requesterName = requesterName,
        requesterRole = requesterRole,
        departmentName = departmentName,
        purpose = purpose,
        attendeesCount = attendeesCount,
        startTime = startTime,
        endTime = endTime,
        status = parseStatus(status),
        priority = parsePriority(priority),
        requiredEquipment = requiredEquipment ?: emptyList(),
        rejectionReason = rejectionReason,
        decidedBy = approvedBy,
        createdAt = createdAt ?: ""
    )

    private fun ApprovalRuleDto.toDomain() = ApprovalRule(
        id = id,
        name = name,
        description = description,
        isEnabled = isEnabled,
        targetRole = targetRole,
        maxCapacity = maxCapacity,
        autoApprove = autoApprove,
        escalationHours = escalationHours
    )

    private fun RoomCategoryDto.toDomain() = RoomCategory(
        id = id,
        name = name,
        description = description,
        roomCount = roomCount ?: 0
    )

    private fun parseStatus(status: String?): ApprovalStatus {
        return when (status?.uppercase()) {
            "APPROVED" -> ApprovalStatus.APPROVED
            "REJECTED" -> ApprovalStatus.REJECTED
            "CANCELLED" -> ApprovalStatus.CANCELLED
            "LOCKED" -> ApprovalStatus.LOCKED
            else -> ApprovalStatus.PENDING
        }
    }

    private fun parsePriority(priority: String?): ApprovalPriority {
        return when (priority?.uppercase()) {
            "URGENT" -> ApprovalPriority.URGENT
            "HIGH" -> ApprovalPriority.HIGH
            else -> ApprovalPriority.NORMAL
        }
    }
}

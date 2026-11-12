package com.example.laptrinhmang.feature.approval.domain.usecase

import com.example.laptrinhmang.core.model.ApiError
import com.example.laptrinhmang.core.model.NetworkResult
import com.example.laptrinhmang.feature.approval.domain.model.ApprovalItem
import com.example.laptrinhmang.feature.approval.domain.model.ApprovalRule
import com.example.laptrinhmang.feature.approval.domain.model.BatchApprovalResult
import com.example.laptrinhmang.feature.approval.domain.model.CampusSchedule
import com.example.laptrinhmang.feature.approval.domain.model.LockedSlotResult
import com.example.laptrinhmang.feature.approval.domain.model.RoomCategory
import com.example.laptrinhmang.feature.approval.domain.repository.ApprovalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

private fun errorResult(msg: String, statusCode: Int = 400) =
    NetworkResult.Error(ApiError(statusCode = statusCode, message = msg))

class GetPendingApprovalsUseCase @Inject constructor(
    private val repository: ApprovalRepository
) {
    operator fun invoke(search: String? = null): Flow<NetworkResult<List<ApprovalItem>>> {
        return repository.getPendingApprovals(search)
    }
}

class DecideApprovalUseCase @Inject constructor(
    private val repository: ApprovalRepository
) {
    suspend operator fun invoke(
        id: String,
        isApproved: Boolean,
        reason: String? = null,
        note: String? = null
    ): NetworkResult<ApprovalItem> {
        if (!isApproved && reason.isNullOrBlank()) {
            return errorResult("Bắt buộc phải nhập lý do khi từ chối đơn đặt phòng!")
        }
        return repository.decideApproval(id, isApproved, reason?.trim(), note?.trim())
    }
}

class BatchDecideApprovalsUseCase @Inject constructor(
    private val repository: ApprovalRepository
) {
    suspend operator fun invoke(
        bookingIds: List<String>,
        isApproved: Boolean,
        reason: String? = null
    ): NetworkResult<BatchApprovalResult> {
        if (bookingIds.isEmpty()) {
            return errorResult("Vui lòng chọn ít nhất một đơn để phê duyệt!")
        }
        if (!isApproved && reason.isNullOrBlank()) {
            return errorResult("Bắt buộc phải nhập lý do khi từ chối hàng loạt!")
        }
        return repository.batchDecide(bookingIds, isApproved, reason)
    }
}

class GetCampusScheduleUseCase @Inject constructor(
    private val repository: ApprovalRepository
) {
    operator fun invoke(date: String, building: String? = null): Flow<NetworkResult<CampusSchedule>> {
        return repository.getCampusSchedule(date, building)
    }
}

class AdjustScheduleUseCase @Inject constructor(
    private val repository: ApprovalRepository
) {
    suspend operator fun invoke(
        bookingId: String,
        newRoomId: String,
        newStartTime: String,
        newEndTime: String,
        adjustmentReason: String
    ): NetworkResult<ApprovalItem> {
        if (adjustmentReason.isBlank()) {
            return errorResult("Vui lòng nhập lý do điều chỉnh lịch!")
        }
        return repository.adjustSchedule(bookingId, newRoomId, newStartTime, newEndTime, adjustmentReason.trim())
    }
}

class LockRoomSlotUseCase @Inject constructor(
    private val repository: ApprovalRepository
) {
    suspend operator fun invoke(
        roomId: String,
        startTime: String,
        endTime: String,
        reason: String,
        notifyAffected: Boolean = true
    ): NetworkResult<LockedSlotResult> {
        if (reason.isBlank()) {
            return errorResult("Vui lòng nhập lý do khóa phòng!")
        }
        return repository.lockRoomSlot(roomId, startTime, endTime, reason.trim(), notifyAffected)
    }
}

class ApprovalRulesUseCases @Inject constructor(
    private val repository: ApprovalRepository
) {
    fun getRules(): Flow<NetworkResult<List<ApprovalRule>>> = repository.getApprovalRules()

    suspend fun updateRule(rule: ApprovalRule): NetworkResult<ApprovalRule> {
        return repository.updateApprovalRule(rule)
    }

    suspend fun sendReminder(bookingId: String): NetworkResult<Unit> {
        return repository.sendReminder(bookingId)
    }
}

class RoomCategoryUseCases @Inject constructor(
    private val repository: ApprovalRepository
) {
    fun getCategories(): Flow<NetworkResult<List<RoomCategory>>> = repository.getRoomCategories()

    suspend fun createCategory(name: String, description: String): NetworkResult<RoomCategory> {
        if (name.isBlank()) return errorResult("Tên danh mục không được để trống")
        return repository.createRoomCategory(name.trim(), description.trim())
    }

    suspend fun updateCategory(id: String, name: String, description: String): NetworkResult<RoomCategory> {
        if (name.isBlank()) return errorResult("Tên danh mục không được để trống")
        return repository.updateRoomCategory(id, name.trim(), description.trim())
    }

    suspend fun deleteCategory(id: String): NetworkResult<Unit> {
        return repository.deleteRoomCategory(id)
    }
}

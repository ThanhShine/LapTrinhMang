package com.example.laptrinhmang.feature.approval.domain.repository

import com.example.laptrinhmang.core.model.NetworkResult
import com.example.laptrinhmang.feature.approval.domain.model.ApprovalItem
import com.example.laptrinhmang.feature.approval.domain.model.ApprovalRule
import com.example.laptrinhmang.feature.approval.domain.model.BatchApprovalResult
import com.example.laptrinhmang.feature.approval.domain.model.CampusSchedule
import com.example.laptrinhmang.feature.approval.domain.model.LockedSlotResult
import com.example.laptrinhmang.feature.approval.domain.model.RoomCategory
import kotlinx.coroutines.flow.Flow

interface ApprovalRepository {

    fun getPendingApprovals(search: String? = null): Flow<NetworkResult<List<ApprovalItem>>>

    suspend fun decideApproval(
        id: String,
        isApproved: Boolean,
        reason: String? = null,
        note: String? = null
    ): NetworkResult<ApprovalItem>

    suspend fun batchDecide(
        bookingIds: List<String>,
        isApproved: Boolean,
        reason: String? = null
    ): NetworkResult<BatchApprovalResult>

    fun getCampusSchedule(date: String, building: String? = null): Flow<NetworkResult<CampusSchedule>>

    suspend fun adjustSchedule(
        bookingId: String,
        newRoomId: String,
        newStartTime: String,
        newEndTime: String,
        adjustmentReason: String
    ): NetworkResult<ApprovalItem>

    suspend fun lockRoomSlot(
        roomId: String,
        startTime: String,
        endTime: String,
        reason: String,
        notifyAffected: Boolean = true
    ): NetworkResult<LockedSlotResult>

    fun getApprovalRules(): Flow<NetworkResult<List<ApprovalRule>>>

    suspend fun updateApprovalRule(rule: ApprovalRule): NetworkResult<ApprovalRule>

    suspend fun sendReminder(bookingId: String): NetworkResult<Unit>

    // Room categories
    fun getRoomCategories(): Flow<NetworkResult<List<RoomCategory>>>

    suspend fun createRoomCategory(name: String, description: String): NetworkResult<RoomCategory>

    suspend fun updateRoomCategory(id: String, name: String, description: String): NetworkResult<RoomCategory>

    suspend fun deleteRoomCategory(id: String): NetworkResult<Unit>
}

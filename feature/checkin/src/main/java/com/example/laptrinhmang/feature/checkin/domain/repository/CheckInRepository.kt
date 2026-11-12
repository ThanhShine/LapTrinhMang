package com.example.laptrinhmang.feature.checkin.domain.repository

import com.example.laptrinhmang.core.model.NetworkResult
import com.example.laptrinhmang.feature.checkin.domain.model.CheckInResult
import com.example.laptrinhmang.feature.checkin.domain.model.CheckOutResult
import com.example.laptrinhmang.feature.checkin.domain.model.IssueSeverity
import com.example.laptrinhmang.feature.checkin.domain.model.IssueStatistics
import com.example.laptrinhmang.feature.checkin.domain.model.RoomEquipment
import com.example.laptrinhmang.feature.checkin.domain.model.RoomIssue
import com.example.laptrinhmang.feature.checkin.domain.model.RoomRating
import com.example.laptrinhmang.feature.checkin.domain.model.UsageStatistics
import kotlinx.coroutines.flow.Flow

interface CheckInRepository {

    suspend fun checkIn(qrCode: String, bookingId: Long? = null): NetworkResult<CheckInResult>

    suspend fun checkOut(bookingId: Long, note: String? = null): NetworkResult<CheckOutResult>

    suspend fun createIssue(
        roomCode: String,
        title: String,
        description: String,
        severity: IssueSeverity,
        images: List<String> = emptyList()
    ): NetworkResult<RoomIssue>

    fun getIssues(
        status: String? = null,
        severity: String? = null,
        search: String? = null
    ): Flow<NetworkResult<List<RoomIssue>>>

    suspend fun assignIssue(
        issueId: Long,
        technicianId: Long,
        technicianName: String,
        note: String? = null
    ): NetworkResult<RoomIssue>

    suspend fun resolveIssue(
        issueId: Long,
        resolutionNote: String,
        acceptedBy: String
    ): NetworkResult<RoomIssue>

    suspend fun submitRating(rating: RoomRating): NetworkResult<Unit>

    // PH5: Thiết bị
    fun getEquipment(roomCode: String? = null, status: String? = null): Flow<NetworkResult<List<RoomEquipment>>>

    suspend fun createEquipment(equipment: RoomEquipment): NetworkResult<RoomEquipment>

    suspend fun updateEquipment(equipment: RoomEquipment): NetworkResult<RoomEquipment>

    suspend fun deleteEquipment(id: Long): NetworkResult<Unit>

    // Thống kê & Báo cáo
    fun getUsageStatistics(): Flow<NetworkResult<UsageStatistics>>

    fun getIssueStatistics(): Flow<NetworkResult<IssueStatistics>>
}

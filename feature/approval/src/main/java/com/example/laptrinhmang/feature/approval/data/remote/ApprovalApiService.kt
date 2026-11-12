package com.example.laptrinhmang.feature.approval.data.remote

import com.example.laptrinhmang.feature.approval.data.model.AdjustScheduleRequest
import com.example.laptrinhmang.feature.approval.data.model.ApiResponse
import com.example.laptrinhmang.feature.approval.data.model.ApprovalDecisionRequest
import com.example.laptrinhmang.feature.approval.data.model.ApprovalItemDto
import com.example.laptrinhmang.feature.approval.data.model.ApprovalRuleDto
import com.example.laptrinhmang.feature.approval.data.model.BatchApprovalRequest
import com.example.laptrinhmang.feature.approval.data.model.BatchApprovalResponseDto
import com.example.laptrinhmang.feature.approval.data.model.CampusScheduleResponseDto
import com.example.laptrinhmang.feature.approval.data.model.LockScheduleRequest
import com.example.laptrinhmang.feature.approval.data.model.LockScheduleResponseDto
import com.example.laptrinhmang.feature.approval.data.model.RoomCategoryDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApprovalApiService {

    @GET("approvals/pending")
    suspend fun getPendingApprovals(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("search") search: String? = null
    ): ApiResponse<List<ApprovalItemDto>>

    @POST("approvals/{id}/decide")
    suspend fun decideApproval(
        @Path("id") id: String,
        @Body request: ApprovalDecisionRequest
    ): ApiResponse<ApprovalItemDto>

    @POST("approvals/batch-decide")
    suspend fun batchDecide(
        @Body request: BatchApprovalRequest
    ): ApiResponse<BatchApprovalResponseDto>

    @GET("approvals/campus-schedule")
    suspend fun getCampusSchedule(
        @Query("date") date: String,
        @Query("building") building: String? = null
    ): ApiResponse<CampusScheduleResponseDto>

    @PUT("approvals/schedules/{id}/adjust")
    suspend fun adjustSchedule(
        @Path("id") bookingId: String,
        @Body request: AdjustScheduleRequest
    ): ApiResponse<ApprovalItemDto>

    @POST("approvals/rooms/{id}/lock")
    suspend fun lockRoomSlot(
        @Path("id") roomId: String,
        @Body request: LockScheduleRequest
    ): ApiResponse<LockScheduleResponseDto>

    @GET("approvals/rules")
    suspend fun getApprovalRules(): ApiResponse<List<ApprovalRuleDto>>

    @PUT("approvals/rules/{id}")
    suspend fun updateApprovalRule(
        @Path("id") ruleId: String,
        @Body rule: ApprovalRuleDto
    ): ApiResponse<ApprovalRuleDto>

    @POST("approvals/remind/{id}")
    suspend fun sendReminder(
        @Path("id") bookingId: String
    ): ApiResponse<Unit>

    // PH5: CRUD Room Categories
    @GET("admin/room-categories")
    suspend fun getRoomCategories(): ApiResponse<List<RoomCategoryDto>>

    @POST("admin/room-categories")
    suspend fun createRoomCategory(
        @Body category: RoomCategoryDto
    ): ApiResponse<RoomCategoryDto>

    @PUT("admin/room-categories/{id}")
    suspend fun updateRoomCategory(
        @Path("id") id: String,
        @Body category: RoomCategoryDto
    ): ApiResponse<RoomCategoryDto>

    @DELETE("admin/room-categories/{id}")
    suspend fun deleteRoomCategory(
        @Path("id") id: String
    ): ApiResponse<Unit>
}

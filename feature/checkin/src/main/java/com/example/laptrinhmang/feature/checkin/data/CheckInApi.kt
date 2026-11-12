package com.example.laptrinhmang.feature.checkin.data

import com.example.laptrinhmang.core.model.ApiResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface CheckInApi {

    @POST("checkin/scan")
    suspend fun checkIn(@Body request: CheckInRequest): ApiResponse<CheckInResponseDto>

    @POST("checkin/checkout")
    suspend fun checkOut(@Body request: CheckOutRequest): ApiResponse<CheckOutResponseDto>

    @POST("issues")
    suspend fun createIssue(@Body request: CreateIssueRequest): ApiResponse<RoomIssueDto>

    @GET("issues")
    suspend fun getIssues(
        @Query("status") status: String? = null,
        @Query("severity") severity: String? = null,
        @Query("search") search: String? = null
    ): ApiResponse<List<RoomIssueDto>>

    @PUT("issues/{id}/assign")
    suspend fun assignIssue(
        @Path("id") id: Long,
        @Body request: AssignIssueRequest
    ): ApiResponse<RoomIssueDto>

    @PUT("issues/{id}/resolve")
    suspend fun resolveIssue(
        @Path("id") id: Long,
        @Body request: ResolveIssueRequest
    ): ApiResponse<RoomIssueDto>

    @POST("ratings")
    suspend fun submitRating(@Body request: CreateRatingRequest): ApiResponse<Unit>

    // PH5: CRUD Thiết bị
    @GET("admin/equipment")
    suspend fun getEquipment(
        @Query("room_code") roomCode: String? = null,
        @Query("status") status: String? = null
    ): ApiResponse<List<RoomEquipmentDto>>

    @POST("admin/equipment")
    suspend fun createEquipment(@Body equipment: RoomEquipmentDto): ApiResponse<RoomEquipmentDto>

    @PUT("admin/equipment/{id}")
    suspend fun updateEquipment(
        @Path("id") id: Long,
        @Body equipment: RoomEquipmentDto
    ): ApiResponse<RoomEquipmentDto>

    @DELETE("admin/equipment/{id}")
    suspend fun deleteEquipment(@Path("id") id: Long): ApiResponse<Unit>

    // Thống kê & Báo cáo
    @GET("reports/usage-statistics")
    suspend fun getUsageStatistics(): ApiResponse<UsageStatisticsDto>

    @GET("reports/issues-statistics")
    suspend fun getIssueStatistics(): ApiResponse<IssueStatisticsDto>
}

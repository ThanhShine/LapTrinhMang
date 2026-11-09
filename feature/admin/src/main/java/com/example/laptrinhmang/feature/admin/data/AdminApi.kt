package com.example.laptrinhmang.feature.admin.data

import com.example.laptrinhmang.core.model.ApiResponse
import com.example.laptrinhmang.core.model.PaginatedData
import com.example.laptrinhmang.core.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface AdminApi {

    @GET("admin/users")
    suspend fun getUsers(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("search") search: String? = null,
        @Query("role") role: String? = null,
        @Query("department_id") departmentId: Long? = null,
        @Query("status") status: String? = null
    ): Response<ApiResponse<PaginatedData<User>>>

    @POST("admin/users")
    suspend fun createUser(@Body request: CreateUserRequest): Response<ApiResponse<Unit>>

    @PUT("admin/users/{id}")
    suspend fun updateUser(@Path("id") id: Long, @Body request: UpdateUserRequest): Response<ApiResponse<Unit>>

    @PUT("admin/users/{id}/status")
    suspend fun updateUserStatus(@Path("id") id: Long, @Body request: UserStatusRequest): Response<ApiResponse<Unit>>

    @POST("admin/users/{id}/reset-password")
    suspend fun resetUserPassword(@Path("id") id: Long, @Body request: ResetUserPasswordRequest): Response<ApiResponse<Unit>>

    @GET("admin/roles")
    suspend fun getRoles(): Response<ApiResponse<List<RolePermissionItem>>>

    @PUT("admin/roles/{roleId}/permissions")
    suspend fun updateRolePermissions(
        @Path("roleId") roleId: String,
        @Body request: UpdateRolePermissionsRequest
    ): Response<ApiResponse<Unit>>

    @GET("admin/departments")
    suspend fun getDepartments(): Response<ApiResponse<List<DepartmentItem>>>

    @POST("admin/departments")
    suspend fun createDepartment(@Body request: CreateDepartmentRequest): Response<ApiResponse<Unit>>

    @GET("admin/audit-logs")
    suspend fun getAuditLogs(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("user_id") userId: Long? = null,
        @Query("action") action: String? = null,
        @Query("start_time") startTime: String? = null,
        @Query("end_time") endTime: String? = null
    ): Response<ApiResponse<PaginatedData<AuditLogItem>>>
}

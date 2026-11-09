package com.example.laptrinhmang.feature.admin.data

import com.example.laptrinhmang.core.model.ApiError
import com.example.laptrinhmang.core.model.NetworkResult
import com.example.laptrinhmang.core.model.PaginatedData
import com.example.laptrinhmang.core.model.User
import com.example.laptrinhmang.core.model.UserStatus
import javax.inject.Inject
import javax.inject.Singleton

interface AdminRepository {
    suspend fun getUsers(page: Int = 1, search: String? = null, role: String? = null, status: String? = null): NetworkResult<PaginatedData<User>>
    suspend fun createUser(request: CreateUserRequest): NetworkResult<Unit>
    suspend fun updateUser(id: Long, request: UpdateUserRequest): NetworkResult<Unit>
    suspend fun updateUserStatus(id: Long, status: UserStatus): NetworkResult<Unit>
    suspend fun resetUserPassword(id: Long, newPassword: String): NetworkResult<Unit>
    suspend fun getRoles(): NetworkResult<List<RolePermissionItem>>
    suspend fun updateRolePermissions(roleId: String, permissions: List<String>): NetworkResult<Unit>
    suspend fun getDepartments(): NetworkResult<List<DepartmentItem>>
    suspend fun createDepartment(name: String, parentId: Long?): NetworkResult<Unit>
    suspend fun getAuditLogs(page: Int = 1, userId: Long? = null, action: String? = null, startTime: String? = null, endTime: String? = null): NetworkResult<PaginatedData<AuditLogItem>>
}

@Singleton
class AdminRepositoryImpl @Inject constructor(
    private val adminApi: AdminApi
) : AdminRepository {

    override suspend fun getUsers(page: Int, search: String?, role: String?, status: String?): NetworkResult<PaginatedData<User>> {
        return try {
            val response = adminApi.getUsers(page, 20, search, role, null, status)
            if (response.isSuccessful && response.body()?.data != null) {
                NetworkResult.Success(response.body()!!.data!!)
            } else {
                NetworkResult.Error(ApiError(response.code(), message = "Không thể lấy danh sách người dùng"))
            }
        } catch (e: Exception) {
            NetworkResult.Error(ApiError(500, message = e.localizedMessage ?: "Lỗi kết nối"))
        }
    }

    override suspend fun createUser(request: CreateUserRequest): NetworkResult<Unit> {
        return try {
            adminApi.createUser(request)
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(ApiError(500, message = e.localizedMessage ?: "Lỗi kết nối"))
        }
    }

    override suspend fun updateUser(id: Long, request: UpdateUserRequest): NetworkResult<Unit> {
        return try {
            adminApi.updateUser(id, request)
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(ApiError(500, message = e.localizedMessage ?: "Lỗi kết nối"))
        }
    }

    override suspend fun updateUserStatus(id: Long, status: UserStatus): NetworkResult<Unit> {
        return try {
            adminApi.updateUserStatus(id, UserStatusRequest(status))
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(ApiError(500, message = e.localizedMessage ?: "Lỗi kết nối"))
        }
    }

    override suspend fun resetUserPassword(id: Long, newPassword: String): NetworkResult<Unit> {
        return try {
            adminApi.resetUserPassword(id, ResetUserPasswordRequest(newPassword))
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(ApiError(500, message = e.localizedMessage ?: "Lỗi kết nối"))
        }
    }

    override suspend fun getRoles(): NetworkResult<List<RolePermissionItem>> {
        return try {
            val response = adminApi.getRoles()
            if (response.isSuccessful && response.body()?.data != null) {
                NetworkResult.Success(response.body()!!.data!!)
            } else {
                NetworkResult.Error(ApiError(response.code(), message = "Không thể lấy danh sách vai trò"))
            }
        } catch (e: Exception) {
            NetworkResult.Error(ApiError(500, message = e.localizedMessage ?: "Lỗi kết nối"))
        }
    }

    override suspend fun updateRolePermissions(roleId: String, permissions: List<String>): NetworkResult<Unit> {
        return try {
            adminApi.updateRolePermissions(roleId, UpdateRolePermissionsRequest(permissions))
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(ApiError(500, message = e.localizedMessage ?: "Lỗi kết nối"))
        }
    }

    override suspend fun getDepartments(): NetworkResult<List<DepartmentItem>> {
        return try {
            val response = adminApi.getDepartments()
            if (response.isSuccessful && response.body()?.data != null) {
                NetworkResult.Success(response.body()!!.data!!)
            } else {
                NetworkResult.Error(ApiError(response.code(), message = "Không thể lấy danh sách đơn vị"))
            }
        } catch (e: Exception) {
            NetworkResult.Error(ApiError(500, message = e.localizedMessage ?: "Lỗi kết nối"))
        }
    }

    override suspend fun createDepartment(name: String, parentId: Long?): NetworkResult<Unit> {
        return try {
            adminApi.createDepartment(CreateDepartmentRequest(name, parentId))
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(ApiError(500, message = e.localizedMessage ?: "Lỗi kết nối"))
        }
    }

    override suspend fun getAuditLogs(page: Int, userId: Long?, action: String?, startTime: String?, endTime: String?): NetworkResult<PaginatedData<AuditLogItem>> {
        return try {
            val response = adminApi.getAuditLogs(page, 20, userId, action, startTime, endTime)
            if (response.isSuccessful && response.body()?.data != null) {
                NetworkResult.Success(response.body()!!.data!!)
            } else {
                NetworkResult.Error(ApiError(response.code(), message = "Không thể lấy danh sách audit log"))
            }
        } catch (e: Exception) {
            NetworkResult.Error(ApiError(500, message = e.localizedMessage ?: "Lỗi kết nối"))
        }
    }
}

package com.example.laptrinhmang.feature.account.data

import com.example.laptrinhmang.core.model.ApiError
import com.example.laptrinhmang.core.model.NetworkResult
import com.example.laptrinhmang.core.model.PaginatedData
import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Singleton

interface NotificationRepository {
    suspend fun getNotifications(page: Int = 1, isReadFilter: Boolean? = null): NetworkResult<PaginatedData<NotificationItem>>
    suspend fun markAsRead(id: Long): NetworkResult<Unit>
    suspend fun markAllAsRead(): NetworkResult<Unit>
    suspend fun updateFcmToken(token: String): NetworkResult<Unit>
}

@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val notificationApi: NotificationApi,
    private val gson: Gson
) : NotificationRepository {

    override suspend fun getNotifications(page: Int, isReadFilter: Boolean?): NetworkResult<PaginatedData<NotificationItem>> {
        return try {
            val response = notificationApi.getNotifications(page = page, isRead = isReadFilter)
            if (response.isSuccessful && response.body()?.data != null) {
                NetworkResult.Success(response.body()!!.data!!)
            } else {
                NetworkResult.Error(ApiError(response.code(), message = "Không thể lấy danh sách thông báo"))
            }
        } catch (e: Exception) {
            NetworkResult.Error(ApiError(500, message = e.localizedMessage ?: "Lỗi kết nối"))
        }
    }

    override suspend fun markAsRead(id: Long): NetworkResult<Unit> {
        return try {
            notificationApi.markAsRead(id)
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(ApiError(500, message = e.localizedMessage ?: "Lỗi kết nối"))
        }
    }

    override suspend fun markAllAsRead(): NetworkResult<Unit> {
        return try {
            notificationApi.markAllAsRead()
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(ApiError(500, message = e.localizedMessage ?: "Lỗi kết nối"))
        }
    }

    override suspend fun updateFcmToken(token: String): NetworkResult<Unit> {
        return try {
            notificationApi.sendFcmToken(FcmTokenRequest(token))
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(ApiError(500, message = e.localizedMessage ?: "Lỗi kết nối"))
        }
    }
}

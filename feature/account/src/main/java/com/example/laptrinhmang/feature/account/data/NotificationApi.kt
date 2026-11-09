package com.example.laptrinhmang.feature.account.data

import com.example.laptrinhmang.core.model.ApiResponse
import com.example.laptrinhmang.core.model.PaginatedData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface NotificationApi {

    @GET("notifications")
    suspend fun getNotifications(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("is_read") isRead: Boolean? = null
    ): Response<ApiResponse<PaginatedData<NotificationItem>>>

    @PUT("notifications/{id}/read")
    suspend fun markAsRead(@Path("id") id: Long): Response<ApiResponse<Unit>>

    @PUT("notifications/read-all")
    suspend fun markAllAsRead(): Response<ApiResponse<Unit>>

    @POST("notifications/fcm-token")
    suspend fun sendFcmToken(@Body request: FcmTokenRequest): Response<ApiResponse<Unit>>
}

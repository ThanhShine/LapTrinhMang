package com.example.laptrinhmang.feature.booking.data

import com.example.laptrinhmang.core.model.ApiResponse
import com.example.laptrinhmang.core.model.Booking
import com.example.laptrinhmang.core.model.Building
import com.example.laptrinhmang.core.model.PaginatedData
import com.example.laptrinhmang.core.model.Room
import com.example.laptrinhmang.core.model.RoomScheduleItem
import com.example.laptrinhmang.core.model.RoomType
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface BookingApi {

    @GET("api/v1/rooms")
    suspend fun getRooms(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 30,
        @Query("search") search: String? = null,
        @Query("building_id") buildingId: Long? = null,
        @Query("room_type_id") roomTypeId: Long? = null,
        @Query("min_capacity") minCapacity: Int? = null,
        @Query("status") status: String? = null,
        @Query("is_favorite") isFavorite: Boolean? = null
    ): Response<ApiResponse<PaginatedData<Room>>>

    @GET("api/v1/rooms/{id}")
    suspend fun getRoomDetail(@Path("id") roomId: Long): Response<ApiResponse<Room>>

    @POST("api/v1/rooms/search-available")
    suspend fun searchAvailableRooms(@Body request: SearchAvailableRoomsRequest): Response<ApiResponse<List<Room>>>

    @GET("api/v1/rooms/{id}/schedule")
    suspend fun getRoomSchedule(
        @Path("id") roomId: Long,
        @Query("date") date: String
    ): Response<ApiResponse<List<RoomScheduleItem>>>

    @POST("api/v1/rooms/{id}/favorite")
    suspend fun toggleFavorite(@Path("id") roomId: Long): Response<ApiResponse<ToggleFavoriteResponse>>

    @POST("api/v1/bookings")
    suspend fun createBooking(@Body request: CreateBookingRequest): Response<ApiResponse<Booking>>

    @GET("api/v1/bookings/my-bookings")
    suspend fun getMyBookings(
        @Query("status") status: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50
    ): Response<ApiResponse<PaginatedData<Booking>>>

    @PUT("api/v1/bookings/{id}")
    suspend fun updateBooking(
        @Path("id") bookingId: Long,
        @Body request: UpdateBookingRequest
    ): Response<ApiResponse<Booking>>

    @POST("api/v1/bookings/{id}/cancel")
    suspend fun cancelBooking(
        @Path("id") bookingId: Long,
        @Body request: CancelBookingRequest
    ): Response<ApiResponse<Unit>>

    @GET("api/v1/buildings")
    suspend fun getBuildings(): Response<ApiResponse<List<Building>>>

    @POST("api/v1/buildings")
    suspend fun createBuilding(@Body request: CreateBuildingRequest): Response<ApiResponse<Building>>

    @DELETE("api/v1/buildings/{id}")
    suspend fun deleteBuilding(@Path("id") id: Long): Response<ApiResponse<Unit>>

    @GET("api/v1/room-types")
    suspend fun getRoomTypes(): Response<ApiResponse<List<RoomType>>>

    @POST("api/v1/room-types")
    suspend fun createRoomType(@Body request: CreateRoomTypeRequest): Response<ApiResponse<RoomType>>

    @DELETE("api/v1/room-types/{id}")
    suspend fun deleteRoomType(@Path("id") id: Long): Response<ApiResponse<Unit>>
}


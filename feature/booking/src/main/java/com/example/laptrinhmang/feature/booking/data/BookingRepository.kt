package com.example.laptrinhmang.feature.booking.data

import com.example.laptrinhmang.core.model.Booking
import com.example.laptrinhmang.core.model.Building
import com.example.laptrinhmang.core.model.NetworkResult
import com.example.laptrinhmang.core.model.Room
import com.example.laptrinhmang.core.model.RoomScheduleItem
import com.example.laptrinhmang.core.model.RoomType

interface BookingRepository {
    suspend fun getRooms(
        search: String? = null,
        buildingId: Long? = null,
        roomTypeId: Long? = null,
        minCapacity: Int? = null,
        status: String? = null,
        isFavorite: Boolean? = null
    ): NetworkResult<List<Room>>

    suspend fun getRoomDetail(roomId: Long): NetworkResult<Room>

    suspend fun toggleFavorite(roomId: Long): NetworkResult<Boolean>

    suspend fun getRoomSchedule(roomId: Long, date: String): NetworkResult<List<RoomScheduleItem>>

    suspend fun createBooking(
        roomId: Long,
        startTime: String,
        endTime: String,
        purpose: String,
        attendeeCount: Int,
        isRecurring: Boolean,
        recurringRule: String?
    ): NetworkResult<Booking>

    suspend fun getMyBookings(status: String? = null): NetworkResult<List<Booking>>

    suspend fun updateBooking(
        bookingId: Long,
        startTime: String,
        endTime: String,
        purpose: String,
        attendeeCount: Int
    ): NetworkResult<Booking>

    suspend fun cancelBooking(bookingId: Long, reason: String): NetworkResult<Unit>

    suspend fun getBuildings(): NetworkResult<List<Building>>
    suspend fun createBuilding(name: String, code: String, floors: Int, desc: String?): NetworkResult<Building>
    suspend fun deleteBuilding(id: Long): NetworkResult<Unit>

    suspend fun getRoomTypes(): NetworkResult<List<RoomType>>
    suspend fun createRoomType(name: String, code: String, desc: String?): NetworkResult<RoomType>
    suspend fun deleteRoomType(id: Long): NetworkResult<Unit>
}


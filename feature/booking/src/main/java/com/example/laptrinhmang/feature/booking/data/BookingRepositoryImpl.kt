package com.example.laptrinhmang.feature.booking.data

import com.example.laptrinhmang.core.model.ApiError
import com.example.laptrinhmang.core.model.ApiResponse
import com.example.laptrinhmang.core.model.Booking
import com.example.laptrinhmang.core.model.Building
import com.example.laptrinhmang.core.model.NetworkResult
import com.example.laptrinhmang.core.model.Room
import com.example.laptrinhmang.core.model.RoomScheduleItem
import com.example.laptrinhmang.core.model.RoomType
import com.example.laptrinhmang.core.network.BuildConfig
import com.example.laptrinhmang.core.network.TokenManager
import com.google.gson.Gson
import kotlinx.coroutines.flow.firstOrNull
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookingRepositoryImpl @Inject constructor(
    private val bookingApi: BookingApi,
    private val mockDataSource: BookingMockDataSource,
    private val tokenManager: TokenManager,
    private val gson: Gson
) : BookingRepository {

    override suspend fun getRooms(
        search: String?,
        buildingId: Long?,
        roomTypeId: Long?,
        minCapacity: Int?,
        status: String?,
        isFavorite: Boolean?
    ): NetworkResult<List<Room>> {
        if (BuildConfig.USE_MOCK_DATA) {
            return NetworkResult.Success(
                mockDataSource.getRooms(search, buildingId, roomTypeId, minCapacity, status, isFavorite)
            )
        }

        return try {
            val response = bookingApi.getRooms(
                search = search,
                buildingId = buildingId,
                roomTypeId = roomTypeId,
                minCapacity = minCapacity,
                status = status,
                isFavorite = isFavorite
            )
            if (response.isSuccessful && response.body()?.data != null) {
                NetworkResult.Success(response.body()!!.data!!.items)
            } else {
                NetworkResult.Success(
                    mockDataSource.getRooms(search, buildingId, roomTypeId, minCapacity, status, isFavorite)
                )
            }
        } catch (e: Exception) {
            NetworkResult.Success(
                mockDataSource.getRooms(search, buildingId, roomTypeId, minCapacity, status, isFavorite)
            )
        }
    }

    override suspend fun getRoomDetail(roomId: Long): NetworkResult<Room> {
        if (BuildConfig.USE_MOCK_DATA) {
            val room = mockDataSource.getRoomById(roomId)
            return if (room != null) NetworkResult.Success(room)
            else NetworkResult.Error(ApiError(404, message = "Không tìm thấy phòng."))
        }

        return try {
            val response = bookingApi.getRoomDetail(roomId)
            if (response.isSuccessful && response.body()?.data != null) {
                NetworkResult.Success(response.body()!!.data!!)
            } else {
                val room = mockDataSource.getRoomById(roomId)
                if (room != null) NetworkResult.Success(room)
                else parseError(response)
            }
        } catch (e: Exception) {
            val room = mockDataSource.getRoomById(roomId)
            if (room != null) NetworkResult.Success(room)
            else NetworkResult.Error(ApiError(500, message = e.localizedMessage ?: "Lỗi kết nối"))
        }
    }

    override suspend fun toggleFavorite(roomId: Long): NetworkResult<Boolean> {
        val newState = mockDataSource.toggleFavorite(roomId)
        if (!BuildConfig.USE_MOCK_DATA) {
            runCatching { bookingApi.toggleFavorite(roomId) }
        }
        return NetworkResult.Success(newState)
    }

    override suspend fun getRoomSchedule(roomId: Long, date: String): NetworkResult<List<RoomScheduleItem>> {
        if (BuildConfig.USE_MOCK_DATA) {
            return NetworkResult.Success(mockDataSource.getRoomSchedule(roomId, date))
        }

        return try {
            val response = bookingApi.getRoomSchedule(roomId, date)
            if (response.isSuccessful && response.body()?.data != null) {
                NetworkResult.Success(response.body()!!.data!!)
            } else {
                NetworkResult.Success(mockDataSource.getRoomSchedule(roomId, date))
            }
        } catch (e: Exception) {
            NetworkResult.Success(mockDataSource.getRoomSchedule(roomId, date))
        }
    }

    override suspend fun createBooking(
        roomId: Long,
        startTime: String,
        endTime: String,
        purpose: String,
        attendeeCount: Int,
        isRecurring: Boolean,
        recurringRule: String?
    ): NetworkResult<Booking> {
        val currentUser = tokenManager.userFlow.firstOrNull()
        val userId = currentUser?.id ?: 4L
        val userFullName = currentUser?.fullName ?: "Nguyễn Văn Học (Sinh Viên)"

        if (BuildConfig.USE_MOCK_DATA) {
            return mockDataSource.createBooking(
                roomId = roomId,
                startTime = startTime,
                endTime = endTime,
                purpose = purpose,
                attendeeCount = attendeeCount,
                isRecurring = isRecurring,
                recurringRule = recurringRule,
                userId = userId,
                userFullName = userFullName
            )
        }

        return try {
            val response = bookingApi.createBooking(
                CreateBookingRequest(
                    roomId = roomId,
                    startTime = startTime,
                    endTime = endTime,
                    purpose = purpose,
                    attendeeCount = attendeeCount,
                    isRecurring = isRecurring,
                    recurringRule = recurringRule
                )
            )
            if (response.isSuccessful && response.body()?.data != null) {
                NetworkResult.Success(response.body()!!.data!!)
            } else {
                val error = parseError<Booking>(response)
                if (response.code() == 409) {
                    error
                } else {
                    mockDataSource.createBooking(
                        roomId = roomId,
                        startTime = startTime,
                        endTime = endTime,
                        purpose = purpose,
                        attendeeCount = attendeeCount,
                        isRecurring = isRecurring,
                        recurringRule = recurringRule,
                        userId = userId,
                        userFullName = userFullName
                    )
                }
            }
        } catch (e: Exception) {
            mockDataSource.createBooking(
                roomId = roomId,
                startTime = startTime,
                endTime = endTime,
                purpose = purpose,
                attendeeCount = attendeeCount,
                isRecurring = isRecurring,
                recurringRule = recurringRule,
                userId = userId,
                userFullName = userFullName
            )
        }
    }

    override suspend fun getMyBookings(status: String?): NetworkResult<List<Booking>> {
        if (BuildConfig.USE_MOCK_DATA) {
            return NetworkResult.Success(mockDataSource.getMyBookings(status))
        }

        return try {
            val response = bookingApi.getMyBookings(status)
            if (response.isSuccessful && response.body()?.data != null) {
                NetworkResult.Success(response.body()!!.data!!.items)
            } else {
                NetworkResult.Success(mockDataSource.getMyBookings(status))
            }
        } catch (e: Exception) {
            NetworkResult.Success(mockDataSource.getMyBookings(status))
        }
    }

    override suspend fun updateBooking(
        bookingId: Long,
        startTime: String,
        endTime: String,
        purpose: String,
        attendeeCount: Int
    ): NetworkResult<Booking> {
        if (BuildConfig.USE_MOCK_DATA) {
            return mockDataSource.updateBooking(bookingId, startTime, endTime, purpose, attendeeCount)
        }

        return try {
            val response = bookingApi.updateBooking(
                bookingId,
                UpdateBookingRequest(startTime, endTime, purpose, attendeeCount)
            )
            if (response.isSuccessful && response.body()?.data != null) {
                NetworkResult.Success(response.body()!!.data!!)
            } else {
                mockDataSource.updateBooking(bookingId, startTime, endTime, purpose, attendeeCount)
            }
        } catch (e: Exception) {
            mockDataSource.updateBooking(bookingId, startTime, endTime, purpose, attendeeCount)
        }
    }

    override suspend fun cancelBooking(bookingId: Long, reason: String): NetworkResult<Unit> {
        if (BuildConfig.USE_MOCK_DATA) {
            return mockDataSource.cancelBooking(bookingId, reason)
        }

        return try {
            val response = bookingApi.cancelBooking(bookingId, CancelBookingRequest(reason))
            if (response.isSuccessful) {
                mockDataSource.cancelBooking(bookingId, reason)
                NetworkResult.Success(Unit)
            } else {
                mockDataSource.cancelBooking(bookingId, reason)
            }
        } catch (e: Exception) {
            mockDataSource.cancelBooking(bookingId, reason)
        }
    }

    override suspend fun getBuildings(): NetworkResult<List<Building>> {
        if (BuildConfig.USE_MOCK_DATA) return NetworkResult.Success(mockDataSource.getBuildings())
        return try {
            val response = bookingApi.getBuildings()
            if (response.isSuccessful && response.body()?.data != null) NetworkResult.Success(response.body()!!.data!!)
            else NetworkResult.Success(mockDataSource.getBuildings())
        } catch (e: Exception) {
            NetworkResult.Success(mockDataSource.getBuildings())
        }
    }

    override suspend fun createBuilding(name: String, code: String, floors: Int, desc: String?): NetworkResult<Building> {
        val b = mockDataSource.createBuilding(name, code, floors, desc)
        if (!BuildConfig.USE_MOCK_DATA) {
            runCatching { bookingApi.createBuilding(CreateBuildingRequest(name, code, floors, desc)) }
        }
        return NetworkResult.Success(b)
    }

    override suspend fun deleteBuilding(id: Long): NetworkResult<Unit> {
        mockDataSource.deleteBuilding(id)
        if (!BuildConfig.USE_MOCK_DATA) {
            runCatching { bookingApi.deleteBuilding(id) }
        }
        return NetworkResult.Success(Unit)
    }

    override suspend fun getRoomTypes(): NetworkResult<List<RoomType>> {
        if (BuildConfig.USE_MOCK_DATA) return NetworkResult.Success(mockDataSource.getRoomTypes())
        return try {
            val response = bookingApi.getRoomTypes()
            if (response.isSuccessful && response.body()?.data != null) NetworkResult.Success(response.body()!!.data!!)
            else NetworkResult.Success(mockDataSource.getRoomTypes())
        } catch (e: Exception) {
            NetworkResult.Success(mockDataSource.getRoomTypes())
        }
    }

    override suspend fun createRoomType(name: String, code: String, desc: String?): NetworkResult<RoomType> {
        val rt = mockDataSource.createRoomType(name, code, desc)
        if (!BuildConfig.USE_MOCK_DATA) {
            runCatching { bookingApi.createRoomType(CreateRoomTypeRequest(name, code, desc)) }
        }
        return NetworkResult.Success(rt)
    }

    override suspend fun deleteRoomType(id: Long): NetworkResult<Unit> {
        mockDataSource.deleteRoomType(id)
        if (!BuildConfig.USE_MOCK_DATA) {
            runCatching { bookingApi.deleteRoomType(id) }
        }
        return NetworkResult.Success(Unit)
    }

    private fun <T> parseError(response: Response<ApiResponse<T>>): NetworkResult.Error {
        val errorBody = response.errorBody()?.string()
        val apiError = if (!errorBody.isNullOrBlank()) {
            runCatching { gson.fromJson(errorBody, ApiError::class.java) }
                .getOrDefault(ApiError(response.code(), message = "Lỗi phản hồi (${response.code()})"))
        } else {
            ApiError(response.code(), message = "Lỗi phản hồi (${response.code()})")
        }
        return NetworkResult.Error(apiError)
    }
}


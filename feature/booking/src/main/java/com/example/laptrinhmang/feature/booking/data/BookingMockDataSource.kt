package com.example.laptrinhmang.feature.booking.data

import com.example.laptrinhmang.core.model.ApiError
import com.example.laptrinhmang.core.model.Booking
import com.example.laptrinhmang.core.model.BookingStatus
import com.example.laptrinhmang.core.model.Building
import com.example.laptrinhmang.core.model.NetworkResult
import com.example.laptrinhmang.core.model.Room
import com.example.laptrinhmang.core.model.RoomScheduleItem
import com.example.laptrinhmang.core.model.RoomStatus
import com.example.laptrinhmang.core.model.RoomType
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookingMockDataSource @Inject constructor() {

    private val bookingIdSeq = AtomicLong(3000L)
    private val buildingIdSeq = AtomicLong(10L)
    private val roomTypeIdSeq = AtomicLong(10L)

    private val buildings = mutableListOf(
        Building(id = 1, name = "Tòa nhà A (Giảng đường chính)", code = "A", floorsCount = 6, description = "Khu giảng đường trung tâm"),
        Building(id = 2, name = "Tòa nhà B (Khu Kiến trúc)", code = "B", floorsCount = 5, description = "Khu xưởng vẽ và thực hành kiến trúc"),
        Building(id = 3, name = "Tòa nhà C (Trung tâm CNTT & TN)", code = "C", floorsCount = 4, description = "Phòng máy tính và phòng Lab"),
        Building(id = 4, name = "Tòa nhà D (Mỹ thuật ứng dụng)", code = "D", floorsCount = 3, description = "Xưởng điêu khắc, hội họa")
    )

    private val roomTypes = mutableListOf(
        RoomType(id = 1, name = "Phòng Lý thuyết", code = "THEORY", description = "Bàn ghế tiêu chuẩn, máy chiếu, micro"),
        RoomType(id = 2, name = "Phòng Máy tính CNTT", code = "LAB_IT", description = "45 bộ máy PC cấu hình cao, switch Gigabit"),
        RoomType(id = 3, name = "Xưởng vẽ Kiến trúc", code = "STUDIO", description = "Bàn vẽ A0/A1, giá vẽ, ánh sáng tự nhiên tiêu chuẩn"),
        RoomType(id = 4, name = "Hội trường lớn", code = "HALL", description = "Sức chứa 250 chỗ, âm thanh vòm, máy chiếu đôi"),
        RoomType(id = 5, name = "Phòng Đồ án tốt nghiệp", code = "CAPSTONE", description = "Phòng làm việc nhóm và bảo vệ đề tài")
    )

    private val rooms = mutableListOf(
        Room(
            id = 1,
            roomCode = "A.302",
            roomName = "Giảng đường Lý thuyết 302",
            buildingId = 1,
            buildingName = "Tòa nhà A",
            floor = 3,
            capacity = 60,
            roomTypeId = 1,
            roomTypeName = "Phòng Lý thuyết",
            status = RoomStatus.AVAILABLE,
            amenities = listOf("Máy chiếu", "Điều hòa 2 chiều", "Micro không dây", "Wifi DAU-HighSpeed", "Bảng trượt"),
            description = "Phòng học lý thuyết chuẩn cho các lớp chuyên ngành CNTT và Kiến trúc.",
            imageUrl = "https://images.unsplash.com/photo-1517245386807-bb43f82c33c4?auto=format&fit=crop&w=600&q=80",
            isFavorite = true
        ),
        Room(
            id = 2,
            roomCode = "C.201",
            roomName = "Phòng Lab Mạng & An toàn thông tin",
            buildingId = 3,
            buildingName = "Tòa nhà C",
            floor = 2,
            capacity = 45,
            roomTypeId = 2,
            roomTypeName = "Phòng Máy tính CNTT",
            status = RoomStatus.AVAILABLE,
            amenities = listOf("45 Máy tính Core i7", "Switch Cisco", "Điều hòa", "Máy chiếu 4K", "Internet cáp quang"),
            description = "Dành riêng cho thực hành Lập trình Mạng, Hệ điều hành và An ninh mạng.",
            imageUrl = "https://images.unsplash.com/photo-1562774053-701939374585?auto=format&fit=crop&w=600&q=80",
            isFavorite = true
        ),
        Room(
            id = 3,
            roomCode = "B.405",
            roomName = "Xưởng Đồ án Kiến trúc B405",
            buildingId = 2,
            buildingName = "Tòa nhà B",
            floor = 4,
            capacity = 35,
            roomTypeId = 3,
            roomTypeName = "Xưởng vẽ Kiến trúc",
            status = RoomStatus.AVAILABLE,
            amenities = listOf("Bàn vẽ kiến trúc A0", "Đèn chiếu chuyên dụng", "Điều hòa", "Giá vẽ mỹ thuật"),
            description = "Không gian yên tĩnh, ánh sáng tự nhiên tốt, thích hợp làm đồ án nhóm.",
            imageUrl = "https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&w=600&q=80",
            isFavorite = false
        ),
        Room(
            id = 4,
            roomCode = "A.101",
            roomName = "Hội trường Đa năng A101",
            buildingId = 1,
            buildingName = "Tòa nhà A",
            floor = 1,
            capacity = 250,
            roomTypeId = 4,
            roomTypeName = "Hội trường lớn",
            status = RoomStatus.AVAILABLE,
            amenities = listOf("Sân khấu lớn", "Màn hình LED P2.5", "Âm thanh hội trường", "Micro bục giảng", "Máy lạnh công nghiệp"),
            description = "Nơi tổ chức hội thảo khoa học, đại hội sinh viên và các sự kiện trường.",
            imageUrl = "https://images.unsplash.com/photo-1544531586-fde5298cdd40?auto=format&fit=crop&w=600&q=80",
            isFavorite = false
        ),
        Room(
            id = 5,
            roomCode = "C.305",
            roomName = "Phòng Thực hành AI & Dữ liệu",
            buildingId = 3,
            buildingName = "Tòa nhà C",
            floor = 3,
            capacity = 40,
            roomTypeId = 2,
            roomTypeName = "Phòng Máy tính CNTT",
            status = RoomStatus.MAINTENANCE,
            amenities = listOf("40 PC card rời RTX 4060", "Điều hòa", "Smartboard 85 inch"),
            description = "Đang bảo dưỡng hệ thống điều hòa và cập nhật GPU Driver.",
            imageUrl = null,
            isFavorite = false
        ),
        Room(
            id = 6,
            roomCode = "B.202",
            roomName = "Phòng Chuyên đề Quy hoạch Đô thị",
            buildingId = 2,
            buildingName = "Tòa nhà B",
            floor = 2,
            capacity = 50,
            roomTypeId = 5,
            roomTypeName = "Phòng Đồ án tốt nghiệp",
            status = RoomStatus.AVAILABLE,
            amenities = listOf("Bàn làm việc nhóm", "Máy in A3 màu", "Máy chiếu Laser", "Điều hòa"),
            description = "Phục vụ sinh viên năm cuối làm đồ án quy hoạch kiến trúc.",
            imageUrl = null,
            isFavorite = false
        )
    )

    private val bookings = mutableListOf(
        Booking(
            id = 101,
            bookingCode = "BK-2026-001",
            roomId = 1,
            roomCode = "A.302",
            roomName = "Giảng đường Lý thuyết 302",
            buildingName = "Tòa nhà A",
            userId = 4,
            userFullName = "Nguyễn Văn Học (Sinh Viên)",
            userPhone = "0905123456",
            startTime = "2026-04-12T08:00:00+07:00",
            endTime = "2026-04-12T11:00:00+07:00",
            purpose = "Họp nhóm Ôn thi Lập trình Mạng & Đồ án",
            attendeeCount = 15,
            status = BookingStatus.APPROVED,
            rejectionReason = null,
            isRecurring = false,
            createdAt = "2026-04-01T08:30:00+07:00"
        ),
        Booking(
            id = 102,
            bookingCode = "BK-2026-002",
            roomId = 2,
            roomCode = "C.201",
            roomName = "Phòng Lab Mạng & An toàn thông tin",
            buildingName = "Tòa nhà C",
            userId = 4,
            userFullName = "Nguyễn Văn Học (Sinh Viên)",
            userPhone = "0905123456",
            startTime = "2026-04-15T13:30:00+07:00",
            endTime = "2026-04-15T17:00:00+07:00",
            purpose = "Thực nghiệm Socket Programming đa luồng",
            attendeeCount = 20,
            status = BookingStatus.PENDING,
            rejectionReason = null,
            isRecurring = false,
            createdAt = "2026-04-05T14:10:00+07:00"
        ),
        Booking(
            id = 103,
            bookingCode = "BK-2026-003",
            roomId = 4,
            roomCode = "A.101",
            roomName = "Hội trường Đa năng A101",
            buildingName = "Tòa nhà A",
            userId = 4,
            userFullName = "Nguyễn Văn Học (Sinh Viên)",
            userPhone = "0905123456",
            startTime = "2026-03-25T08:00:00+07:00",
            endTime = "2026-03-25T11:30:00+07:00",
            purpose = "Sinh hoạt CLB IT đầu kỳ",
            attendeeCount = 80,
            status = BookingStatus.REJECTED,
            rejectionReason = "Trùng lịch bảo trì âm thanh của nhà trường.",
            isRecurring = false,
            createdAt = "2026-03-20T10:00:00+07:00"
        )
    )

    fun getRooms(
        search: String? = null,
        buildingId: Long? = null,
        roomTypeId: Long? = null,
        minCapacity: Int? = null,
        status: String? = null,
        isFavorite: Boolean? = null
    ): List<Room> {
        return rooms.filter { room ->
            val matchSearch = search.isNullOrBlank() ||
                    room.roomCode.contains(search, ignoreCase = true) ||
                    room.roomName.contains(search, ignoreCase = true) ||
                    room.amenities.any { it.contains(search, ignoreCase = true) }

            val matchBuilding = buildingId == null || room.buildingId == buildingId
            val matchType = roomTypeId == null || room.roomTypeId == roomTypeId
            val matchCapacity = minCapacity == null || room.capacity >= minCapacity
            val matchStatus = status.isNullOrBlank() || room.status.name.equals(status, ignoreCase = true)
            val matchFav = isFavorite == null || (if (isFavorite) room.isFavorite else true)

            matchSearch && matchBuilding && matchType && matchCapacity && matchStatus && matchFav
        }
    }

    fun getRoomById(id: Long): Room? = rooms.find { it.id == id }

    fun toggleFavorite(roomId: Long): Boolean {
        val index = rooms.indexOfFirst { it.id == roomId }
        if (index != -1) {
            val updated = rooms[index].copy(isFavorite = !rooms[index].isFavorite)
            rooms[index] = updated
            return updated.isFavorite
        }
        return false
    }

    fun getRoomSchedule(roomId: Long, date: String): List<RoomScheduleItem> {
        val dateFilter = date.take(10) // "YYYY-MM-DD"
        return bookings.filter {
            it.roomId == roomId &&
                    (it.status == BookingStatus.APPROVED || it.status == BookingStatus.PENDING) &&
                    it.startTime.startsWith(dateFilter)
        }.map {
            RoomScheduleItem(
                bookingId = it.id,
                startTime = it.startTime,
                endTime = it.endTime,
                purpose = it.purpose,
                userFullName = it.userFullName,
                status = it.status
            )
        }
    }

    fun createBooking(
        roomId: Long,
        startTime: String,
        endTime: String,
        purpose: String,
        attendeeCount: Int,
        isRecurring: Boolean,
        recurringRule: String?,
        userId: Long = 4,
        userFullName: String = "Nguyễn Văn Học (Sinh Viên)"
    ): NetworkResult<Booking> {
        val room = getRoomById(roomId)
            ?: return NetworkResult.Error(ApiError(404, message = "Không tìm thấy phòng học."))

        if (room.status == RoomStatus.LOCKED || room.status == RoomStatus.MAINTENANCE) {
            return NetworkResult.Error(
                ApiError(409, code = "ROOM_NOT_AVAILABLE", message = "Phòng hiện đang tạm khóa hoặc đang bảo trì.")
            )
        }

        // Check conflict with approved bookings
        val hasConflict = bookings.any {
            it.roomId == roomId &&
                    it.status == BookingStatus.APPROVED &&
                    checkTimeOverlap(it.startTime, it.endTime, startTime, endTime)
        }

        if (hasConflict) {
            return NetworkResult.Error(
                ApiError(
                    409,
                    code = "BOOKING_CONFLICT",
                    message = "Khung giờ bạn chọn đã bị trùng với lịch đặt phòng đã được phê duyệt khác."
                )
            )
        }

        val newId = bookingIdSeq.incrementAndGet()
        val newBooking = Booking(
            id = newId,
            bookingCode = "BK-2026-$newId",
            roomId = roomId,
            roomCode = room.roomCode,
            roomName = room.roomName,
            buildingName = room.buildingName,
            userId = userId,
            userFullName = userFullName,
            startTime = startTime,
            endTime = endTime,
            purpose = purpose,
            attendeeCount = attendeeCount,
            status = BookingStatus.PENDING,
            rejectionReason = null,
            isRecurring = isRecurring,
            recurringRule = recurringRule,
            createdAt = "2026-04-10T08:00:00+07:00"
        )
        bookings.add(0, newBooking)
        return NetworkResult.Success(newBooking)
    }

    fun getMyBookings(status: String? = null): List<Booking> {
        return if (status.isNullOrBlank() || status.equals("ALL", ignoreCase = true)) {
            bookings.toList()
        } else {
            bookings.filter { it.status.name.equals(status, ignoreCase = true) }
        }
    }

    fun updateBooking(
        bookingId: Long,
        startTime: String,
        endTime: String,
        purpose: String,
        attendeeCount: Int
    ): NetworkResult<Booking> {
        val index = bookings.indexOfFirst { it.id == bookingId }
        if (index == -1) {
            return NetworkResult.Error(ApiError(404, message = "Không tìm thấy đơn đặt phòng."))
        }
        val current = bookings[index]
        if (current.status != BookingStatus.PENDING) {
            return NetworkResult.Error(ApiError(400, message = "Chỉ có thể chỉnh sửa đơn khi đang ở trạng thái 'Chờ duyệt'."))
        }

        val updated = current.copy(
            startTime = startTime,
            endTime = endTime,
            purpose = purpose,
            attendeeCount = attendeeCount
        )
        bookings[index] = updated
        return NetworkResult.Success(updated)
    }

    fun cancelBooking(bookingId: Long, reason: String): NetworkResult<Unit> {
        val index = bookings.indexOfFirst { it.id == bookingId }
        if (index == -1) {
            return NetworkResult.Error(ApiError(404, message = "Không tìm thấy đơn đặt phòng."))
        }
        val current = bookings[index]
        if (current.status == BookingStatus.CANCELLED || current.status == BookingStatus.COMPLETED) {
            return NetworkResult.Error(ApiError(400, message = "Đơn này không thể hủy."))
        }

        bookings[index] = current.copy(
            status = BookingStatus.CANCELLED,
            rejectionReason = "Đã hủy bởi người dùng: $reason"
        )
        return NetworkResult.Success(Unit)
    }

    fun getBuildings(): List<Building> = buildings.toList()

    fun createBuilding(name: String, code: String, floors: Int, desc: String?): Building {
        val b = Building(id = buildingIdSeq.incrementAndGet(), name = name, code = code, floorsCount = floors, description = desc)
        buildings.add(b)
        return b
    }

    fun deleteBuilding(id: Long) {
        buildings.removeAll { it.id == id }
    }

    fun getRoomTypes(): List<RoomType> = roomTypes.toList()

    fun createRoomType(name: String, code: String, desc: String?): RoomType {
        val rt = RoomType(id = roomTypeIdSeq.incrementAndGet(), name = name, code = code, description = desc)
        roomTypes.add(rt)
        return rt
    }

    fun deleteRoomType(id: Long) {
        roomTypes.removeAll { it.id == id }
    }

    private fun checkTimeOverlap(startA: String, endA: String, startB: String, endB: String): Boolean {
        return (startA < endB && endA > startB)
    }
}


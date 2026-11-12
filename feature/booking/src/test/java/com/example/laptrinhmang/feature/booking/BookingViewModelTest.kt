package com.example.laptrinhmang.feature.booking

import com.example.laptrinhmang.core.model.ApiError
import com.example.laptrinhmang.core.model.Booking
import com.example.laptrinhmang.core.model.BookingStatus
import com.example.laptrinhmang.core.model.Building
import com.example.laptrinhmang.core.model.NetworkResult
import com.example.laptrinhmang.core.model.Room
import com.example.laptrinhmang.core.model.RoomScheduleItem
import com.example.laptrinhmang.core.model.RoomStatus
import com.example.laptrinhmang.core.model.RoomType
import com.example.laptrinhmang.feature.booking.data.BookingRepository
import com.example.laptrinhmang.feature.booking.ui.BookingTab
import com.example.laptrinhmang.feature.booking.ui.BookingViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BookingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeBookingRepository
    private lateinit var viewModel: BookingViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeBookingRepository()
        viewModel = BookingViewModel(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initial_load_populates_rooms_and_bookings() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2, state.rooms.size)
        assertEquals(1, state.myBookings.size)
        assertEquals(BookingTab.ROOMS, state.selectedTab)
    }

    @Test
    fun setTab_switches_tab_correctly() = runTest {
        viewModel.setTab(BookingTab.MY_BOOKINGS)
        assertEquals(BookingTab.MY_BOOKINGS, viewModel.uiState.value.selectedTab)

        viewModel.setTab(BookingTab.FAVORITES)
        assertEquals(BookingTab.FAVORITES, viewModel.uiState.value.selectedTab)
    }

    @Test
    fun toggleFavorite_updates_favorite_status() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.toggleFavorite(1L)
        testDispatcher.scheduler.advanceUntilIdle()

        val room = viewModel.uiState.value.rooms.find { it.id == 1L }
        assertNotNull(room)
        assertTrue(room!!.isFavorite)
    }

    @Test
    fun create_booking_success_adds_new_booking() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        val targetRoom = viewModel.uiState.value.rooms.first()
        viewModel.openCreateBookingDialog(targetRoom)
        assertTrue(viewModel.uiState.value.showCreateDialog)

        viewModel.submitCreateBooking(
            startTime = "2026-04-20T08:00:00+07:00",
            endTime = "2026-04-20T11:00:00+07:00",
            purpose = "Họp Lớp 20KTPM1",
            attendeeCount = 30,
            isRecurring = false,
            recurringRule = null
        )
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.showCreateDialog)
        assertNotNull(viewModel.uiState.value.userMessage)
        assertEquals(2, viewModel.uiState.value.myBookings.size)
    }

    @Test
    fun cancel_booking_updates_booking_status_to_cancelled() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        val booking = viewModel.uiState.value.myBookings.first()
        viewModel.openCancelBookingConfirm(booking)
        viewModel.submitCancelBooking("Bận việc đột xuất")
        testDispatcher.scheduler.advanceUntilIdle()

        val cancelledBooking = viewModel.uiState.value.myBookings.find { it.id == booking.id }
        assertNotNull(cancelledBooking)
        assertEquals(BookingStatus.CANCELLED, cancelledBooking!!.status)
    }

    @Test
    fun search_query_filters_rooms() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onSearchQueryChanged("Lab")
        testDispatcher.scheduler.advanceUntilIdle()

        val rooms = viewModel.uiState.value.rooms
        assertEquals(1, rooms.size)
        assertEquals("C.201", rooms.first().roomCode)
    }
}

private class FakeBookingRepository : BookingRepository {
    private val rooms = mutableListOf(
        Room(
            id = 1L,
            roomCode = "A.302",
            roomName = "Giảng đường Lý thuyết 302",
            buildingId = 1L,
            buildingName = "Tòa nhà A",
            capacity = 60,
            status = RoomStatus.AVAILABLE,
            isFavorite = false
        ),
        Room(
            id = 2L,
            roomCode = "C.201",
            roomName = "Phòng Lab Mạng",
            buildingId = 3L,
            buildingName = "Tòa nhà C",
            capacity = 45,
            status = RoomStatus.AVAILABLE,
            isFavorite = true
        )
    )

    private val bookings = mutableListOf(
        Booking(
            id = 101L,
            bookingCode = "BK-2026-001",
            roomId = 1L,
            roomCode = "A.302",
            roomName = "Giảng đường Lý thuyết 302",
            userId = 4L,
            userFullName = "Nguyễn Văn Học",
            startTime = "2026-04-12T08:00:00+07:00",
            endTime = "2026-04-12T11:00:00+07:00",
            purpose = "Họp nhóm",
            attendeeCount = 15,
            status = BookingStatus.PENDING
        )
    )

    override suspend fun getRooms(
        search: String?,
        buildingId: Long?,
        roomTypeId: Long?,
        minCapacity: Int?,
        status: String?,
        isFavorite: Boolean?
    ): NetworkResult<List<Room>> {
        val filtered = rooms.filter { r ->
            (search.isNullOrBlank() || r.roomCode.contains(search, true) || r.roomName.contains(search, true)) &&
                    (isFavorite == null || r.isFavorite == isFavorite)
        }
        return NetworkResult.Success(filtered)
    }

    override suspend fun getRoomDetail(roomId: Long): NetworkResult<Room> {
        val r = rooms.find { it.id == roomId }
        return if (r != null) NetworkResult.Success(r)
        else NetworkResult.Error(ApiError(404, message = "Not found"))
    }

    override suspend fun toggleFavorite(roomId: Long): NetworkResult<Boolean> {
        val index = rooms.indexOfFirst { it.id == roomId }
        if (index != -1) {
            val updated = rooms[index].copy(isFavorite = !rooms[index].isFavorite)
            rooms[index] = updated
            return NetworkResult.Success(updated.isFavorite)
        }
        return NetworkResult.Success(false)
    }

    override suspend fun getRoomSchedule(roomId: Long, date: String): NetworkResult<List<RoomScheduleItem>> {
        return NetworkResult.Success(emptyList())
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
        val newBooking = Booking(
            id = (bookings.size + 100).toLong(),
            bookingCode = "BK-2026-${bookings.size + 100}",
            roomId = roomId,
            roomCode = "A.302",
            roomName = "Giảng đường Lý thuyết 302",
            userId = 4L,
            userFullName = "Nguyễn Văn Học",
            startTime = startTime,
            endTime = endTime,
            purpose = purpose,
            attendeeCount = attendeeCount,
            status = BookingStatus.PENDING,
            isRecurring = isRecurring,
            recurringRule = recurringRule
        )
        bookings.add(0, newBooking)
        return NetworkResult.Success(newBooking)
    }

    override suspend fun getMyBookings(status: String?): NetworkResult<List<Booking>> {
        val list = if (status.isNullOrBlank() || status == "ALL") bookings
        else bookings.filter { it.status.name.equals(status, true) }
        return NetworkResult.Success(list)
    }

    override suspend fun updateBooking(
        bookingId: Long,
        startTime: String,
        endTime: String,
        purpose: String,
        attendeeCount: Int
    ): NetworkResult<Booking> {
        val index = bookings.indexOfFirst { it.id == bookingId }
        val updated = bookings[index].copy(startTime = startTime, endTime = endTime, purpose = purpose, attendeeCount = attendeeCount)
        bookings[index] = updated
        return NetworkResult.Success(updated)
    }

    override suspend fun cancelBooking(bookingId: Long, reason: String): NetworkResult<Unit> {
        val index = bookings.indexOfFirst { it.id == bookingId }
        bookings[index] = bookings[index].copy(status = BookingStatus.CANCELLED)
        return NetworkResult.Success(Unit)
    }

    override suspend fun getBuildings(): NetworkResult<List<Building>> =
        NetworkResult.Success(listOf(Building(1L, "Tòa nhà A", "A", 6, null)))

    override suspend fun createBuilding(name: String, code: String, floors: Int, desc: String?): NetworkResult<Building> =
        NetworkResult.Success(Building(2L, name, code, floors, desc))

    override suspend fun deleteBuilding(id: Long): NetworkResult<Unit> = NetworkResult.Success(Unit)

    override suspend fun getRoomTypes(): NetworkResult<List<RoomType>> =
        NetworkResult.Success(listOf(RoomType(1L, "Lý thuyết", "THEORY", null)))

    override suspend fun createRoomType(name: String, code: String, desc: String?): NetworkResult<RoomType> =
        NetworkResult.Success(RoomType(2L, name, code, desc))

    override suspend fun deleteRoomType(id: Long): NetworkResult<Unit> = NetworkResult.Success(Unit)
}


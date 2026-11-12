package com.example.laptrinhmang.feature.booking.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laptrinhmang.core.model.Booking
import com.example.laptrinhmang.core.model.Building
import com.example.laptrinhmang.core.model.NetworkResult
import com.example.laptrinhmang.core.model.Room
import com.example.laptrinhmang.core.model.RoomScheduleItem
import com.example.laptrinhmang.core.model.RoomType
import com.example.laptrinhmang.core.ui.components.CrudItem
import com.example.laptrinhmang.feature.booking.data.BookingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

enum class BookingTab(val title: String) {
    ROOMS("Phòng học"),
    MY_BOOKINGS("Đơn của tôi"),
    FAVORITES("Yêu thích"),
    SCHEDULE("Lịch phòng"),
    MANAGEMENT("Danh mục (P2)")
}

data class BookingUiState(
    val selectedTab: BookingTab = BookingTab.ROOMS,
    val searchQuery: String = "",
    val selectedBuildingId: Long? = null,
    val selectedRoomTypeId: Long? = null,
    val minCapacity: Int? = null,
    val rooms: List<Room> = emptyList(),
    val favoriteRooms: List<Room> = emptyList(),
    val buildings: List<Building> = emptyList(),
    val roomTypes: List<RoomType> = emptyList(),
    val selectedRoomForDetail: Room? = null,
    val scheduleDate: String = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
    val selectedScheduleRoomId: Long = 1L,
    val roomScheduleItems: List<RoomScheduleItem> = emptyList(),
    val myBookings: List<Booking> = emptyList(),
    val myBookingsStatusFilter: String = "ALL",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userMessage: String? = null,
    // Dialog states
    val showCreateDialog: Boolean = false,
    val bookingTargetRoom: Room? = null,
    val showEditDialog: Boolean = false,
    val editingBooking: Booking? = null,
    val bookingToCancel: Booking? = null,
    val sharingBooking: Booking? = null,
    val managementSubTab: Int = 0 // 0 = Buildings, 1 = RoomTypes
)

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val bookingRepository: BookingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookingUiState())
    val uiState: StateFlow<BookingUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            loadBuildingsAndTypes()
            loadRooms()
            loadMyBookings()
            loadSchedule()
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun setTab(tab: BookingTab) {
        _uiState.update { it.copy(selectedTab = tab) }
        when (tab) {
            BookingTab.ROOMS -> loadRooms()
            BookingTab.MY_BOOKINGS -> loadMyBookings()
            BookingTab.FAVORITES -> loadFavorites()
            BookingTab.SCHEDULE -> loadSchedule()
            BookingTab.MANAGEMENT -> loadBuildingsAndTypes()
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        loadRooms()
    }

    fun onBuildingFilterSelected(buildingId: Long?) {
        _uiState.update { it.copy(selectedBuildingId = buildingId) }
        loadRooms()
    }

    fun onRoomTypeFilterSelected(roomTypeId: Long?) {
        _uiState.update { it.copy(selectedRoomTypeId = roomTypeId) }
        loadRooms()
    }

    fun onMinCapacitySelected(capacity: Int?) {
        _uiState.update { it.copy(minCapacity = capacity) }
        loadRooms()
    }

    fun loadRooms() {
        viewModelScope.launch {
            val state = _uiState.value
            when (val result = bookingRepository.getRooms(
                search = state.searchQuery.ifBlank { null },
                buildingId = state.selectedBuildingId,
                roomTypeId = state.selectedRoomTypeId,
                minCapacity = state.minCapacity
            )) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            rooms = result.data,
                            favoriteRooms = result.data.filter { r -> r.isFavorite }
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(errorMessage = result.apiError.message) }
                }
                else -> Unit
            }
        }
    }

    fun loadFavorites() {
        viewModelScope.launch {
            when (val result = bookingRepository.getRooms(isFavorite = true)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(favoriteRooms = result.data) }
                }
                else -> Unit
            }
        }
    }

    fun toggleFavorite(roomId: Long) {
        viewModelScope.launch {
            when (val result = bookingRepository.toggleFavorite(roomId)) {
                is NetworkResult.Success -> {
                    _uiState.update { s ->
                        val updatedRooms = s.rooms.map { r ->
                            if (r.id == roomId) r.copy(isFavorite = result.data) else r
                        }
                        s.copy(
                            rooms = updatedRooms,
                            favoriteRooms = updatedRooms.filter { it.isFavorite },
                            userMessage = if (result.data) "Đã thêm vào danh sách yêu thích" else "Đã xóa khỏi yêu thích"
                        )
                    }
                }
                else -> Unit
            }
        }
    }

    fun onSelectRoomForDetail(room: Room?) {
        _uiState.update { it.copy(selectedRoomForDetail = room) }
    }

    fun onScheduleDateChanged(date: String) {
        _uiState.update { it.copy(scheduleDate = date) }
        loadSchedule()
    }

    fun onScheduleRoomChanged(roomId: Long) {
        _uiState.update { it.copy(selectedScheduleRoomId = roomId) }
        loadSchedule()
    }

    fun loadSchedule() {
        viewModelScope.launch {
            val state = _uiState.value
            when (val result = bookingRepository.getRoomSchedule(state.selectedScheduleRoomId, state.scheduleDate)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(roomScheduleItems = result.data) }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(roomScheduleItems = emptyList()) }
                }
                else -> Unit
            }
        }
    }

    fun openCreateBookingDialog(room: Room) {
        _uiState.update {
            it.copy(
                showCreateDialog = true,
                bookingTargetRoom = room,
                selectedRoomForDetail = null
            )
        }
    }

    fun closeCreateBookingDialog() {
        _uiState.update { it.copy(showCreateDialog = false, bookingTargetRoom = null) }
    }

    fun submitCreateBooking(
        startTime: String,
        endTime: String,
        purpose: String,
        attendeeCount: Int,
        isRecurring: Boolean,
        recurringRule: String?
    ) {
        val targetRoom = _uiState.value.bookingTargetRoom ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = bookingRepository.createBooking(
                roomId = targetRoom.id,
                startTime = startTime,
                endTime = endTime,
                purpose = purpose,
                attendeeCount = attendeeCount,
                isRecurring = isRecurring,
                recurringRule = recurringRule
            )) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            showCreateDialog = false,
                            bookingTargetRoom = null,
                            userMessage = "Đã gửi yêu cầu đặt phòng ${targetRoom.roomCode} thành công! Đơn đang chờ phê duyệt."
                        )
                    }
                    loadMyBookings()
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.apiError.message
                        )
                    }
                }
                else -> _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onMyBookingsStatusFilterChanged(status: String) {
        _uiState.update { it.copy(myBookingsStatusFilter = status) }
        loadMyBookings()
    }

    fun loadMyBookings() {
        viewModelScope.launch {
            val status = _uiState.value.myBookingsStatusFilter
            when (val result = bookingRepository.getMyBookings(if (status == "ALL") null else status)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(myBookings = result.data) }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(errorMessage = result.apiError.message) }
                }
                else -> Unit
            }
        }
    }

    fun openEditBookingDialog(booking: Booking) {
        _uiState.update { it.copy(showEditDialog = true, editingBooking = booking) }
    }

    fun closeEditBookingDialog() {
        _uiState.update { it.copy(showEditDialog = false, editingBooking = null) }
    }

    fun submitUpdateBooking(startTime: String, endTime: String, purpose: String, attendeeCount: Int) {
        val booking = _uiState.value.editingBooking ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = bookingRepository.updateBooking(booking.id, startTime, endTime, purpose, attendeeCount)) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            showEditDialog = false,
                            editingBooking = null,
                            userMessage = "Cập nhật yêu cầu đặt phòng thành công!"
                        )
                    }
                    loadMyBookings()
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.apiError.message
                        )
                    }
                }
                else -> _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun openCancelBookingConfirm(booking: Booking) {
        _uiState.update { it.copy(bookingToCancel = booking) }
    }

    fun closeCancelBookingConfirm() {
        _uiState.update { it.copy(bookingToCancel = null) }
    }

    fun submitCancelBooking(reason: String = "Người dùng chủ động hủy") {
        val booking = _uiState.value.bookingToCancel ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = bookingRepository.cancelBooking(booking.id, reason)) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            bookingToCancel = null,
                            userMessage = "Đã hủy đơn đặt phòng ${booking.bookingCode} thành công."
                        )
                    }
                    loadMyBookings()
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            bookingToCancel = null,
                            errorMessage = result.apiError.message
                        )
                    }
                }
                else -> _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun openShareBookingDialog(booking: Booking) {
        _uiState.update { it.copy(sharingBooking = booking) }
    }

    fun closeShareBookingDialog() {
        _uiState.update { it.copy(sharingBooking = null) }
    }

    // P2 Management (Buildings & Room Types)
    fun setManagementSubTab(tabIndex: Int) {
        _uiState.update { it.copy(managementSubTab = tabIndex) }
    }

    private fun loadBuildingsAndTypes() {
        viewModelScope.launch {
            when (val bRes = bookingRepository.getBuildings()) {
                is NetworkResult.Success -> _uiState.update { it.copy(buildings = bRes.data) }
                else -> Unit
            }
            when (val rtRes = bookingRepository.getRoomTypes()) {
                is NetworkResult.Success -> _uiState.update { it.copy(roomTypes = rtRes.data) }
                else -> Unit
            }
        }
    }

    fun addBuilding(title: String, subtitle: String) {
        viewModelScope.launch {
            val code = title.take(2).uppercase()
            val floors = subtitle.filter { it.isDigit() }.toIntOrNull() ?: 3
            bookingRepository.createBuilding(title, code, floors, subtitle)
            loadBuildingsAndTypes()
            _uiState.update { it.copy(userMessage = "Đã thêm tòa nhà: $title") }
        }
    }

    fun deleteBuilding(id: String) {
        viewModelScope.launch {
            val bId = id.toLongOrNull() ?: return@launch
            bookingRepository.deleteBuilding(bId)
            loadBuildingsAndTypes()
            _uiState.update { it.copy(userMessage = "Đã xóa tòa nhà thành công") }
        }
    }

    fun addRoomType(title: String, subtitle: String) {
        viewModelScope.launch {
            val code = title.replace(" ", "_").uppercase().take(8)
            bookingRepository.createRoomType(title, code, subtitle)
            loadBuildingsAndTypes()
            _uiState.update { it.copy(userMessage = "Đã thêm loại phòng: $title") }
        }
    }

    fun deleteRoomType(id: String) {
        viewModelScope.launch {
            val rtId = id.toLongOrNull() ?: return@launch
            bookingRepository.deleteRoomType(rtId)
            loadBuildingsAndTypes()
            _uiState.update { it.copy(userMessage = "Đã xóa loại phòng thành công") }
        }
    }

    fun clearUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}


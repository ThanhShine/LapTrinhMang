package com.example.laptrinhmang.feature.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.laptrinhmang.core.ui.components.CrudItem
import com.example.laptrinhmang.core.ui.components.DauConfirmDialog
import com.example.laptrinhmang.core.ui.components.DauEmptyState
import com.example.laptrinhmang.core.ui.components.DauLoadingState
import com.example.laptrinhmang.core.ui.components.DauTimeUtils
import com.example.laptrinhmang.core.ui.components.GenericCrudScreen
import com.example.laptrinhmang.feature.booking.ui.BookingTab
import com.example.laptrinhmang.feature.booking.ui.BookingUiState
import com.example.laptrinhmang.feature.booking.ui.BookingViewModel
import com.example.laptrinhmang.feature.booking.ui.components.BookingCard
import com.example.laptrinhmang.feature.booking.ui.components.CreateBookingDialog
import com.example.laptrinhmang.feature.booking.ui.components.EditBookingDialog
import com.example.laptrinhmang.feature.booking.ui.components.RoomCard
import com.example.laptrinhmang.feature.booking.ui.components.RoomDetailDialog
import com.example.laptrinhmang.feature.booking.ui.components.ShareBookingDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    modifier: Modifier = Modifier,
    viewModel: BookingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { err ->
            snackbarHostState.showSnackbar(err)
            viewModel.clearErrorMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main Navigation Tabs
            ScrollableTabRow(
                selectedTabIndex = uiState.selectedTab.ordinal,
                edgePadding = 16.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                BookingTab.entries.forEach { tab ->
                    Tab(
                        selected = uiState.selectedTab == tab,
                        onClick = { viewModel.setTab(tab) },
                        text = { Text(text = tab.title) },
                        icon = {
                            val icon: ImageVector = when (tab) {
                                BookingTab.ROOMS -> Icons.Default.MeetingRoom
                                BookingTab.MY_BOOKINGS -> Icons.Default.ListAlt
                                BookingTab.FAVORITES -> Icons.Default.Favorite
                                BookingTab.SCHEDULE -> Icons.Default.CalendarMonth
                                BookingTab.MANAGEMENT -> Icons.Default.Settings
                            }
                            Icon(imageVector = icon, contentDescription = tab.title)
                        }
                    )
                }
            }

            // Tab Content
            when (uiState.selectedTab) {
                BookingTab.ROOMS -> RoomsTabContent(uiState = uiState, viewModel = viewModel)
                BookingTab.MY_BOOKINGS -> MyBookingsTabContent(uiState = uiState, viewModel = viewModel)
                BookingTab.FAVORITES -> FavoritesTabContent(uiState = uiState, viewModel = viewModel)
                BookingTab.SCHEDULE -> ScheduleTabContent(uiState = uiState, viewModel = viewModel)
                BookingTab.MANAGEMENT -> ManagementTabContent(uiState = uiState, viewModel = viewModel)
            }
        }
    }

    // Dialogs
    if (uiState.showCreateDialog && uiState.bookingTargetRoom != null) {
        CreateBookingDialog(
            room = uiState.bookingTargetRoom!!,
            onDismiss = viewModel::closeCreateBookingDialog,
            onSubmit = viewModel::submitCreateBooking
        )
    }

    if (uiState.showEditDialog && uiState.editingBooking != null) {
        EditBookingDialog(
            booking = uiState.editingBooking!!,
            onDismiss = viewModel::closeEditBookingDialog,
            onSubmit = viewModel::submitUpdateBooking
        )
    }

    if (uiState.selectedRoomForDetail != null) {
        RoomDetailDialog(
            room = uiState.selectedRoomForDetail!!,
            onDismiss = { viewModel.onSelectRoomForDetail(null) },
            onToggleFavorite = viewModel::toggleFavorite,
            onBookRoom = viewModel::openCreateBookingDialog
        )
    }

    if (uiState.bookingToCancel != null) {
        DauConfirmDialog(
            title = "Xác nhận hủy đơn đặt phòng",
            message = "Bạn có chắc chắn muốn hủy đơn đặt phòng ${uiState.bookingToCancel!!.bookingCode} (${uiState.bookingToCancel!!.roomCode}) không?",
            confirmText = "Hủy đơn",
            dismissText = "Giữ lại",
            onConfirm = { viewModel.submitCancelBooking("Người dùng chủ động hủy") },
            onDismiss = viewModel::closeCancelBookingConfirm
        )
    }

    if (uiState.sharingBooking != null) {
        ShareBookingDialog(
            booking = uiState.sharingBooking!!,
            onDismiss = viewModel::closeShareBookingDialog,
            onCopied = {
                // Clipboard copy handled inside dialog
            }
        )
    }
}

@Composable
private fun RoomsTabContent(
    uiState: BookingUiState,
    viewModel: BookingViewModel
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Search Bar
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = viewModel::onSearchQueryChanged,
            placeholder = { Text("Tìm theo mã phòng, tên phòng hoặc tiện ích...") },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (uiState.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Xóa")
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // Building Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = uiState.selectedBuildingId == null,
                onClick = { viewModel.onBuildingFilterSelected(null) },
                label = { Text("Tất cả tòa") }
            )
            uiState.buildings.forEach { b ->
                FilterChip(
                    selected = uiState.selectedBuildingId == b.id,
                    onClick = { viewModel.onBuildingFilterSelected(b.id) },
                    label = { Text(b.code) }
                )
            }
        }

        // Room Type Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = uiState.selectedRoomTypeId == null,
                onClick = { viewModel.onRoomTypeFilterSelected(null) },
                label = { Text("Tất cả loại phòng") }
            )
            uiState.roomTypes.forEach { rt ->
                FilterChip(
                    selected = uiState.selectedRoomTypeId == rt.id,
                    onClick = { viewModel.onRoomTypeFilterSelected(rt.id) },
                    label = { Text(rt.name) }
                )
            }
        }

        if (uiState.isLoading && uiState.rooms.isEmpty()) {
            DauLoadingState(message = "Đang tải danh sách phòng học...")
        } else if (uiState.rooms.isEmpty()) {
            DauEmptyState(message = "Không tìm thấy phòng học phù hợp với bộ lọc")
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.rooms, key = { it.id }) { room ->
                    RoomCard(
                        room = room,
                        onClick = { viewModel.onSelectRoomForDetail(room) },
                        onToggleFavorite = { viewModel.toggleFavorite(room.id) },
                        onViewSchedule = {
                            viewModel.onScheduleRoomChanged(room.id)
                            viewModel.setTab(BookingTab.SCHEDULE)
                        },
                        onBookNow = { viewModel.openCreateBookingDialog(room) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MyBookingsTabContent(
    uiState: BookingUiState,
    viewModel: BookingViewModel
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Status Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val statusFilters = listOf(
                "ALL" to "Tất cả",
                "PENDING" to "Chờ duyệt",
                "APPROVED" to "Đã duyệt",
                "REJECTED" to "Từ chối",
                "CANCELLED" to "Đã hủy"
            )
            statusFilters.forEach { (code, title) ->
                FilterChip(
                    selected = uiState.myBookingsStatusFilter == code,
                    onClick = { viewModel.onMyBookingsStatusFilterChanged(code) },
                    label = { Text(title) }
                )
            }
        }

        if (uiState.myBookings.isEmpty()) {
            DauEmptyState(message = "Bạn chưa có đơn đặt phòng nào trong danh mục này.")
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.myBookings, key = { it.id }) { booking ->
                    BookingCard(
                        booking = booking,
                        onEdit = { viewModel.openEditBookingDialog(booking) },
                        onCancel = { viewModel.openCancelBookingConfirm(booking) },
                        onShare = { viewModel.openShareBookingDialog(booking) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoritesTabContent(
    uiState: BookingUiState,
    viewModel: BookingViewModel
) {
    if (uiState.favoriteRooms.isEmpty()) {
        DauEmptyState(message = "Bạn chưa lưu phòng yêu thích nào. Hãy nhấn biểu tượng trái tim ở danh sách phòng!")
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(uiState.favoriteRooms, key = { it.id }) { room ->
                RoomCard(
                    room = room,
                    onClick = { viewModel.onSelectRoomForDetail(room) },
                    onToggleFavorite = { viewModel.toggleFavorite(room.id) },
                    onViewSchedule = {
                        viewModel.onScheduleRoomChanged(room.id)
                        viewModel.setTab(BookingTab.SCHEDULE)
                    },
                    onBookNow = { viewModel.openCreateBookingDialog(room) }
                )
            }
        }
    }
}

@Composable
private fun ScheduleTabContent(
    uiState: BookingUiState,
    viewModel: BookingViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Date Input
        OutlinedTextField(
            value = uiState.scheduleDate,
            onValueChange = viewModel::onScheduleDateChanged,
            label = { Text("Ngày xem lịch (YYYY-MM-DD)") },
            singleLine = true,
            leadingIcon = { Icon(imageVector = Icons.Default.DateRange, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Room Selector Chips
        Text(text = "Chọn phòng học:", style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            uiState.rooms.forEach { room ->
                FilterChip(
                    selected = uiState.selectedScheduleRoomId == room.id,
                    onClick = { viewModel.onScheduleRoomChanged(room.id) },
                    label = { Text(room.roomCode) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Lịch sử dụng trong ngày (${uiState.scheduleDate}):",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (uiState.roomScheduleItems.isEmpty()) {
            DauEmptyState(message = "Phòng trống trong ngày này! Không có lịch học hoặc đơn mượn phòng nào.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.roomScheduleItems, key = { it.bookingId }) { item ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${item.startTime.substringAfter("T").take(5)} - ${item.endTime.substringAfter("T").take(5)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = item.purpose,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Người đăng ký: ${item.userFullName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF10B981).copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = item.status.displayName,
                                    color = Color(0xFF10B981),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ManagementTabContent(
    uiState: BookingUiState,
    viewModel: BookingViewModel
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = uiState.managementSubTab) {
            Tab(
                selected = uiState.managementSubTab == 0,
                onClick = { viewModel.setManagementSubTab(0) },
                text = { Text("Tòa nhà & Tầng") }
            )
            Tab(
                selected = uiState.managementSubTab == 1,
                onClick = { viewModel.setManagementSubTab(1) },
                text = { Text("Loại phòng") }
            )
        }

        if (uiState.managementSubTab == 0) {
            val buildingCrudItems = uiState.buildings.map {
                CrudItem(
                    id = it.id.toString(),
                    title = it.name,
                    subtitle = "Mã: ${it.code} • Số tầng: ${it.floorsCount} • ${it.description ?: ""}"
                )
            }
            GenericCrudScreen(
                title = "Danh mục Tòa nhà",
                items = buildingCrudItems,
                onAddItem = { title, subtitle -> viewModel.addBuilding(title, subtitle) },
                onEditItem = { /* Edit handled by add/update */ },
                onDeleteItem = { id -> viewModel.deleteBuilding(id) }
            )
        } else {
            val roomTypeCrudItems = uiState.roomTypes.map {
                CrudItem(
                    id = it.id.toString(),
                    title = it.name,
                    subtitle = "Mã: ${it.code} • ${it.description ?: ""}"
                )
            }
            GenericCrudScreen(
                title = "Danh mục Loại phòng",
                items = roomTypeCrudItems,
                onAddItem = { title, subtitle -> viewModel.addRoomType(title, subtitle) },
                onEditItem = { /* Edit handled by add/update */ },
                onDeleteItem = { id -> viewModel.deleteRoomType(id) }
            )
        }
    }
}

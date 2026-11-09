package com.example.laptrinhmang.feature.account.ui.notifications

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.laptrinhmang.core.ui.components.DauEmptyState
import com.example.laptrinhmang.core.ui.components.DauErrorState
import com.example.laptrinhmang.core.ui.components.DauFilterChipGroup
import com.example.laptrinhmang.core.ui.components.DauLoadingState
import com.example.laptrinhmang.core.ui.components.DauTimeUtils
import com.example.laptrinhmang.feature.account.data.NotificationItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    uiState: NotificationUiState,
    onFilterSelected: (NotificationFilter) -> Unit,
    onNotificationClick: (NotificationItem) -> Unit,
    onMarkAllAsReadClick: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thông báo") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    IconButton(onClick = onMarkAllAsReadClick) {
                        Icon(imageVector = Icons.Default.DoneAll, contentDescription = "Đọc tất cả")
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            DauFilterChipGroup(
                options = NotificationFilter.entries,
                selectedOption = uiState.filter,
                onOptionSelected = onFilterSelected,
                labelProvider = {
                    when (it) {
                        NotificationFilter.ALL -> "Tất cả"
                        NotificationFilter.UNREAD -> "Chưa đọc"
                        NotificationFilter.READ -> "Đã đọc"
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                uiState.isLoading -> {
                    DauLoadingState()
                }
                uiState.errorMessage != null -> {
                    DauErrorState(message = uiState.errorMessage)
                }
                uiState.items.isEmpty() -> {
                    DauEmptyState(message = "Không có thông báo nào")
                }
                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(uiState.items, key = { it.id }) { item ->
                            val cardContainerColor = if (item.isRead) {
                                MaterialTheme.colorScheme.surface
                            } else {
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                            }

                            Card(
                                colors = CardDefaults.cardColors(containerColor = cardContainerColor),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNotificationClick(item) }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = null,
                                        tint = if (item.isRead) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = if (!item.isRead) FontWeight.Bold else FontWeight.Normal
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = item.message,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = DauTimeUtils.formatDisplayDate(item.createdAt),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

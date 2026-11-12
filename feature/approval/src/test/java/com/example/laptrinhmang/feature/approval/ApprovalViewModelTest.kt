package com.example.laptrinhmang.feature.approval

import com.example.laptrinhmang.core.model.NetworkResult
import com.example.laptrinhmang.feature.approval.data.remote.ApprovalApiService
import com.example.laptrinhmang.feature.approval.data.remote.MockApprovalDataSource
import com.example.laptrinhmang.feature.approval.data.repository.ApprovalRepositoryImpl
import com.example.laptrinhmang.feature.approval.domain.model.ApprovalItem
import com.example.laptrinhmang.feature.approval.domain.model.ApprovalPriority
import com.example.laptrinhmang.feature.approval.domain.model.ApprovalStatus
import com.example.laptrinhmang.feature.approval.domain.usecase.AdjustScheduleUseCase
import com.example.laptrinhmang.feature.approval.domain.usecase.ApprovalRulesUseCases
import com.example.laptrinhmang.feature.approval.domain.usecase.BatchDecideApprovalsUseCase
import com.example.laptrinhmang.feature.approval.domain.usecase.DecideApprovalUseCase
import com.example.laptrinhmang.feature.approval.domain.usecase.GetCampusScheduleUseCase
import com.example.laptrinhmang.feature.approval.domain.usecase.GetPendingApprovalsUseCase
import com.example.laptrinhmang.feature.approval.domain.usecase.LockRoomSlotUseCase
import com.example.laptrinhmang.feature.approval.domain.usecase.RoomCategoryUseCases
import com.example.laptrinhmang.feature.approval.ui.ApprovalTab
import com.example.laptrinhmang.feature.approval.ui.ApprovalViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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
import java.lang.reflect.Proxy

@OptIn(ExperimentalCoroutinesApi::class)
class ApprovalViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockDataSource: MockApprovalDataSource
    private lateinit var repository: ApprovalRepositoryImpl
    private lateinit var decideApprovalUseCase: DecideApprovalUseCase
    private lateinit var batchDecideApprovalsUseCase: BatchDecideApprovalsUseCase
    private lateinit var lockRoomSlotUseCase: LockRoomSlotUseCase
    private lateinit var roomCategoryUseCases: RoomCategoryUseCases

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockDataSource = MockApprovalDataSource()

        // Mock api service proxy
        val dummyApiService = Proxy.newProxyInstance(
            ApprovalApiService::class.java.classLoader,
            arrayOf(ApprovalApiService::class.java)
        ) { _, _, _ -> null } as ApprovalApiService

        repository = ApprovalRepositoryImpl(dummyApiService, mockDataSource)
        decideApprovalUseCase = DecideApprovalUseCase(repository)
        batchDecideApprovalsUseCase = BatchDecideApprovalsUseCase(repository)
        lockRoomSlotUseCase = LockRoomSlotUseCase(repository)
        roomCategoryUseCases = RoomCategoryUseCases(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `reject booking without reason should return error`() = runTest {
        val result = decideApprovalUseCase(
            id = "BK-2026-001",
            isApproved = false,
            reason = "" // Empty reason
        )

        assertTrue(result is NetworkResult.Error)
        assertEquals("Bắt buộc phải nhập lý do khi từ chối đơn đặt phòng!", (result as NetworkResult.Error).apiError.message)
    }

    @Test
    fun `approve booking should succeed and update status to APPROVED`() = runTest {
        val result = decideApprovalUseCase(
            id = "BK-2026-001",
            isApproved = true
        )

        assertTrue(result is NetworkResult.Success)
        val data = (result as NetworkResult.Success).data
        assertEquals(ApprovalStatus.APPROVED, data.status)
    }

    @Test
    fun `batch approve should return report with each item result`() = runTest {
        val ids = listOf("BK-2026-002", "BK-2026-003")
        val result = batchDecideApprovalsUseCase(
            bookingIds = ids,
            isApproved = true
        )

        assertTrue(result is NetworkResult.Success)
        val report = (result as NetworkResult.Success).data
        assertEquals(2, report.total)
        assertEquals(2, report.successCount)
        assertEquals(2, report.results.size)
        assertTrue(report.results.all { it.success })
    }

    @Test
    fun `lock room time slot should return affected bookings without deleting them`() = runTest {
        val result = lockRoomSlotUseCase(
            roomId = "P.302",
            startTime = "2026-11-20T07:00:00+07:00",
            endTime = "2026-11-20T12:00:00+07:00",
            reason = "Bảo trì nguồn điện",
            notifyAffected = true
        )

        assertTrue(result is NetworkResult.Success)
        val lockedResult = (result as NetworkResult.Success).data
        assertEquals("P.302", lockedResult.roomId)
        assertNotNull(lockedResult.affectedBookings)
        // Check that affected bookings are marked as NOT cancelled (không tự xóa)
        lockedResult.affectedBookings.forEach { booking ->
            assertFalse(booking.isCancelled)
        }
    }

    @Test
    fun `create and delete room category should update categories list`() = runTest {
        val createResult = roomCategoryUseCases.createCategory(
            name = "Phòng Studio VR/AR",
            description = "Trang thiết bị kính thực tế ảo hiện đại"
        )
        assertTrue(createResult is NetworkResult.Success)
        val created = (createResult as NetworkResult.Success).data
        assertEquals("Phòng Studio VR/AR", created.name)

        val deleteResult = roomCategoryUseCases.deleteCategory(created.id)
        assertTrue(deleteResult is NetworkResult.Success)
    }
}

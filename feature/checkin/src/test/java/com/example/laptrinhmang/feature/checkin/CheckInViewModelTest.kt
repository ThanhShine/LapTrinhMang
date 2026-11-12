package com.example.laptrinhmang.feature.checkin

import com.example.laptrinhmang.core.model.NetworkResult
import com.example.laptrinhmang.feature.checkin.data.CheckInApi
import com.example.laptrinhmang.feature.checkin.data.CheckInMockDataSource
import com.example.laptrinhmang.feature.checkin.data.CheckInRepositoryImpl
import com.example.laptrinhmang.feature.checkin.domain.model.EquipmentStatus
import com.example.laptrinhmang.feature.checkin.domain.model.IssueSeverity
import com.example.laptrinhmang.feature.checkin.domain.model.IssueStatus
import com.example.laptrinhmang.feature.checkin.domain.model.RoomEquipment
import com.example.laptrinhmang.feature.checkin.domain.model.RoomRating
import com.example.laptrinhmang.feature.checkin.domain.usecase.AssignIssueUseCase
import com.example.laptrinhmang.feature.checkin.domain.usecase.CheckInUseCase
import com.example.laptrinhmang.feature.checkin.domain.usecase.CheckOutUseCase
import com.example.laptrinhmang.feature.checkin.domain.usecase.EquipmentUseCases
import com.example.laptrinhmang.feature.checkin.domain.usecase.GetIssuesUseCase
import com.example.laptrinhmang.feature.checkin.domain.usecase.ReportIssueUseCase
import com.example.laptrinhmang.feature.checkin.domain.usecase.ResolveIssueUseCase
import com.example.laptrinhmang.feature.checkin.domain.usecase.StatisticsUseCases
import com.example.laptrinhmang.feature.checkin.domain.usecase.SubmitRatingUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.lang.reflect.Proxy

@OptIn(ExperimentalCoroutinesApi::class)
class CheckInViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockDataSource: CheckInMockDataSource
    private lateinit var repository: CheckInRepositoryImpl
    private lateinit var checkInUseCase: CheckInUseCase
    private lateinit var checkOutUseCase: CheckOutUseCase
    private lateinit var reportIssueUseCase: ReportIssueUseCase
    private lateinit var assignIssueUseCase: AssignIssueUseCase
    private lateinit var resolveIssueUseCase: ResolveIssueUseCase
    private lateinit var submitRatingUseCase: SubmitRatingUseCase
    private lateinit var equipmentUseCases: EquipmentUseCases
    private lateinit var statisticsUseCases: StatisticsUseCases

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockDataSource = CheckInMockDataSource()

        val dummyApi = Proxy.newProxyInstance(
            CheckInApi::class.java.classLoader,
            arrayOf(CheckInApi::class.java)
        ) { _, _, _ -> null } as CheckInApi

        repository = CheckInRepositoryImpl(dummyApi, mockDataSource)
        checkInUseCase = CheckInUseCase(repository)
        checkOutUseCase = CheckOutUseCase(repository)
        reportIssueUseCase = ReportIssueUseCase(repository)
        assignIssueUseCase = AssignIssueUseCase(repository)
        resolveIssueUseCase = ResolveIssueUseCase(repository)
        submitRatingUseCase = SubmitRatingUseCase(repository)
        equipmentUseCases = EquipmentUseCases(repository)
        statisticsUseCases = StatisticsUseCases(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `scan QR check-in should succeed and return checked-in room info`() = runTest {
        val result = checkInUseCase("DAU-ROOM-302")
        assertTrue(result is NetworkResult.Success)
        val data = (result as NetworkResult.Success).data
        assertEquals("P.302", data.roomCode)
        assertEquals("CHECKED_IN", data.status)
    }

    @Test
    fun `checkout should succeed and calculate usage duration`() = runTest {
        val result = checkOutUseCase(1L, "Trả phòng sau tiết dạy")
        assertTrue(result is NetworkResult.Success)
        val data = (result as NetworkResult.Success).data
        assertEquals("COMPLETED", data.status)
        assertTrue(data.durationMinutes > 0)
    }

    @Test
    fun `report issue without title should return error`() = runTest {
        val result = reportIssueUseCase(
            roomCode = "P.302",
            title = "",
            description = "Chi tiết",
            severity = IssueSeverity.HIGH
        )
        assertTrue(result is NetworkResult.Error)
        assertEquals("Tiêu đề sự cố không được để trống!", (result as NetworkResult.Error).apiError.message)
    }

    @Test
    fun `report issue with valid info should create issue with OPEN status`() = runTest {
        val result = reportIssueUseCase(
            roomCode = "P.302",
            title = "Máy chiếu không lên nguồn",
            description = "Bóng đèn nhấp nháy đỏ",
            severity = IssueSeverity.HIGH,
            images = listOf("https://dau.edu.vn/img.jpg")
        )
        assertTrue(result is NetworkResult.Success)
        val issue = (result as NetworkResult.Success).data
        assertEquals("Máy chiếu không lên nguồn", issue.title)
        assertEquals(IssueStatus.OPEN, issue.status)
        assertEquals(IssueSeverity.HIGH, issue.severity)
    }

    @Test
    fun `assign and resolve issue should update issue status appropriately`() = runTest {
        val assignResult = assignIssueUseCase(
            issueId = 501L,
            technicianId = 10L,
            technicianName = "Võ Kỹ Thuật"
        )
        assertTrue(assignResult is NetworkResult.Success)
        val assigned = (assignResult as NetworkResult.Success).data
        assertEquals(IssueStatus.ASSIGNED, assigned.status)
        assertEquals("Võ Kỹ Thuật", assigned.assignedTo)

        val resolveResult = resolveIssueUseCase(
            issueId = 501L,
            resolutionNote = "Đã thay cầu chì mới",
            acceptedBy = "Quản lý phòng"
        )
        assertTrue(resolveResult is NetworkResult.Success)
        val resolved = (resolveResult as NetworkResult.Success).data
        assertEquals(IssueStatus.RESOLVED, resolved.status)
    }

    @Test
    fun `submit rating should complete successfully`() = runTest {
        val rating = RoomRating(
            bookingId = 1L,
            roomCode = "P.302",
            cleanlinessScore = 5,
            equipmentScore = 4,
            lightingScore = 5,
            overallScore = 5,
            comment = "Phòng rất tốt"
        )
        val result = submitRatingUseCase(rating)
        assertTrue(result is NetworkResult.Success)
    }

    @Test
    fun `equipment CRUD operations should work properly`() = runTest {
        val createResult = equipmentUseCases.createEquipment(
            RoomEquipment(
                id = 0L,
                code = "",
                name = "Máy chiếu Laser Epson",
                category = "Máy chiếu",
                roomCode = "P.304",
                roomName = "Phòng 304",
                status = EquipmentStatus.ACTIVE
            )
        )
        assertTrue(createResult is NetworkResult.Success)
        val created = (createResult as NetworkResult.Success).data
        assertEquals("Máy chiếu Laser Epson", created.name)

        val deleteResult = equipmentUseCases.deleteEquipment(created.id)
        assertTrue(deleteResult is NetworkResult.Success)
    }

    @Test
    fun `get usage and issue statistics should return valid data`() = runTest(testDispatcher) {
        val usageList = mutableListOf<NetworkResult<*>>()
        val usageJob = launch { statisticsUseCases.getUsageStatistics().collect { usageList.add(it) } }
        testScheduler.advanceUntilIdle()
        usageJob.cancel()

        val usageSuccess = usageList.filterIsInstance<NetworkResult.Success<*>>().firstOrNull()
        assertNotNull(usageSuccess)

        val issueList = mutableListOf<NetworkResult<*>>()
        val issueJob = launch { statisticsUseCases.getIssueStatistics().collect { issueList.add(it) } }
        testScheduler.advanceUntilIdle()
        issueJob.cancel()

        val issueSuccess = issueList.filterIsInstance<NetworkResult.Success<*>>().firstOrNull()
        assertNotNull(issueSuccess)
    }
}

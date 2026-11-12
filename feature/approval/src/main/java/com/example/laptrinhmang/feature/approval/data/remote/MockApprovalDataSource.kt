package com.example.laptrinhmang.feature.approval.data.remote

import com.example.laptrinhmang.feature.approval.data.model.AdjustScheduleRequest
import com.example.laptrinhmang.feature.approval.data.model.AffectedBookingDto
import com.example.laptrinhmang.feature.approval.data.model.ApprovalDecisionRequest
import com.example.laptrinhmang.feature.approval.data.model.ApprovalItemDto
import com.example.laptrinhmang.feature.approval.data.model.ApprovalRuleDto
import com.example.laptrinhmang.feature.approval.data.model.BatchApprovalRequest
import com.example.laptrinhmang.feature.approval.data.model.BatchApprovalResponseDto
import com.example.laptrinhmang.feature.approval.data.model.BatchSingleResultDto
import com.example.laptrinhmang.feature.approval.data.model.CampusScheduleResponseDto
import com.example.laptrinhmang.feature.approval.data.model.LockScheduleRequest
import com.example.laptrinhmang.feature.approval.data.model.LockScheduleResponseDto
import com.example.laptrinhmang.feature.approval.data.model.RoomCategoryDto
import com.example.laptrinhmang.feature.approval.data.model.RoomScheduleDto
import com.example.laptrinhmang.feature.approval.data.model.RoomTimeSlotDto
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockApprovalDataSource @Inject constructor() {

    private val pendingList = mutableListOf(
        ApprovalItemDto(
            id = "BK-2026-001",
            roomId = "P.302",
            roomName = "Phòng Lý thuyết 302 - Nhà A",
            capacity = 60,
            requesterId = "2051220001",
            requesterName = "Nguyễn Văn Học",
            requesterRole = "STUDENT",
            departmentName = "Khoa Công nghệ thông tin",
            purpose = "Học bù môn Lập trình mạng",
            attendeesCount = 45,
            startTime = "2026-11-20T08:00:00+07:00",
            endTime = "2026-11-20T11:30:00+07:00",
            status = "PENDING",
            priority = "HIGH",
            requiredEquipment = listOf("Máy chiếu", "Micro", "Điều hòa"),
            createdAt = "2026-11-12T07:30:00+07:00"
        ),
        ApprovalItemDto(
            id = "BK-2026-002",
            roomId = "LAB-501",
            roomName = "Phòng Thực hành Mạng 501 - Nhà C",
            capacity = 45,
            requesterId = "giangvien",
            requesterName = "Trần Thị Giáo",
            requesterRole = "LECTURER",
            departmentName = "Khoa Công nghệ thông tin",
            purpose = "Thi kết thúc học phần An toàn thông tin",
            attendeesCount = 40,
            startTime = "2026-11-20T13:30:00+07:00",
            endTime = "2026-11-20T16:45:00+07:00",
            status = "PENDING",
            priority = "URGENT",
            requiredEquipment = listOf("Máy tính PC", "Hệ thống Switch mạng", "Máy chiếu"),
            createdAt = "2026-11-12T08:00:00+07:00"
        ),
        ApprovalItemDto(
            id = "BK-2026-003",
            roomId = "XUONG-204",
            roomName = "Xưởng Đồ án Kiến trúc 204 - Nhà B",
            capacity = 35,
            requesterId = "2051220088",
            requesterName = "Lê Hoàng Nam",
            requesterRole = "STUDENT",
            departmentName = "Khoa Kiến trúc",
            purpose = "Bảo vệ đồ án tốt nghiệp KTS",
            attendeesCount = 30,
            startTime = "2026-11-21T08:00:00+07:00",
            endTime = "2026-11-21T11:30:00+07:00",
            status = "PENDING",
            priority = "NORMAL",
            requiredEquipment = listOf("Bàn vẽ chuyên dụng", "Máy chiếu Laser"),
            createdAt = "2026-11-12T09:15:00+07:00"
        ),
        ApprovalItemDto(
            id = "BK-2026-004",
            roomId = "HT-LON",
            roomName = "Hội trường lớn Tầng 2 - Nhà Đa năng",
            capacity = 300,
            requesterId = "doanthanhnien",
            requesterName = "Đoàn Thanh Niên DAU",
            requesterRole = "TRAINING_OFFICER",
            departmentName = "Đoàn Thanh niên",
            purpose = "Tọa đàm Sinh viên khởi nghiệp ĐH Kiến Trúc",
            attendeesCount = 250,
            startTime = "2026-11-22T14:00:00+07:00",
            endTime = "2026-11-22T17:30:00+07:00",
            status = "PENDING",
            priority = "HIGH",
            requiredEquipment = listOf("Âm thanh sân khấu", "2 Micro không dây", "Màn hình LED"),
            createdAt = "2026-11-12T10:00:00+07:00"
        )
    )

    private val schedulesList = mutableListOf(
        RoomScheduleDto(
            roomId = "P.302",
            roomName = "Phòng Lý thuyết 302",
            building = "Nhà A",
            floor = 3,
            timeSlots = mutableListOf(
                RoomTimeSlotDto(
                    bookingId = "BK-2026-090",
                    purpose = "Giảng dạy Kiến trúc máy tính",
                    requesterName = "ThS. Phạm Hùng",
                    startTime = "2026-11-20T07:30:00+07:00",
                    endTime = "2026-11-20T11:00:00+07:00",
                    status = "APPROVED",
                    isLocked = false
                ),
                RoomTimeSlotDto(
                    bookingId = null,
                    purpose = "Tạm khóa kiểm tra hệ thống điện",
                    requesterName = "Tổ kỹ thuật",
                    startTime = "2026-11-20T11:30:00+07:00",
                    endTime = "2026-11-20T13:00:00+07:00",
                    status = "LOCKED",
                    isLocked = true,
                    lockReason = "Bảo trì nguồn điện tổng"
                )
            )
        ),
        RoomScheduleDto(
            roomId = "P.304",
            roomName = "Phòng Lý thuyết 304",
            building = "Nhà A",
            floor = 3,
            timeSlots = mutableListOf(
                RoomTimeSlotDto(
                    bookingId = "BK-2026-088",
                    purpose = "Sinh hoạt Câu lạc bộ Lập trình",
                    requesterName = "CLB CNTT DAU",
                    startTime = "2026-11-20T14:00:00+07:00",
                    endTime = "2026-11-20T17:00:00+07:00",
                    status = "APPROVED",
                    isLocked = false
                )
            )
        ),
        RoomScheduleDto(
            roomId = "LAB-501",
            roomName = "Phòng Thực hành Mạng 501",
            building = "Nhà C",
            floor = 5,
            timeSlots = mutableListOf(
                RoomTimeSlotDto(
                    bookingId = "BK-2026-077",
                    purpose = "Thực hành Quản trị mạng Cisco",
                    requesterName = "Trần Thị Giáo",
                    startTime = "2026-11-20T07:30:00+07:00",
                    endTime = "2026-11-20T11:30:00+07:00",
                    status = "APPROVED",
                    isLocked = false
                )
            )
        )
    )

    private val rulesList = mutableListOf(
        ApprovalRuleDto(
            id = "RULE-01",
            name = "Tự động duyệt phòng học cho Giảng viên",
            description = "Tự động phê duyệt đơn của Giảng viên nếu phòng còn trống và dưới 60 chỗ",
            isEnabled = true,
            targetRole = "LECTURER",
            maxCapacity = 60,
            autoApprove = true,
            escalationHours = 24
        ),
        ApprovalRuleDto(
            id = "RULE-02",
            name = "Quy trình kiểm duyệt Sinh viên mượn Lab",
            description = "Đơn mượn phòng Lab của Sinh viên cần cán bộ Phê duyệt hoặc P.Đào tạo duyệt",
            isEnabled = true,
            targetRole = "STUDENT",
            maxCapacity = 45,
            autoApprove = false,
            escalationHours = 12
        ),
        ApprovalRuleDto(
            id = "RULE-03",
            name = "Quy định Hội trường lớn",
            description = "Tất cả yêu cầu mượn Hội trường lớn phải qua Ban Giám Hiệu phê duyệt",
            isEnabled = true,
            targetRole = "ALL",
            maxCapacity = 300,
            autoApprove = false,
            escalationHours = 48
        )
    )

    private val roomCategories = mutableListOf(
        RoomCategoryDto(
            id = "CAT-LT",
            name = "Phòng Lý thuyết",
            description = "Phòng học trang bị bảng từ, micro, máy chiếu tiêu chuẩn",
            roomCount = 28
        ),
        RoomCategoryDto(
            id = "CAT-LAB-IT",
            name = "Phòng Thực hành Máy tính (CNTT)",
            description = "Phòng thực hành trang bị 40-50 máy PC và switch mạng Cisco",
            roomCount = 8
        ),
        RoomCategoryDto(
            id = "CAT-XUONG-KT",
            name = "Xưởng Đồ án Kiến trúc",
            description = "Bàn vẽ tiêu chuẩn kiến trúc, không gian làm mô hình rộng rãi",
            roomCount = 12
        ),
        RoomCategoryDto(
            id = "CAT-HOI-TRUONG",
            name = "Hội trường lớn",
            description = "Sức chứa 200-500 người, hệ thống âm thanh ánh sáng sân khấu",
            roomCount = 3
        )
    )

    fun getPendingApprovals(search: String?): List<ApprovalItemDto> {
        return if (search.isNullOrBlank()) {
            pendingList.filter { it.status == "PENDING" }
        } else {
            pendingList.filter {
                it.status == "PENDING" &&
                (it.requesterName.contains(search, ignoreCase = true) ||
                 it.roomName.contains(search, ignoreCase = true) ||
                 it.purpose.contains(search, ignoreCase = true) ||
                 it.id.contains(search, ignoreCase = true))
            }
        }
    }

    fun decideApproval(id: String, request: ApprovalDecisionRequest): ApprovalItemDto {
        val index = pendingList.indexOfFirst { it.id == id }
        if (index == -1) throw IllegalArgumentException("Không tìm thấy đơn $id")

        if (request.action.equals("REJECT", ignoreCase = true) && request.reason.isNullOrBlank()) {
            throw IllegalArgumentException("Bắt buộc phải nhập lý do khi từ chối đơn đặt phòng!")
        }

        val current = pendingList[index]
        val newStatus = if (request.action.equals("APPROVE", ignoreCase = true)) "APPROVED" else "REJECTED"
        val updated = current.copy(
            status = newStatus,
            rejectionReason = request.reason,
            approvedBy = "Nguyễn Quản Trị"
        )
        pendingList[index] = updated
        return updated
    }

    fun batchDecide(request: BatchApprovalRequest): BatchApprovalResponseDto {
        val results = mutableListOf<BatchSingleResultDto>()
        var successCount = 0
        var failureCount = 0

        request.bookingIds.forEach { bookingId ->
            val item = pendingList.find { it.id == bookingId }
            if (item == null) {
                failureCount++
                results.add(
                    BatchSingleResultDto(
                        bookingId = bookingId,
                        success = false,
                        status = "PENDING",
                        message = "Không tìm thấy mã đơn"
                    )
                )
            } else {
                val isConflict = bookingId == "BK-2026-999" // Mock conflict test
                if (isConflict) {
                    failureCount++
                    results.add(
                        BatchSingleResultDto(
                            bookingId = bookingId,
                            success = false,
                            status = "PENDING",
                            message = "Xung đột lịch với đơn khác đã duyệt"
                        )
                    )
                } else {
                    val newStatus = if (request.action.equals("APPROVE", ignoreCase = true)) "APPROVED" else "REJECTED"
                    val index = pendingList.indexOf(item)
                    pendingList[index] = item.copy(status = newStatus, rejectionReason = request.reason)
                    successCount++
                    results.add(
                        BatchSingleResultDto(
                            bookingId = bookingId,
                            success = true,
                            status = newStatus,
                            message = if (newStatus == "APPROVED") "Phê duyệt thành công" else "Từ chối thành công"
                        )
                    )
                }
            }
        }

        return BatchApprovalResponseDto(
            total = request.bookingIds.size,
            successCount = successCount,
            failureCount = failureCount,
            results = results
        )
    }

    fun getCampusSchedule(date: String, building: String?): CampusScheduleResponseDto {
        val filtered = if (building.isNullOrBlank()) {
            schedulesList
        } else {
            schedulesList.filter { it.building.contains(building, ignoreCase = true) }
        }
        return CampusScheduleResponseDto(date = date, schedules = filtered)
    }

    fun adjustSchedule(bookingId: String, request: AdjustScheduleRequest): ApprovalItemDto {
        // Cập nhật lịch vào schedulesList
        schedulesList.forEach { schedule ->
            val slot = schedule.timeSlots.find { it.bookingId == bookingId }
            if (slot != null) {
                // Di chuyển sang phòng mới nếu có
                val mutableSlots = schedule.timeSlots as? MutableList
                mutableSlots?.remove(slot)
            }
        }

        val targetSchedule = schedulesList.find { it.roomId == request.newRoomId }
            ?: schedulesList.firstOrNull()

        (targetSchedule?.timeSlots as? MutableList)?.add(
            RoomTimeSlotDto(
                bookingId = bookingId,
                purpose = "Đã điều chuyển: ${request.adjustmentReason}",
                requesterName = "Người đặt điều chuyển",
                startTime = request.newStartTime,
                endTime = request.newEndTime,
                status = "APPROVED",
                isLocked = false
            )
        )

        return ApprovalItemDto(
            id = bookingId,
            roomId = request.newRoomId,
            roomName = targetSchedule?.roomName ?: request.newRoomId,
            capacity = 50,
            requesterId = "adjusted_user",
            requesterName = "Đã điều chỉnh",
            requesterRole = "LECTURER",
            departmentName = "Phòng Đào tạo",
            purpose = request.adjustmentReason,
            attendeesCount = 30,
            startTime = request.newStartTime,
            endTime = request.newEndTime,
            status = "APPROVED"
        )
    }

    fun lockRoomSlot(roomId: String, request: LockScheduleRequest): LockScheduleResponseDto {
        // Tìm các đơn bị ảnh hưởng trong phòng này
        val affected = mutableListOf<AffectedBookingDto>()
        val roomSchedule = schedulesList.find { it.roomId == roomId }

        roomSchedule?.timeSlots?.forEach { slot ->
            if (slot.bookingId != null && !slot.isLocked!!) {
                affected.add(
                    AffectedBookingDto(
                        bookingId = slot.bookingId,
                        requesterName = slot.requesterName ?: "Người đặt",
                        requesterPhone = "0905123456",
                        purpose = slot.purpose,
                        status = slot.status,
                        isCancelled = false // KHÔNG tự xóa lịch đã duyệt!
                    )
                )
            }
        }

        // Thêm slot khóa vào lịch
        (roomSchedule?.timeSlots as? MutableList)?.add(
            RoomTimeSlotDto(
                bookingId = null,
                purpose = "Tạm khóa: ${request.reason}",
                requesterName = "Ban Quản lý",
                startTime = request.startTime,
                endTime = request.endTime,
                status = "LOCKED",
                isLocked = true,
                lockReason = request.reason
            )
        )

        return LockScheduleResponseDto(
            lockId = "LOCK-${UUID.randomUUID().toString().take(6).uppercase()}",
            roomId = roomId,
            startTime = request.startTime,
            endTime = request.endTime,
            reason = request.reason,
            affectedBookings = affected
        )
    }

    fun getRules(): List<ApprovalRuleDto> = rulesList

    fun updateRule(id: String, updated: ApprovalRuleDto): ApprovalRuleDto {
        val index = rulesList.indexOfFirst { it.id == id }
        if (index != -1) {
            rulesList[index] = updated
        }
        return updated
    }

    fun getCategories(): List<RoomCategoryDto> = roomCategories

    fun createCategory(cat: RoomCategoryDto): RoomCategoryDto {
        val newCat = cat.copy(id = "CAT-${UUID.randomUUID().toString().take(4).uppercase()}")
        roomCategories.add(newCat)
        return newCat
    }

    fun updateCategory(id: String, cat: RoomCategoryDto): RoomCategoryDto {
        val index = roomCategories.indexOfFirst { it.id == id }
        if (index != -1) {
            roomCategories[index] = cat.copy(id = id)
        }
        return cat
    }

    fun deleteCategory(id: String) {
        roomCategories.removeAll { it.id == id }
    }
}

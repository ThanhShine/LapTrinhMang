package com.example.laptrinhmang.core.model

enum class Permission(val code: String, val description: String) {
    ROOM_VIEW("ROOM_VIEW", "Xem danh sách và thông tin phòng học"),
    BOOKING_CREATE("BOOKING_CREATE", "Tạo đơn đặt phòng"),
    BOOKING_EDIT_OWN("BOOKING_EDIT_OWN", "Sửa đơn đặt phòng của chính mình"),
    BOOKING_CANCEL_OWN("BOOKING_CANCEL_OWN", "Hủy đơn đặt phòng của chính mình"),
    BOOKING_APPROVE("BOOKING_APPROVE", "Phê duyệt / Từ chối đơn đặt phòng"),
    BOOKING_PROPOSE("BOOKING_PROPOSE", "Đề xuất phòng thay thế"),
    ROOM_LOCK("ROOM_LOCK", "Khóa / Mở khóa phòng học"),
    ISSUE_ASSIGN("ISSUE_ASSIGN", "Phân công xử lý sự cố"),
    ISSUE_UPDATE("ISSUE_UPDATE", "Cập nhật trạng thái sự cố"),
    ISSUE_ACCEPT("ISSUE_ACCEPT", "Nghiệm thu sự cố"),
    REPORT_EXPORT("REPORT_EXPORT", "Xuất báo cáo thống kê"),
    AUDIT_VIEW("AUDIT_VIEW", "Xem lịch sử thao tác hệ thống");

    companion object {
        fun fromCode(code: String?): Permission? {
            return entries.find { it.code.equals(code, ignoreCase = true) }
        }

        fun getDefaultPermissionsForRole(role: Role): List<Permission> {
            return when (role) {
                Role.ADMIN -> Permission.entries.toList()
                Role.ROOM_MANAGER -> listOf(
                    ROOM_VIEW, ROOM_LOCK, ISSUE_ASSIGN, ISSUE_UPDATE, ISSUE_ACCEPT, REPORT_EXPORT
                )
                Role.TRAINING_OFFICER -> listOf(
                    ROOM_VIEW, BOOKING_CREATE, BOOKING_EDIT_OWN, BOOKING_CANCEL_OWN, BOOKING_APPROVE, BOOKING_PROPOSE
                )
                Role.LECTURER -> listOf(
                    ROOM_VIEW, BOOKING_CREATE, BOOKING_EDIT_OWN, BOOKING_CANCEL_OWN
                )
                Role.STUDENT -> listOf(
                    ROOM_VIEW, BOOKING_CREATE, BOOKING_EDIT_OWN, BOOKING_CANCEL_OWN
                )
                Role.DEAN -> listOf(
                    ROOM_VIEW, BOOKING_APPROVE, REPORT_EXPORT
                )
                Role.APPROVER -> listOf(
                    ROOM_VIEW, BOOKING_APPROVE, BOOKING_PROPOSE
                )
                Role.TECHNICIAN -> listOf(
                    ROOM_VIEW, ISSUE_UPDATE
                )
                Role.EXAM_OFFICE -> listOf(
                    ROOM_VIEW, BOOKING_CREATE, BOOKING_EDIT_OWN, BOOKING_CANCEL_OWN
                )
            }
        }
    }
}

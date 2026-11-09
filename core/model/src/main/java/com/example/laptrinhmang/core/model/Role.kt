package com.example.laptrinhmang.core.model

enum class Role(val displayName: String) {
    ADMIN("Quản trị hệ thống"),
    ROOM_MANAGER("Quản lý phòng"),
    TRAINING_OFFICER("Cán bộ Phòng Đào tạo"),
    LECTURER("Giảng viên"),
    STUDENT("Sinh viên"),
    DEAN("Trưởng khoa"),
    APPROVER("Người phê duyệt"),
    TECHNICIAN("Kỹ thuật viên"),
    EXAM_OFFICE("Cán bộ Khảo thí");

    companion object {
        fun fromName(name: String?): Role {
            return entries.find { it.name.equals(name, ignoreCase = true) } ?: STUDENT
        }
    }
}

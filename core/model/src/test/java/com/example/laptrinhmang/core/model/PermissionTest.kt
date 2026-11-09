package com.example.laptrinhmang.core.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PermissionTest {

    @Test
    fun admin_has_all_permissions() {
        val adminUser = User(
            id = 1,
            username = "admin",
            fullName = "Admin Nguyễn",
            email = "admin@dau.edu.vn",
            role = Role.ADMIN,
            permissions = emptyList()
        )

        assertTrue(adminUser.hasPermission(Permission.ROOM_VIEW.code))
        assertTrue(adminUser.hasPermission(Permission.BOOKING_APPROVE.code))
        assertTrue(adminUser.hasPermission(Permission.ROOM_LOCK.code))
        assertTrue(adminUser.hasPermission(Permission.AUDIT_VIEW.code))
    }

    @Test
    fun student_has_only_assigned_permissions() {
        val studentUser = User(
            id = 2,
            username = "2051220001",
            fullName = "Sinh Viên A",
            email = "sv@dau.edu.vn",
            role = Role.STUDENT,
            permissions = listOf("ROOM_VIEW", "BOOKING_CREATE")
        )

        assertTrue(studentUser.hasPermission("ROOM_VIEW"))
        assertTrue(studentUser.hasPermission("BOOKING_CREATE"))
        assertFalse(studentUser.hasPermission("BOOKING_APPROVE"))
        assertFalse(studentUser.hasPermission("ROOM_LOCK"))
    }

    @Test
    fun default_permissions_for_roles_are_correct() {
        val trainingOfficerPerms = Permission.getDefaultPermissionsForRole(Role.TRAINING_OFFICER)
        assertTrue(trainingOfficerPerms.contains(Permission.BOOKING_CREATE))
        assertTrue(trainingOfficerPerms.contains(Permission.BOOKING_APPROVE))
    }
}

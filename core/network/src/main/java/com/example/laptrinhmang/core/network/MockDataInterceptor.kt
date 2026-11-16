package com.example.laptrinhmang.core.network

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockDataInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!BuildConfig.USE_MOCK_DATA) {
            return chain.proceed(chain.request())
        }

        val request = chain.request()
        val path = request.url.encodedPath
        val method = request.method

        val jsonResponse = when {
            path.endsWith("/auth/login") && method == "POST" -> getMockLoginResponse(request)
            path.contains("/auth/forgot-password") -> getMockSuccessMessage("Vui lòng liên hệ Quản trị viên để được cấp lại mật khẩu.")
            path.endsWith("/account/profile") && method == "GET" -> getMockProfileResponse()
            path.endsWith("/account/profile") && method == "PUT" -> getMockSuccessMessage("Cập nhật hồ sơ thành công.")
            path.endsWith("/account/change-password") && method == "PUT" -> getMockSuccessMessage("Đổi mật khẩu thành công.")
            path.endsWith("/notifications") && method == "GET" -> getMockNotificationsResponse()
            path.contains("/notifications/") && path.endsWith("/read") -> getMockSuccessMessage("Đã đánh dấu là đã đọc.")
            path.endsWith("/notifications/read-all") -> getMockSuccessMessage("Đã đánh dấu đọc tất cả.")
            path.endsWith("/admin/users/import-csv") && method == "POST" -> getMockImportCsvResponse()
            path.endsWith("/admin/users") && method == "GET" -> getMockAdminUsersResponse()
            path.endsWith("/admin/users") && method == "POST" -> getMockCreateUserResponse()
            path.contains("/admin/users/") && path.endsWith("/reset-password") -> getMockResetPasswordResponse()
            path.contains("/admin/users/") && method == "PUT" -> getMockSuccessMessage("Cập nhật thành công.")
            path.endsWith("/admin/roles") -> getMockRolesResponse()
            path.endsWith("/admin/departments") -> getMockDepartmentsResponse()
            path.endsWith("/admin/audit-logs") -> getMockAuditLogsResponse()
            else -> null
        }

        if (jsonResponse != null) {
            return Response.Builder()
                .code(200)
                .message("OK")
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .body(jsonResponse.toResponseBody("application/json".toMediaType()))
                .build()
        }

        return chain.proceed(request)
    }

    private fun getMockLoginResponse(request: okhttp3.Request): String {
        val bodyString = runCatching {
            val buffer = okio.Buffer()
            request.body?.writeTo(buffer)
            buffer.readUtf8()
        }.getOrDefault("")

        val mustChangePass = bodyString.contains("newuser")

        val (email, role, permissions, fullName) = when {
            bodyString.contains("admin") -> Quadruple(
                "admin@dau.edu.vn", "ADMIN",
                listOf("ROOM_VIEW", "BOOKING_CREATE", "BOOKING_EDIT_OWN", "BOOKING_CANCEL_OWN", "BOOKING_APPROVE", "BOOKING_PROPOSE", "ROOM_LOCK", "ISSUE_ASSIGN", "ISSUE_UPDATE", "ISSUE_ACCEPT", "REPORT_EXPORT", "AUDIT_VIEW"),
                "Nguyễn Quản Trị (ADMIN)"
            )
            bodyString.contains("quanly") -> Quadruple(
                "quanly@dau.edu.vn", "ROOM_MANAGER",
                listOf("ROOM_VIEW", "ROOM_LOCK", "ISSUE_ASSIGN", "ISSUE_UPDATE", "ISSUE_ACCEPT", "REPORT_EXPORT"),
                "Lê Văn Quản (Quản Lý Phòng)"
            )
            bodyString.contains("giangvien") -> Quadruple(
                "giangvien@dau.edu.vn", "LECTURER",
                listOf("ROOM_VIEW", "BOOKING_CREATE", "BOOKING_EDIT_OWN", "BOOKING_CANCEL_OWN"),
                "Trần Thị Giáo (Giảng Viên)"
            )
            else -> Quadruple(
                "sv.2051220001@dau.edu.vn", "STUDENT",
                listOf("ROOM_VIEW", "BOOKING_CREATE", "BOOKING_EDIT_OWN", "BOOKING_CANCEL_OWN"),
                "Nguyễn Văn Học (Sinh Viên)"
            )
        }

        val permsJson = permissions.joinToString(",") { "\"$it\"" }

        return """
        {
          "status": 200,
          "message": "Đăng nhập thành công",
          "data": {
            "access_token": "mock_jwt_token_$email",
            "refresh_token": "mock_refresh_token_$email",
            "expires_in": 86400,
            "user": {
              "id": 1,
              "username": "user123",
              "full_name": "$fullName",
              "email": "$email",
              "phone": "0905123456",
              "role": "$role",
              "permissions": [$permsJson],
              "department_id": 10,
              "department_name": "Khoa Công nghệ thông tin",
              "avatar_url": "https://dau.edu.vn/uploads/avatars/user1.jpg",
              "status": "ACTIVE",
              "must_change_password": $mustChangePass
            }
          }
        }
        """.trimIndent()
    }

    private fun getMockCreateUserResponse(): String {
        return """
        {
          "status": 201,
          "message": "Tạo tài khoản thành công",
          "data": {
            "user_id": 99,
            "temporary_password": "TempRandomPass88"
          }
        }
        """.trimIndent()
    }

    private fun getMockResetPasswordResponse(): String {
        return """
        {
          "status": 200,
          "message": "Đặt lại mật khẩu thành công",
          "data": {
            "temporary_password": "NewResetPass777"
          }
        }
        """.trimIndent()
    }

    private fun getMockImportCsvResponse(): String {
        return """
        {
          "status": 200,
          "message": "Nhập dữ liệu CSV hoàn tất",
          "data": {
            "total_rows": 2,
            "success_count": 2,
            "failure_count": 0,
            "results": [
              {
                "line": 1,
                "email": "sv.2051220091@dau.edu.vn",
                "status": "SUCCESS",
                "temporary_password": "PassRandom123"
              },
              {
                "line": 2,
                "email": "gv.tranvan@dau.edu.vn",
                "status": "SUCCESS",
                "temporary_password": "PassRandom456"
              }
            ]
          }
        }
        """.trimIndent()
    }

    private fun getMockProfileResponse(): String {
        return """
        {
          "status": 200,
          "data": {
            "id": 1,
            "username": "2051220001",
            "full_name": "Nguyễn Văn A",
            "email": "sv.2051220001@dau.edu.vn",
            "phone": "0905123456",
            "role": "STUDENT",
            "permissions": ["ROOM_VIEW", "BOOKING_CREATE", "BOOKING_EDIT_OWN", "BOOKING_CANCEL_OWN"],
            "department_id": 10,
            "department_name": "Khoa Công nghệ thông tin",
            "avatar_url": "https://dau.edu.vn/uploads/avatars/user1.jpg",
            "status": "ACTIVE",
            "must_change_password": false
          }
        }
        """.trimIndent()
    }

    private fun getMockNotificationsResponse(): String {
        return """
        {
          "status": 200,
          "data": {
            "items": [
              {
                "id": 101,
                "title": "Phê duyệt phòng học",
                "message": "Yêu cầu mượn phòng P.302 cho lớp LTM đã được phê duyệt.",
                "is_read": false,
                "target_screen": "BOOKING_DETAIL",
                "target_id": "BK-2026-001",
                "created_at": "2026-03-30T09:15:00Z"
              }
            ],
            "pagination": {
              "page": 1,
              "limit": 20,
              "total_items": 1,
              "total_pages": 1
            }
          }
        }
        """.trimIndent()
    }

    private fun getMockAdminUsersResponse(): String {
        return """
        {
          "status": 200,
          "data": {
            "items": [
              {
                "id": 1,
                "username": "admin",
                "full_name": "Nguyễn Quản Trị",
                "email": "admin@dau.edu.vn",
                "role": "ADMIN",
                "permissions": ["ROOM_VIEW", "BOOKING_CREATE", "BOOKING_EDIT_OWN", "BOOKING_CANCEL_OWN", "BOOKING_APPROVE", "BOOKING_PROPOSE", "ROOM_LOCK", "ISSUE_ASSIGN", "ISSUE_UPDATE", "ISSUE_ACCEPT", "REPORT_EXPORT", "AUDIT_VIEW"],
                "department_id": 1,
                "department_name": "Trường ĐH Kiến trúc Đà Nẵng",
                "status": "ACTIVE",
                "must_change_password": false
              },
              {
                "id": 2,
                "username": "quanly",
                "full_name": "Lê Văn Quản",
                "email": "quanly@dau.edu.vn",
                "role": "ROOM_MANAGER",
                "permissions": ["ROOM_VIEW", "ROOM_LOCK", "ISSUE_ASSIGN", "ISSUE_UPDATE", "ISSUE_ACCEPT", "REPORT_EXPORT"],
                "department_id": 2,
                "department_name": "Phòng Cơ sở vật chất",
                "status": "ACTIVE",
                "must_change_password": false
              },
              {
                "id": 3,
                "username": "giangvien",
                "full_name": "Trần Thị Giáo",
                "email": "giangvien@dau.edu.vn",
                "role": "LECTURER",
                "permissions": ["ROOM_VIEW", "BOOKING_CREATE", "BOOKING_EDIT_OWN", "BOOKING_CANCEL_OWN"],
                "department_id": 10,
                "department_name": "Khoa CNTT",
                "status": "ACTIVE",
                "must_change_password": false
              },
              {
                "id": 4,
                "username": "2051220001",
                "full_name": "Nguyễn Văn Học",
                "email": "sv.2051220001@dau.edu.vn",
                "role": "STUDENT",
                "permissions": ["ROOM_VIEW", "BOOKING_CREATE", "BOOKING_EDIT_OWN", "BOOKING_CANCEL_OWN"],
                "department_id": 10,
                "department_name": "Khoa CNTT",
                "status": "ACTIVE",
                "must_change_password": false
              }
            ],
            "pagination": {
              "page": 1,
              "limit": 20,
              "total_items": 4,
              "total_pages": 1
            }
          }
        }
        """.trimIndent()
    }

    private fun getMockRolesResponse(): String {
        return """
        {
          "status": 200,
          "data": [
            {
              "id": "ADMIN",
              "name": "Quản trị hệ thống",
              "permissions": ["ROOM_VIEW", "BOOKING_CREATE", "BOOKING_EDIT_OWN", "BOOKING_CANCEL_OWN", "BOOKING_APPROVE", "BOOKING_PROPOSE", "ROOM_LOCK", "ISSUE_ASSIGN", "ISSUE_UPDATE", "ISSUE_ACCEPT", "REPORT_EXPORT", "AUDIT_VIEW"]
            },
            {
              "id": "ROOM_MANAGER",
              "name": "Quản lý phòng",
              "permissions": ["ROOM_VIEW", "ROOM_LOCK", "ISSUE_ASSIGN", "ISSUE_UPDATE", "ISSUE_ACCEPT", "REPORT_EXPORT"]
            },
            {
              "id": "LECTURER",
              "name": "Giảng viên",
              "permissions": ["ROOM_VIEW", "BOOKING_CREATE", "BOOKING_EDIT_OWN", "BOOKING_CANCEL_OWN"]
            },
            {
              "id": "STUDENT",
              "name": "Sinh viên",
              "permissions": ["ROOM_VIEW", "BOOKING_CREATE", "BOOKING_EDIT_OWN", "BOOKING_CANCEL_OWN"]
            }
          ]
        }
        """.trimIndent()
    }

    private fun getMockDepartmentsResponse(): String {
        return """
        {
          "status": 200,
          "data": [
            {
              "id": 1,
              "name": "Trường ĐH Kiến trúc Đà Nẵng",
              "parent_id": null,
              "status": "ACTIVE",
              "children": [
                {
                  "id": 2,
                  "name": "Phòng Cơ sở vật chất",
                  "parent_id": 1,
                  "status": "ACTIVE"
                },
                {
                  "id": 10,
                  "name": "Khoa Công nghệ thông tin",
                  "parent_id": 1,
                  "status": "ACTIVE"
                }
              ]
            }
          ]
        }
        """.trimIndent()
    }

    private fun getMockAuditLogsResponse(): String {
        return """
        {
          "status": 200,
          "data": {
            "items": [
              {
                "id": 5001,
                "user_id": 1,
                "user_name": "Nguyễn Quản Trị (ADMIN)",
                "action": "CREATE_USER",
                "description": "Tạo tài khoản nguyenvanb@dau.edu.vn kèm mật khẩu tạm",
                "ip_address": "172.16.1.45",
                "created_at": "2026-03-30T10:00:00Z"
              }
            ],
            "pagination": {
              "page": 1,
              "limit": 20,
              "total_items": 1,
              "total_pages": 1
            }
          }
        }
        """.trimIndent()
    }

    private fun getMockSuccessMessage(message: String): String {
        return """
        {
          "status": 200,
          "message": "$message"
        }
        """.trimIndent()
    }

    private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
}

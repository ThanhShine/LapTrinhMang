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
            path.endsWith("/auth/register") && method == "POST" -> getMockRegisterResponse()
            path.endsWith("/auth/activate") && method == "POST" -> getMockActivateResponse()
            path.contains("/auth/forgot-password") -> getMockSuccessMessage("Mã OTP đã được gửi về email.")
            path.endsWith("/account/profile") && method == "GET" -> getMockProfileResponse()
            path.endsWith("/account/profile") && method == "PUT" -> getMockSuccessMessage("Cập nhật hồ sơ thành công.")
            path.endsWith("/account/change-password") && method == "PUT" -> getMockSuccessMessage("Đổi mật khẩu thành công.")
            path.endsWith("/notifications") && method == "GET" -> getMockNotificationsResponse()
            path.contains("/notifications/") && path.endsWith("/read") -> getMockSuccessMessage("Đã đánh dấu là đã đọc.")
            path.endsWith("/notifications/read-all") -> getMockSuccessMessage("Đã đánh dấu đọc tất cả.")
            path.endsWith("/admin/users") && method == "GET" -> getMockAdminUsersResponse()
            path.endsWith("/admin/users") && method == "POST" -> getMockSuccessMessage("Thêm người dùng thành công.")
            path.contains("/admin/users/") && method == "PUT" -> getMockSuccessMessage("Cập nhật thành công.")
            path.contains("/admin/users/") && method == "POST" -> getMockSuccessMessage("Đặt lại mật khẩu thành công.")
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

        val (username, role, permissions, fullName) = when {
            bodyString.contains("admin") -> Quadruple(
                "admin", "ADMIN",
                listOf("ROOM_VIEW", "BOOKING_CREATE", "BOOKING_EDIT_OWN", "BOOKING_CANCEL_OWN", "BOOKING_APPROVE", "BOOKING_PROPOSE", "ROOM_LOCK", "ISSUE_ASSIGN", "ISSUE_UPDATE", "ISSUE_ACCEPT", "REPORT_EXPORT", "AUDIT_VIEW"),
                "Nguyễn Quản Trị (ADMIN)"
            )
            bodyString.contains("manager") || bodyString.contains("quanly") -> Quadruple(
                "quanly", "ROOM_MANAGER",
                listOf("ROOM_VIEW", "ROOM_LOCK", "ISSUE_ASSIGN", "ISSUE_UPDATE", "ISSUE_ACCEPT", "REPORT_EXPORT"),
                "Lê Văn Quản (Quản Lý Phòng)"
            )
            bodyString.contains("lecturer") || bodyString.contains("giangvien") -> Quadruple(
                "giangvien", "LECTURER",
                listOf("ROOM_VIEW", "BOOKING_CREATE", "BOOKING_EDIT_OWN", "BOOKING_CANCEL_OWN"),
                "Trần Thị Giáo (Giảng Viên)"
            )
            else -> Quadruple(
                "2051220001", "STUDENT",
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
            "access_token": "mock_jwt_token_$username",
            "refresh_token": "mock_refresh_token_$username",
            "expires_in": 86400,
            "user": {
              "id": 1,
              "username": "$username",
              "full_name": "$fullName",
              "email": "$username@dau.edu.vn",
              "phone": "0905123456",
              "role": "$role",
              "permissions": [$permsJson],
              "department_id": 10,
              "department_name": "Khoa Công nghệ thông tin",
              "avatar_url": "https://dau.edu.vn/uploads/avatars/user1.jpg",
              "status": "ACTIVE"
            }
          }
        }
        """.trimIndent()
    }

    private fun getMockRegisterResponse(): String {
        return """
        {
          "status": 200,
          "message": "Mã OTP đã được gửi về email trường.",
          "data": {
            "email": "user@dau.edu.vn",
            "expires_in_seconds": 300,
            "resend_in_seconds": 60
          }
        }
        """.trimIndent()
    }

    private fun getMockActivateResponse(): String {
        return """
        {
          "status": 200,
          "message": "Kích hoạt tài khoản thành công! Bạn có thể đăng nhập ngay."
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
            "status": "ACTIVE"
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
              },
              {
                "id": 102,
                "title": "Bảo trì thiết bị",
                "message": "Phòng P.405 sẽ tạm đóng để bảo trì máy chiếu vào thứ 7.",
                "is_read": true,
                "target_screen": "ROOM_DETAIL",
                "target_id": "ROOM-405",
                "created_at": "2026-03-29T14:00:00Z"
              }
            ],
            "pagination": {
              "page": 1,
              "limit": 20,
              "total_items": 2,
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
                "status": "ACTIVE"
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
                "status": "ACTIVE"
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
                "status": "ACTIVE"
              },
              {
                "id": 4,
                "username": "2051220001",
                "full_name": "Nguyễn Văn Học",
                "email": "2051220001@dau.edu.vn",
                "role": "STUDENT",
                "permissions": ["ROOM_VIEW", "BOOKING_CREATE", "BOOKING_EDIT_OWN", "BOOKING_CANCEL_OWN"],
                "department_id": 10,
                "department_name": "Khoa CNTT",
                "status": "ACTIVE"
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
              "id": "TRAINING_OFFICER",
              "name": "Cán bộ Phòng Đào tạo",
              "permissions": ["ROOM_VIEW", "BOOKING_CREATE", "BOOKING_EDIT_OWN", "BOOKING_CANCEL_OWN", "BOOKING_APPROVE", "BOOKING_PROPOSE"]
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
                  "id": 3,
                  "name": "Phòng Đào tạo",
                  "parent_id": 1,
                  "status": "ACTIVE"
                },
                {
                  "id": 10,
                  "name": "Khoa Công nghệ thông tin",
                  "parent_id": 1,
                  "status": "ACTIVE"
                },
                {
                  "id": 11,
                  "name": "Khoa Kiến trúc",
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
                "action": "LOCK_USER",
                "description": "Đã khóa tài khoản sinh viên sv.2051220099",
                "ip_address": "172.16.1.45",
                "created_at": "2026-03-30T10:00:00Z"
              },
              {
                "id": 5002,
                "user_id": 2,
                "user_name": "Lê Văn Quản (ROOM_MANAGER)",
                "action": "ROOM_LOCK",
                "description": "Đã khóa phòng P.405 phục vụ sửa chữa",
                "ip_address": "172.16.1.88",
                "created_at": "2026-03-29T15:30:00Z"
              }
            ],
            "pagination": {
              "page": 1,
              "limit": 20,
              "total_items": 2,
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

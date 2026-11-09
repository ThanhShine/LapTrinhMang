# DAU RMS - API Contract (Tài khoản, Nền tảng, Phân quyền & Quản trị)

Tài liệu Hợp đồng API RESTful dành cho ứng dụng **DAU RMS** (Hệ thống quản lý phòng học - Trường Đại học Kiến trúc Đà Nẵng).

---

## 1. Quy ước chung

- **Base URL**: `https://api-rms.dau.edu.vn/api/v1` (Cấu hình qua `BuildConfig.BASE_URL`).
- **Format dữ liệu**: `JSON` (Content-Type: `application/json; charset=utf-8`).
- **Múi giờ**: `Asia/Ho_Chi_Minh` (Định dạng ISO-8601: `YYYY-MM-DDTHH:mm:ssZ` hoặc `YYYY-MM-DDTHH:mm:ss+07:00`).
- **Xác thực**: HTTP Bearer Token gửi trong Header:
  ```
  Authorization: Bearer <JWT_TOKEN>
  ```

---

## 2. Dấu hiệu & Mã lỗi Thống nhất (Unified Error Format)

Mọi API lỗi trả về cấu trúc JSON chuẩn:
```json
{
  "status": 400,
  "code": "INVALID_INPUT",
  "message": "Thông điệp lỗi hiển thị cho người dùng",
  "errors": {
    "email": "Email phải có tên miền @dau.edu.vn"
  }
}
```

Các mã HTTP status code chính:
- `200 OK` / `201 Created`: Thành công.
- `400 Bad Request`: Tham số không hợp lệ.
- `401 Unauthorized`: Token không hợp lệ hoặc hết hạn -> **App tự xóa phiên và quay lại màn Đăng nhập**.
- `403 Forbidden`: Người dùng không có quyền truy cập -> **App hiển thị thông báo "Bạn không có quyền thực hiện chức năng này"**.
- `409 Conflict`: Dữ liệu bị xung đột -> **App hiển thị "Trùng lịch hoặc dữ liệu đã thay đổi, vui lòng tải lại"**.
- `500 Internal Server Error`: Lỗi hệ thống máy chủ.

---

## 3. Danh sách Vai trò (Roles) & Mã quyền (Permissions)

### Enum Role:
1. `ADMIN`: Quản trị hệ thống
2. `ROOM_MANAGER`: Quản lý phòng
3. `TRAINING_OFFICER`: Cán bộ Phòng Đào tạo (có cả đặt phòng & phê duyệt)
4. `LECTURER`: Giảng viên
5. `STUDENT`: Sinh viên
6. `DEAN`: Trưởng khoa
7. `APPROVER`: Người phê duyệt
8. `TECHNICIAN`: Kỹ thuật viên / Quản lý sự cố
9. `EXAM_OFFICE`: Khảo thí

### Enum Permission Code:
- `ROOM_VIEW`: Xem danh sách & thông tin phòng học
- `BOOKING_CREATE`: Tạo đơn đặt phòng
- `BOOKING_EDIT_OWN`: Sửa đơn đặt phòng của chính mình
- `BOOKING_CANCEL_OWN`: Hủy đơn đặt phòng của chính mình
- `BOOKING_APPROVE`: Phê duyệt / Từ chối đơn đặt phòng
- `BOOKING_PROPOSE`: Đề xuất phòng thay thế
- `ROOM_LOCK`: Khóa / Mở khóa phòng học
- `ISSUE_ASSIGN`: Phân công xử lý sự cố
- `ISSUE_UPDATE`: Cập nhật trạng thái sự cố
- `ISSUE_ACCEPT`: Nghiệm thu sự cố
- `REPORT_EXPORT`: Xuất báo cáo thống kê
- `AUDIT_VIEW`: Xem lịch sử thao tác (Audit Log)

---

## 4. Hợp đồng API chi tiết

### 4.1 PH1 - Xác thực & Tài khoản (Auth & Account)

#### 4.1.1 Đăng ký tài khoản
- **Endpoint**: `POST /auth/register`
- **Request Body**:
  ```json
  {
    "user_code": "2051220001",
    "email": "sv.2051220001@dau.edu.vn",
    "password": "Password123@",
    "full_name": "Nguyễn Văn A"
  }
  ```
- **Response 200 OK**:
  ```json
  {
    "status": 200,
    "message": "Mã OTP đã được gửi về email trường.",
    "data": {
      "email": "sv.2051220001@dau.edu.vn",
      "expires_in_seconds": 300,
      "resend_in_seconds": 60
    }
  }
  ```

#### 4.1.2 Kích hoạt tài khoản bằng OTP
- **Endpoint**: `POST /auth/activate`
- **Request Body**:
  ```json
  {
    "email": "sv.2051220001@dau.edu.vn",
    "otp_code": "123456"
  }
  ```
- **Response 200 OK**:
  ```json
  {
    "status": 200,
    "message": "Kích hoạt tài khoản thành công! Bạn có thể đăng nhập ngay."
  }
  ```

#### 4.1.3 Gửi lại OTP
- **Endpoint**: `POST /auth/resend-otp`
- **Request Body**:
  ```json
  {
    "email": "sv.2051220001@dau.edu.vn",
    "type": "REGISTER" // REGISTER | FORGOT_PASSWORD
  }
  ```
- **Response 200 OK**:
  ```json
  {
    "status": 200,
    "message": "Mã OTP mới đã được gửi."
  }
  ```

#### 4.1.4 Đăng nhập
- **Endpoint**: `POST /auth/login`
- **Request Body**:
  ```json
  {
    "identifier": "2051220001", // MSSV/MSGV hoặc Email
    "password": "Password123@"
  }
  ```
- **Response 200 OK**:
  ```json
  {
    "status": 200,
    "message": "Đăng nhập thành công",
    "data": {
      "access_token": "eyJhbGciOiJIUzI1Ni...",
      "refresh_token": "def456...",
      "expires_in": 86400,
      "user": {
        "id": 1,
        "username": "2051220001",
        "full_name": "Nguyễn Văn A",
        "email": "sv.2051220001@dau.edu.vn",
        "role": "STUDENT",
        "permissions": [
          "ROOM_VIEW",
          "BOOKING_CREATE",
          "BOOKING_EDIT_OWN",
          "BOOKING_CANCEL_OWN"
        ],
        "department_id": 10,
        "department_name": "Khoa Công nghệ thông tin",
        "avatar_url": "https://dau.edu.vn/uploads/avatars/user1.jpg",
        "status": "ACTIVE"
      }
    }
  }
  ```

#### 4.1.5 Quên mật khẩu - Yêu cầu OTP
- **Endpoint**: `POST /auth/forgot-password/request`
- **Request Body**:
  ```json
  {
    "email": "sv.2051220001@dau.edu.vn"
  }
  ```
- **Response 200 OK**:
  ```json
  {
    "status": 200,
    "message": "Mã OTP khôi phục mật khẩu đã được gửi đến email."
  }
  ```

#### 4.1.6 Quên mật khẩu - Đặt lại mật khẩu mới
- **Endpoint**: `POST /auth/forgot-password/reset`
- **Request Body**:
  ```json
  {
    "email": "sv.2051220001@dau.edu.vn",
    "otp_code": "123456",
    "new_password": "NewPassword123@"
  }
  ```
- **Response 200 OK**:
  ```json
  {
    "status": 200,
    "message": "Đặt lại mật khẩu thành công. Vui lòng đăng nhập lại."
  }
  ```

#### 4.1.7 Đăng xuất
- **Endpoint**: `POST /auth/logout`
- **Header**: `Authorization: Bearer <JWT>`
- **Response 200 OK**:
  ```json
  {
    "status": 200,
    "message": "Đăng xuất thành công."
  }
  ```

#### 4.1.8 Xem hồ sơ cá nhân
- **Endpoint**: `GET /account/profile`
- **Header**: `Authorization: Bearer <JWT>`
- **Response 200 OK**:
  ```json
  {
    "status": 200,
    "data": {
      "id": 1,
      "username": "2051220001",
      "full_name": "Nguyễn Văn A",
      "email": "sv.2051220001@dau.edu.vn",
      "phone": "0905123456",
      "role": "STUDENT",
      "department_id": 10,
      "department_name": "Khoa Công nghệ thông tin",
      "avatar_url": "https://dau.edu.vn/uploads/avatars/user1.jpg",
      "status": "ACTIVE",
      "created_at": "2026-01-15T08:00:00Z"
    }
  }
  ```

#### 4.1.9 Cập nhật hồ sơ cá nhân
- **Endpoint**: `PUT /account/profile`
- **Header**: `Authorization: Bearer <JWT>`
- **Request Body**:
  ```json
  {
    "full_name": "Nguyễn Văn A",
    "phone": "0905999888",
    "avatar_url": "https://dau.edu.vn/uploads/avatars/user1_new.jpg"
  }
  ```
- **Response 200 OK**:
  ```json
  {
    "status": 200,
    "message": "Cập nhật hồ sơ thành công.",
    "data": { ... }
  }
  ```

#### 4.1.10 Đổi mật khẩu
- **Endpoint**: `PUT /account/change-password`
- **Header**: `Authorization: Bearer <JWT>`
- **Request Body**:
  ```json
  {
    "old_password": "Password123@",
    "new_password": "NewPassword456@"
  }
  ```
- **Response 200 OK**:
  ```json
  {
    "status": 200,
    "message": "Đổi mật khẩu thành công."
  }
  ```

---

### 4.2 Thông báo (Notifications)

#### 4.2.1 Danh sách thông báo (Có phân trang & lọc)
- **Endpoint**: `GET /notifications`
- **Header**: `Authorization: Bearer <JWT>`
- **Query Params**:
  - `page` (default: 1)
  - `limit` (default: 20)
  - `is_read` (optional: `true` / `false`)
- **Response 200 OK**:
  ```json
  {
    "status": 200,
    "data": {
      "items": [
        {
          "id": 101,
          "title": "Yêu cầu đặt phòng đã được duyệt",
          "message": "Phòng P.302 cho môn Lập trình mạng đã được phê duyệt.",
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
  ```

#### 4.2.2 Đánh dấu đã đọc một thông báo
- **Endpoint**: `PUT /notifications/{id}/read`
- **Response 200 OK**:
  ```json
  {
    "status": 200,
    "message": "Đã đánh dấu là đã đọc."
  }
  ```

#### 4.2.3 Đánh dấu đã đọc tất cả thông báo
- **Endpoint**: `PUT /notifications/read-all`
- **Response 200 OK**:
  ```json
  {
    "status": 200,
    "message": "Đã đánh dấu đọc tất cả."
  }
  ```

#### 4.2.4 Đăng ký Token Push Notification (FCM)
- **Endpoint**: `POST /notifications/fcm-token`
- **Request Body**:
  ```json
  {
    "fcm_token": "fcm_device_token_hash_here"
  }
  ```
- **Response 200 OK**:
  ```json
  {
    "status": 200,
    "message": "Đã lưu FCM Token."
  }
  ```

---

### 4.3 PH5 - Quản trị Tài khoản, Vai trò, Đơn vị & Audit Log (Admin)

#### 4.3.1 Danh sách người dùng (Admin)
- **Endpoint**: `GET /admin/users`
- **Query Params**: `page`, `limit`, `search`, `role`, `department_id`, `status`
- **Response 200 OK**:
  ```json
  {
    "status": 200,
    "data": {
      "items": [
        {
          "id": 1,
          "username": "2051220001",
          "full_name": "Nguyễn Văn A",
          "email": "sv.2051220001@dau.edu.vn",
          "role": "STUDENT",
          "department_name": "Khoa CNTT",
          "status": "ACTIVE"
        }
      ],
      "pagination": {
        "page": 1,
        "limit": 20,
        "total_items": 45,
        "total_pages": 3
      }
    }
  }
  ```

#### 4.3.2 Thêm người dùng mới (Admin)
- **Endpoint**: `POST /admin/users`
- **Request Body**:
  ```json
  {
    "username": "2051220002",
    "full_name": "Trần Thị B",
    "email": "sv.2051220002@dau.edu.vn",
    "role": "STUDENT",
    "department_id": 10,
    "password": "Password123@"
  }
  ```
- **Response 201 Created**:
  ```json
  {
    "status": 201,
    "message": "Tạo tài khoản thành công."
  }
  ```

#### 4.3.3 Sửa người dùng (Admin)
- **Endpoint**: `PUT /admin/users/{id}`
- **Request Body**:
  ```json
  {
    "full_name": "Trần Thị B (Đã cập nhật)",
    "role": "LECTURER",
    "department_id": 10
  }
  ```

#### 4.3.4 Khóa / Mở khóa người dùng (Admin)
- **Endpoint**: `PUT /admin/users/{id}/status`
- **Request Body**:
  ```json
  {
    "status": "LOCKED" // ACTIVE | LOCKED
  }
  ```

#### 4.3.5 Đặt lại mật khẩu người dùng (Admin)
- **Endpoint**: `POST /admin/users/{id}/reset-password`
- **Request Body**:
  ```json
  {
    "new_password": "NewAdminReset123@"
  }
  ```

#### 4.3.6 Danh sách vai trò & Cấu hình quyền (Admin)
- **Endpoint**: `GET /admin/roles`
- **Response 200 OK**:
  ```json
  {
    "status": 200,
    "data": [
      {
        "id": "ROLE_LECTURER",
        "name": "Giảng viên",
        "permissions": ["ROOM_VIEW", "BOOKING_CREATE", "BOOKING_EDIT_OWN", "BOOKING_CANCEL_OWN"]
      }
    ]
  }
  ```

- **Endpoint**: `PUT /admin/roles/{role_id}/permissions`
- **Request Body**:
  ```json
  {
    "permissions": ["ROOM_VIEW", "BOOKING_CREATE", "BOOKING_EDIT_OWN", "BOOKING_CANCEL_OWN", "ROOM_LOCK"]
  }
  ```

#### 4.3.7 Quản lý Đơn vị (Khoa / Phòng / Ban - Cha/Con)
- **Endpoint**: `GET /admin/departments`
- **Response 200 OK**:
  ```json
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
            "id": 10,
            "name": "Khoa Công nghệ thông tin",
            "parent_id": 1,
            "status": "ACTIVE"
          }
        ]
      }
    ]
  }
  ```

- **Endpoint**: `POST /admin/departments`
- **Request Body**:
  ```json
  {
    "name": "Phòng Đào tạo",
    "parent_id": 1
  }
  ```

- **Endpoint**: `PUT /admin/departments/{id}`
- **Request Body**:
  ```json
  {
    "name": "Phòng Khảo thí & Đảm bảo chất lượng",
    "parent_id": 1,
    "status": "ACTIVE"
  }
  ```

#### 4.3.8 Danh sách Lịch sử Thao tác (Audit Log)
- **Endpoint**: `GET /admin/audit-logs`
- **Query Params**: `page`, `limit`, `user_id`, `action`, `start_time`, `end_time`
- **Response 200 OK**:
  ```json
  {
    "status": 200,
    "data": {
      "items": [
        {
          "id": 5001,
          "user_id": 1,
          "user_name": "Nguyễn Văn A (ADMIN)",
          "action": "LOCK_USER",
          "description": "Đã khóa tài khoản sv.2051220002",
          "ip_address": "172.16.1.45",
          "created_at": "2026-03-30T10:00:00Z"
        }
      ],
      "pagination": {
        "page": 1,
        "limit": 20,
        "total_items": 150,
        "total_pages": 8
      }
    }
  }
  ```

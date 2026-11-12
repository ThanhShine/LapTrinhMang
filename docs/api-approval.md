# DAU RMS - API Contract (Phê duyệt & Quản lý lịch - PH3, Quản lý danh mục phòng - PH5)

Tài liệu Hợp đồng API RESTful dành cho module **Phê duyệt & Quản lý lịch** do **TV3 (Phong)** phụ trách trong dự án **DAU RMS** (Đại học Kiến trúc Đà Nẵng).

---

## 1. Quy ước chung

- **Base URL**: `https://api-rms.dau.edu.vn/api/v1` (Cấu hình qua `BuildConfig.BASE_URL`).
- **Format dữ liệu**: `JSON` (`Content-Type: application/json; charset=utf-8`).
- **Múi giờ**: `Asia/Ho_Chi_Minh` (Chuẩn ISO-8601: `YYYY-MM-DDTHH:mm:ss+07:00`).
- **Xác thực**: Bearer Token trong Request Header:
  ```http
  Authorization: Bearer <JWT_TOKEN>
  ```
- **Quyền hạn cần thiết**: `BOOKING_APPROVE`, `ROOM_LOCK`, hoặc vai trò `ADMIN`, `APPROVER`, `TRAINING_OFFICER`, `DEAN`, `ROOM_MANAGER`.

---

## 2. Mã lỗi đặc thù của Module Approval

Ngoài các mã lỗi chuẩn (`400`, `401`, `403`, `500`), module Approval có các mã lỗi nghiệp vụ:

| HTTP Status | Error Code | Mô tả |
|---|---|---|
| `400` | `REASON_REQUIRED` | Bắt buộc phải nhập lý do khi từ chối đơn đặt phòng |
| `409` | `SCHEDULE_CONFLICT` | Trùng lịch với đơn khác đã được duyệt trước đó |
| `409` | `ROOM_LOCKED` | Phòng đang bị khóa trong khung giờ được yêu cầu |
| `400` | `INVALID_STATUS_TRANSITION` | Trạng thái đơn hiện tại không hợp lệ để phê duyệt |

---

## 3. Danh sách Endpoints chi tiết

### 3.1. Danh sách đơn chờ duyệt (P0)

- **Endpoint**: `GET /approvals/pending`
- **Query Params**:
  - `page`: int (mặc định 1)
  - `limit`: int (mặc định 20)
  - `room_id`: string (tùy chọn)
  - `search`: string (tìm theo tên người đặt, mã đơn, mục đích)
  - `priority`: string (`NORMAL` / `HIGH` / `URGENT`)
- **Response 200 OK**:
  ```json
  {
    "status": 200,
    "message": "Thành công",
    "data": {
      "items": [
        {
          "id": "BK-2026-001",
          "room_id": "P.302",
          "room_name": "Phòng Lý thuyết 302 - Nhà A",
          "capacity": 60,
          "requester_id": "2051220001",
          "requester_name": "Nguyễn Văn Học",
          "requester_role": "STUDENT",
          "department_name": "Khoa CNTT",
          "purpose": "Học bù môn Lập trình mạng",
          "attendees_count": 45,
          "start_time": "2026-11-20T08:00:00+07:00",
          "end_time": "2026-11-20T11:30:00+07:00",
          "status": "PENDING",
          "priority": "HIGH",
          "required_equipment": ["Máy chiếu", "Micro", "Điều hòa"],
          "created_at": "2026-11-12T07:30:00+07:00"
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

---

### 3.2. Phê duyệt hoặc từ chối đơn lẻ (P0)

- **Endpoint**: `POST /approvals/{id}/decide`
- **Request Body**:
  ```json
  {
    "action": "REJECT", // "APPROVE" hoặc "REJECT"
    "reason": "Phòng bận chuẩn bị cho hội thảo cấp khoa", // Bắt buộc nếu action là REJECT
    "note": "Gợi ý đặt sang phòng P.304"
  }
  ```
- **Response 200 OK**:
  ```json
  {
    "status": 200,
    "message": "Đã cập nhật trạng thái đơn đặt phòng thành công.",
    "data": {
      "id": "BK-2026-001",
      "status": "REJECTED",
      "approved_by": "Nguyễn Quản Trị",
      "decided_at": "2026-11-12T08:00:00+07:00",
      "reason": "Phòng bận chuẩn bị cho hội thảo cấp khoa"
    }
  }
  ```

---

### 3.3. Xem lịch toàn trường (P0)

- **Endpoint**: `GET /approvals/campus-schedule`
- **Query Params**:
  - `date`: string (định dạng `YYYY-MM-DD`, ví dụ `2026-11-20`)
  - `building`: string (tùy chọn, ví dụ `Nhà A`, `Nhà B`)
  - `floor`: int (tùy chọn)
  - `room_category_id`: string (tùy chọn)
- **Response 200 OK**:
  ```json
  {
    "status": 200,
    "data": {
      "date": "2026-11-20",
      "schedules": [
        {
          "room_id": "P.302",
          "room_name": "Phòng Lý thuyết 302",
          "building": "Nhà A",
          "time_slots": [
            {
              "booking_id": "BK-2026-001",
              "requester_name": "Trần Thị Giáo",
              "purpose": "Giảng dạy Lập trình mạng",
              "start_time": "2026-11-20T07:30:00+07:00",
              "end_time": "2026-11-20T11:30:00+07:00",
              "status": "APPROVED",
              "is_locked": false
            },
            {
              "booking_id": null,
              "purpose": "Bảo trì định kỳ máy lạnh",
              "start_time": "2026-11-20T13:00:00+07:00",
              "end_time": "2026-11-20T15:00:00+07:00",
              "status": "LOCKED",
              "is_locked": true
            }
          ]
        }
      ]
    }
  }
  ```

---

### 3.4. Duyệt hàng loạt (P1)

- **Endpoint**: `POST /approvals/batch-decide`
- **Request Body**:
  ```json
  {
    "action": "APPROVE", // "APPROVE" hoặc "REJECT"
    "booking_ids": ["BK-2026-001", "BK-2026-002", "BK-2026-003"],
    "reason": "Phê duyệt định kỳ theo danh sách đủ điều kiện"
  }
  ```
- **Response 200 OK**:
  ```json
  {
    "status": 200,
    "message": "Xử lý duyệt hàng loạt hoàn tất",
    "data": {
      "total": 3,
      "success_count": 2,
      "failure_count": 1,
      "results": [
        {
          "booking_id": "BK-2026-001",
          "success": true,
          "status": "APPROVED",
          "message": "Phê duyệt thành công"
        },
        {
          "booking_id": "BK-2026-002",
          "success": true,
          "status": "APPROVED",
          "message": "Phê duyệt thành công"
        },
        {
          "booking_id": "BK-2026-003",
          "success": false,
          "status": "PENDING",
          "message": "Xung đột lịch với đơn BK-2026-099 đã được duyệt trước đó"
        }
      ]
    }
  }
  ```

---

### 3.5. Điều chỉnh lịch đã duyệt (P1)

- **Endpoint**: `PUT /approvals/schedules/{id}/adjust`
- **Request Body**:
  ```json
  {
    "new_room_id": "P.304",
    "new_start_time": "2026-11-20T08:30:00+07:00",
    "new_end_time": "2026-11-20T12:00:00+07:00",
    "adjustment_reason": "Điều chuyển do phòng 302 bảo trì đột xuất"
  }
  ```
- **Response 200 OK**:
  ```json
  {
    "status": 200,
    "message": "Điều chỉnh lịch thành công. Thông báo đã được gửi tới người đặt.",
    "data": {
      "booking_id": "BK-2026-001",
      "old_room_id": "P.302",
      "new_room_id": "P.304",
      "start_time": "2026-11-20T08:30:00+07:00",
      "end_time": "2026-11-20T12:00:00+07:00",
      "status": "APPROVED"
    }
  }
  ```

---

### 3.6. Tạm khóa khung giờ phòng (P1)

> **Lưu ý cốt lõi**: Trả về danh sách tất cả các đơn đặt phòng bị ảnh hưởng; **hệ thống KHÔNG tự xóa lịch đã duyệt** mà giữ nguyên để người quản trị chủ động điều chuyển hoặc thông báo.

- **Endpoint**: `POST /approvals/rooms/{id}/lock`
- **Request Body**:
  ```json
  {
    "room_id": "P.302",
    "start_time": "2026-11-20T07:00:00+07:00",
    "end_time": "2026-11-20T12:00:00+07:00",
    "reason": "Phục vụ bảo trì hệ thống điều hòa toàn tầng 3",
    "notify_affected_users": true
  }
  ```
- **Response 200 OK**:
  ```json
  {
    "status": 200,
    "message": "Đã khóa khung giờ phòng thành công. Phát hiện 1 lịch bị ảnh hưởng.",
    "data": {
      "lock_id": "LOCK-991",
      "room_id": "P.302",
      "start_time": "2026-11-20T07:00:00+07:00",
      "end_time": "2026-11-20T12:00:00+07:00",
      "reason": "Phục vụ bảo trì hệ thống điều hòa toàn tầng 3",
      "affected_bookings": [
        {
          "booking_id": "BK-2026-001",
          "requester_name": "Nguyễn Văn Học",
          "requester_phone": "0905123456",
          "purpose": "Học bù môn Lập trình mạng",
          "status": "APPROVED",
          "is_cancelled": false
        }
      ]
    }
  }
  ```

---

### 3.7. Cấu hình quy trình duyệt & Phân công tự động (P2)

- **Endpoint**: `GET /approvals/rules`
- **Response 200 OK**:
  ```json
  {
    "status": 200,
    "data": [
      {
        "id": "RULE-01",
        "name": "Tự động duyệt phòng học cho Giảng viên",
        "description": "Tự động phê duyệt đơn của Giảng viên nếu phòng còn trống và dưới 60 chỗ",
        "is_enabled": true,
        "target_role": "LECTURER",
        "max_capacity": 60,
        "auto_approve": true,
        "escalation_hours": 24
      },
      {
        "id": "RULE-02",
        "name": "Quy trình duyệt sinh viên mượn phòng Lab",
        "description": "Đơn mượn phòng Lab của Sinh viên cần Trưởng khoa hoặc P.Đào tạo duyệt",
        "is_enabled": true,
        "target_role": "STUDENT",
        "max_capacity": 40,
        "auto_approve": false,
        "escalation_hours": 12
      }
    ]
  }
  ```

- **Endpoint**: `PUT /approvals/rules/{id}`
- **Request Body**:
  ```json
  {
    "is_enabled": true,
    "escalation_hours": 36,
    "auto_approve": true
  }
  ```

---

### 3.8. Nhắc duyệt (P2)

- **Endpoint**: `POST /approvals/remind/{id}`
- **Response 200 OK**:
  ```json
  {
    "status": 200,
    "message": "Đã gửi thông báo nhắc duyệt tới người có thẩm quyền."
  }
  ```

---

### 3.9. Quản lý danh mục phòng (PH5 - P2)

- **Endpoint**: `GET /admin/room-categories`
- **Response 200 OK**:
  ```json
  {
    "status": 200,
    "data": [
      {
        "id": "CAT-LT",
        "name": "Phòng Lý thuyết",
        "description": "Phòng học trang bị bảng, micro, máy chiếu tiêu chuẩn",
        "room_count": 28
      },
      {
        "id": "CAT-LAB-IT",
        "name": "Phòng Thực hành Máy tính (CNTT)",
        "description": "Phòng thực hành trang bị 40-50 máy PC và switch mạng chuyên dụng",
        "room_count": 8
      },
      {
        "id": "CAT-XUONG-KT",
        "name": "Xưởng Đồ án Kiến trúc",
        "description": "Bàn vẽ tiêu chuẩn kiến trúc, không gian làm mô hình",
        "room_count": 12
      },
      {
        "id": "CAT-HOI-TRUONG",
        "name": "Hội trường lớn",
        "description": "Sức chứa 200-500 người, hệ thống âm thanh sân khấu",
        "room_count": 3
      }
    ]
  }
  ```

- **Endpoint**: `POST /admin/room-categories`
- **Request Body**:
  ```json
  {
    "name": "Phòng Studio Multimedia",
    "description": "Trang bị máy dựng phim và phông xanh"
  }
  ```

- **Endpoint**: `PUT /admin/room-categories/{id}`
- **Endpoint**: `DELETE /admin/room-categories/{id}`

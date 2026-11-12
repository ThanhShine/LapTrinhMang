# DAU RMS - API Contract (Check-in, Quản lý Sự cố - PH4 & Thiết bị, Thống kê Báo cáo - PH5)

Tài liệu Hợp đồng API RESTful dành cho module **Check-in, Báo cáo sự cố, Quản lý thiết bị & Thống kê báo cáo** do **TV4 (Bảo)** phụ trách trong dự án **DAU RMS** (Trường Đại học Kiến trúc Đà Nẵng).

---

## 1. Quy ước chung

- **Base URL**: `https://api-rms.dau.edu.vn/api/v1` (Cấu hình qua `BuildConfig.BASE_URL`).
- **Format dữ liệu**: `JSON` (`Content-Type: application/json; charset=utf-8`).
- **Múi giờ**: `Asia/Ho_Chi_Minh` (Định dạng ISO-8601: `YYYY-MM-DDTHH:mm:ss+07:00`).
- **Xác thực**: HTTP Bearer Token gửi trong Header:
  ```http
  Authorization: Bearer <JWT_TOKEN>
  ```
- **Quyền hạn chính**:
  - `ISSUE_ASSIGN`: Phân công kỹ thuật viên xử lý sự cố.
  - `ISSUE_UPDATE`: Cập nhật trạng thái sự cố.
  - `ISSUE_ACCEPT`: Nghiệm thu sự cố sau sửa chữa.
  - `REPORT_EXPORT`: Xuất báo cáo thống kê PDF/Excel.

---

## 2. Mã lỗi đặc thù (Error Codes)

| HTTP Status | Error Code | Mô tả |
|---|---|---|
| `400` | `INVALID_QR_CODE` | Mã QR không đúng định dạng của hệ thống DAU RMS |
| `400` | `CHECKIN_TOO_EARLY` | Chưa đến thời gian cho phép check-in (chỉ trước tối đa 15 phút) |
| `400` | `ALREADY_CHECKED_IN` | Đơn đặt phòng này đã được check-in trước đó |
| `400` | `ALREADY_CHECKED_OUT`| Đơn đặt phòng này đã hoàn tất check-out |
| `404` | `BOOKING_NOT_FOUND` | Không tìm thấy lịch đặt phòng tương ứng |
| `400` | `SEVERITY_REQUIRED` | Mức độ sự cố bắt buộc phải được chọn |

---

## 3. Hợp đồng API chi tiết

### 3.1. Quét QR Check-in phòng học (P0)

- **Endpoint**: `POST /checkin/scan`
- **Request Body**:
  ```json
  {
    "qr_code": "DAU-ROOM-302", // Hoặc mã booking "DAU-BK-2026-001"
    "booking_id": 1, // Tùy chọn nếu người dùng nhập tay
    "device_info": "Android Device - App v1.0"
  }
  ```
- **Response 200 OK**:
  ```json
  {
    "status": 200,
    "message": "Check-in thành công phòng P.302",
    "data": {
      "booking_id": 1,
      "room_id": 302,
      "room_code": "P.302",
      "room_name": "Phòng Lý thuyết 302 - Nhà A",
      "user_id": 4,
      "user_name": "Nguyễn Văn Học",
      "check_in_time": "2026-11-20T07:45:10+07:00",
      "expected_end_time": "2026-11-20T11:30:00+07:00",
      "status": "CHECKED_IN"
    }
  }
  ```

---

### 3.2. Xác nhận Check-out trả phòng (P0)

- **Endpoint**: `POST /checkin/checkout`
- **Request Body**:
  ```json
  {
    "booking_id": 1,
    "note": "Đã tắt máy chiếu và điều hòa"
  }
  ```
- **Response 200 OK**:
  ```json
  {
    "status": 200,
    "message": "Trả phòng thành công. Cảm ơn bạn đã sử dụng phòng học!",
    "data": {
      "booking_id": 1,
      "room_code": "P.302",
      "check_out_time": "2026-11-20T11:25:00+07:00",
      "duration_minutes": 220,
      "status": "COMPLETED"
    }
  }
  ```

---

### 3.3. Báo cáo sự cố phòng học (P0)

- **Endpoint**: `POST /issues`
- **Request Body**:
  ```json
  {
    "room_id": 302,
    "room_code": "P.302",
    "title": "Máy chiếu Panasonic không lên nguồn",
    "description": "Bật công tắc nguồn đèn đỏ nhấp nháy liên tục nhưng bóng chiếu không sáng.",
    "severity": "HIGH", // "LOW", "MEDIUM", "HIGH", "CRITICAL"
    "equipment_id": 101, // Tùy chọn nếu liên quan thiết bị cụ thể
    "images": [
      "https://dau.edu.vn/uploads/issues/img_projector_error.jpg"
    ]
  }
  ```
- **Response 201 Created**:
  ```json
  {
    "status": 201,
    "message": "Đã gửi báo cáo sự cố thành công. Kỹ thuật viên sẽ sớm kiểm tra.",
    "data": {
      "id": 501,
      "code": "ISS-2026-001",
      "room_code": "P.302",
      "title": "Máy chiếu Panasonic không lên nguồn",
      "severity": "HIGH",
      "status": "OPEN", // OPEN -> ASSIGNED -> RESOLVED -> CLOSED
      "reporter_name": "Trần Thị Giáo",
      "created_at": "2026-11-20T08:15:00+07:00"
    }
  }
  ```

---

### 3.4. Danh sách sự cố (P1)

- **Endpoint**: `GET /issues`
- **Query Params**: `status` (OPEN, ASSIGNED, RESOLVED), `severity`, `room_id`, `search`
- **Response 200 OK**:
  ```json
  {
    "status": 200,
    "data": [
      {
        "id": 501,
        "code": "ISS-2026-001",
        "room_code": "P.302",
        "room_name": "Phòng Lý thuyết 302",
        "title": "Máy chiếu Panasonic không lên nguồn",
        "description": "Bật công tắc nguồn đèn đỏ nhấp nháy liên tục.",
        "severity": "HIGH",
        "status": "OPEN",
        "reporter_name": "Trần Thị Giáo",
        "assigned_to": null,
        "images": ["https://dau.edu.vn/uploads/issues/img_projector_error.jpg"],
        "created_at": "2026-11-20T08:15:00+07:00"
      }
    ]
  }
  ```

---

### 3.5. Tiếp nhận & Phân công kỹ thuật viên (P1)

- **Endpoint**: `PUT /issues/{id}/assign`
- **Request Body**:
  ```json
  {
    "technician_id": 5,
    "technician_name": "Võ Kỹ Thuật (Kỹ thuật viên CSVC)",
    "priority": "HIGH",
    "note": "Cần sửa trước 13h chiều cho ca học tiếp theo"
  }
  ```
- **Response 200 OK**:
  ```json
  {
    "status": 200,
    "message": "Phân công xử lý sự cố thành công.",
    "data": {
      "id": 501,
      "status": "ASSIGNED",
      "assigned_to": "Võ Kỹ Thuật",
      "assigned_at": "2026-11-20T08:30:00+07:00"
    }
  }
  ```

---

### 3.6. Nghiệm thu và hoàn tất sự cố (P1)

- **Endpoint**: `PUT /issues/{id}/resolve`
- **Request Body**:
  ```json
  {
    "resolution_note": "Đã thay cầu chì nguồn và vệ sinh lưới lọc quạt tản nhiệt máy chiếu. Thiết bị hoạt động tốt.",
    "is_accepted": true,
    "accepted_by": "Lê Văn Quản (Quản lý phòng)"
  }
  ```
- **Response 200 OK**:
  ```json
  {
    "status": 200,
    "message": "Nghiệm thu và đóng sự cố thành công.",
    "data": {
      "id": 501,
      "status": "RESOLVED",
      "resolved_at": "2026-11-20T10:15:00+07:00"
    }
  }
  ```

---

### 3.7. Đánh giá chất lượng phòng học (P1)

- **Endpoint**: `POST /ratings`
- **Request Body**:
  ```json
  {
    "booking_id": 1,
    "room_id": 302,
    "cleanliness_score": 5, // 1-5 sao
    "equipment_score": 4,   // 1-5 sao
    "lighting_score": 5,    // 1-5 sao
    "overall_score": 5,     // 1-5 sao
    "comment": "Phòng sạch sẽ, micro rõ ràng, điều hòa rất mát."
  }
  ```
- **Response 201 Created**:
  ```json
  {
    "status": 201,
    "message": "Cảm ơn bạn đã gửi đánh giá chất lượng phòng học!"
  }
  ```

---

### 3.8. Quản lý thiết bị phòng học (PH5 - P2)

- **Endpoint**: `GET /admin/equipment`
- **Query Params**: `room_id`, `category`, `status`
- **Response 200 OK**:
  ```json
  {
    "status": 200,
    "data": [
      {
        "id": 101,
        "code": "EQ-PRJ-01",
        "name": "Máy chiếu Panasonic Laser",
        "category": "Máy chiếu",
        "room_code": "P.302",
        "room_name": "Phòng Lý thuyết 302",
        "status": "ACTIVE", // ACTIVE, MAINTENANCE, BROKEN
        "serial_number": "PAN-2024-8899",
        "installed_date": "2024-08-15"
      }
    ]
  }
  ```
- **Endpoint**: `POST /admin/equipment`
- **Endpoint**: `PUT /admin/equipment/{id}`
- **Endpoint**: `DELETE /admin/equipment/{id}`

---

### 3.9. Thống kê & Báo cáo (P2)

- **Endpoint**: `GET /reports/usage-statistics`
  - Thống kê tỷ lệ lấp đầy phòng học theo tuần/tháng, số giờ sử dụng, khoa sử dụng nhiều nhất.
- **Endpoint**: `GET /reports/issues-statistics`
  - Thống kê số lượng sự cố theo tháng, thời gian xử lý trung bình (MTTR), loại thiết bị hay hỏng nhất.
- **Endpoint**: `GET /reports/export?type=EXCEL&month=11&year=2026`
  - Xuất báo cáo dưới định dạng Excel (.xlsx), PDF hoặc CSV.

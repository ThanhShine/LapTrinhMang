# API Contract - Module Đặt phòng (Booking API)
Tài liệu đặc tả kỹ thuật RESTful API cho Module Quản lý và Đặt phòng (PH2 & PH5).
Người phụ trách: **Trinh (Thành viên 2 - TV2)**
Áp dụng múi giờ chuẩn: `Asia/Ho_Chi_Minh` (+07:00), định dạng ngày giờ chuẩn ISO-8601 (`YYYY-MM-DDTHH:mm:ss+07:00`).

---

## 1. Tiêu chuẩn mã phản hồi (HTTP Status Codes)
- `200 OK`: Truy vấn hoặc cập nhật dữ liệu thành công.
- `201 Created`: Tạo mới thành công (đặt phòng, tòa nhà, loại phòng).
- `400 Bad Request`: Dữ liệu đầu vào không hợp lệ (giờ kết thúc trước giờ bắt đầu, thiếu thông tin bắt buộc).
- `401 Unauthorized`: Chưa đăng nhập hoặc Access Token hết hạn.
- `403 Forbidden`: Người dùng không có quyền thực hiện thao tác.
- `404 Not Found`: Không tìm thấy tài nguyên (phòng, đơn đặt phòng, tòa nhà).
- `409 Conflict`: Xung đột lịch (phòng đã có người đặt hoặc đang trong thời gian tạm khóa).
- `500 Internal Server Error`: Lỗi máy chủ nội bộ.

---

## 2. Danh sách Endpoints

### 2.1. Tra cứu danh sách và chi tiết phòng học

#### `GET /api/v1/rooms`
Lấy danh sách phòng học có hỗ trợ phân trang và bộ lọc tìm kiếm.
- **Headers:** `Authorization: Bearer <access_token>`
- **Query Parameters:**
  - `page` (int, default: 1): Trang hiện tại.
  - `limit` (int, default: 20): Số lượng bản ghi mỗi trang.
  - `search` (string, optional): Từ khóa tìm kiếm (mã phòng, tên phòng).
  - `building_id` (long, optional): Lọc theo ID tòa nhà.
  - `room_type_id` (long, optional): Lọc theo ID loại phòng.
  - `min_capacity` (int, optional): Sức chứa tối thiểu.
  - `status` (string, optional): `AVAILABLE`, `OCCUPIED`, `MAINTENANCE`, `LOCKED`.
  - `is_favorite` (boolean, optional): Lọc các phòng đã đánh dấu yêu thích.

- **Response:** `200 OK`
```json
{
  "status": 200,
  "data": {
    "items": [
      {
        "id": 1,
        "room_code": "P.302",
        "room_name": "Phòng học Lý thuyết 302",
        "building_id": 1,
        "building_name": "Tòa nhà A",
        "floor": 3,
        "capacity": 60,
        "room_type_id": 1,
        "room_type_name": "Phòng Lý thuyết",
        "status": "AVAILABLE",
        "amenities": ["Máy chiếu", "Điều hòa", "Micro không dây", "Wifi DAU-HighSpeed"],
        "description": "Phòng học trang bị hệ thống âm thanh giảng đường tiêu chuẩn.",
        "image_url": "https://dau.edu.vn/uploads/rooms/p302.jpg",
        "is_favorite": true
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

#### `GET /api/v1/rooms/{id}`
Lấy thông tin chi tiết của một phòng học.
- **Headers:** `Authorization: Bearer <access_token>`
- **Response:** `200 OK`
```json
{
  "status": 200,
  "data": {
    "id": 1,
    "room_code": "P.302",
    "room_name": "Phòng học Lý thuyết 302",
    "building_id": 1,
    "building_name": "Tòa nhà A",
    "floor": 3,
    "capacity": 60,
    "room_type_id": 1,
    "room_type_name": "Phòng Lý thuyết",
    "status": "AVAILABLE",
    "amenities": ["Máy chiếu", "Điều hòa", "Micro không dây", "Wifi DAU-HighSpeed"],
    "description": "Phòng học trang bị hệ thống âm thanh giảng đường tiêu chuẩn.",
    "image_url": "https://dau.edu.vn/uploads/rooms/p302.jpg",
    "is_favorite": true
  }
}
```

---

#### `POST /api/v1/rooms/search-available`
Tìm phòng trống theo khung giờ cụ thể và tiêu chí mong muốn.
- **Headers:** `Authorization: Bearer <access_token>`
- **Request Body:**
```json
{
  "start_time": "2026-04-10T08:00:00+07:00",
  "end_time": "2026-04-10T11:30:00+07:00",
  "building_id": 1,
  "room_type_id": 1,
  "min_capacity": 45,
  "amenities": ["Máy chiếu", "Điều hòa"]
}
```
- **Response:** `200 OK` (Trả về danh sách phòng thỏa mãn và không bị trùng lịch)

---

#### `GET /api/v1/rooms/{id}/schedule`
Lấy lịch sử dụng của một phòng trong một ngày hoặc khoảng ngày.
- **Headers:** `Authorization: Bearer <access_token>`
- **Query Parameters:**
  - `date` (string, format YYYY-MM-DD): Ngày cần xem lịch.
- **Response:** `200 OK`
```json
{
  "status": 200,
  "data": [
    {
      "booking_id": 1001,
      "start_time": "2026-04-10T07:30:00+07:00",
      "end_time": "2026-04-10T09:30:00+07:00",
      "purpose": "Học Lập trình Mạng - Lớp 20KTPM1",
      "user_full_name": "ThS. Trần Thị Giáo",
      "status": "APPROVED"
    },
    {
      "booking_id": 1002,
      "start_time": "2026-04-10T13:30:00+07:00",
      "end_time": "2026-04-10T16:30:00+07:00",
      "purpose": "Bảo vệ Đồ án Kiến trúc K21",
      "user_full_name": "KTS. Lê Văn Khoa",
      "status": "APPROVED"
    }
  ]
}
```

---

#### `POST /api/v1/rooms/{id}/favorite`
Bật/tắt đánh dấu phòng yêu thích.
- **Headers:** `Authorization: Bearer <access_token>`
- **Response:** `200 OK`
```json
{
  "status": 200,
  "message": "Đã thêm vào danh sách yêu thích",
  "data": {
    "room_id": 1,
    "is_favorite": true
  }
}
```

---

### 2.2. Quản lý Đặt phòng (Bookings)

#### `POST /api/v1/bookings`
Gửi yêu cầu đặt phòng mới (P0 & P1).
- **Headers:** `Authorization: Bearer <access_token>`
- **Request Body:**
```json
{
  "room_id": 1,
  "start_time": "2026-04-15T08:00:00+07:00",
  "end_time": "2026-04-15T11:00:00+07:00",
  "purpose": "Họp Câu lạc bộ IT Kiến trúc",
  "attendee_count": 35,
  "is_recurring": false,
  "recurring_rule": null
}
```
- **Response khi thành công:** `201 Created`
```json
{
  "status": 201,
  "message": "Tạo yêu cầu đặt phòng thành công. Đang chờ phê duyệt.",
  "data": {
    "id": 2001,
    "booking_code": "BK-2026-2001",
    "room_id": 1,
    "room_code": "P.302",
    "room_name": "Phòng học Lý thuyết 302",
    "building_name": "Tòa nhà A",
    "user_id": 4,
    "user_full_name": "Nguyễn Văn Học",
    "start_time": "2026-04-15T08:00:00+07:00",
    "end_time": "2026-04-15T11:00:00+07:00",
    "purpose": "Họp Câu lạc bộ IT Kiến trúc",
    "attendee_count": 35,
    "status": "PENDING",
    "rejection_reason": null,
    "is_recurring": false,
    "recurring_rule": null,
    "created_at": "2026-04-01T09:00:00+07:00"
  }
}
```
- **Response khi trùng lịch:** `409 Conflict`
```json
{
  "status": 409,
  "code": "BOOKING_CONFLICT",
  "message": "Khung giờ bạn chọn đã bị trùng với lịch đặt phòng khác hoặc phòng đang tạm khóa.",
  "errors": {
    "conflict_booking_id": "1001",
    "conflict_time": "07:30 - 09:30"
  }
}
```

---

#### `GET /api/v1/bookings/my-bookings`
Xem danh sách yêu cầu đặt phòng của chính người dùng hiện tại (P0).
- **Headers:** `Authorization: Bearer <access_token>`
- **Query Parameters:**
  - `status` (string, optional): `PENDING`, `APPROVED`, `REJECTED`, `CANCELLED`, `COMPLETED`.
  - `page` (int, default: 1)
  - `limit` (int, default: 20)
- **Response:** `200 OK`
```json
{
  "status": 200,
  "data": {
    "items": [
      {
        "id": 2001,
        "booking_code": "BK-2026-2001",
        "room_id": 1,
        "room_code": "P.302",
        "room_name": "Phòng học Lý thuyết 302",
        "building_name": "Tòa nhà A",
        "user_id": 4,
        "user_full_name": "Nguyễn Văn Học",
        "start_time": "2026-04-15T08:00:00+07:00",
        "end_time": "2026-04-15T11:00:00+07:00",
        "purpose": "Họp Câu lạc bộ IT Kiến trúc",
        "attendee_count": 35,
        "status": "PENDING",
        "rejection_reason": null,
        "is_recurring": false,
        "created_at": "2026-04-01T09:00:00+07:00"
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

#### `PUT /api/v1/bookings/{id}`
Chỉnh sửa yêu cầu đặt phòng khi còn ở trạng thái `PENDING` (P1).
- **Headers:** `Authorization: Bearer <access_token>`
- **Request Body:**
```json
{
  "start_time": "2026-04-15T08:30:00+07:00",
  "end_time": "2026-04-15T11:30:00+07:00",
  "purpose": "Họp Ban chủ nhiệm CLB IT (Cập nhật giờ)",
  "attendee_count": 40
}
```
- **Response:** `200 OK`

---

#### `POST /api/v1/bookings/{id}/cancel`
Hủy yêu cầu đặt phòng của chính mình (P1).
- **Headers:** `Authorization: Bearer <access_token>`
- **Request Body:**
```json
{
  "reason": "Dời lịch sinh hoạt sang tuần sau"
}
```
- **Response:** `200 OK`
```json
{
  "status": 200,
  "message": "Đã hủy đơn đặt phòng thành công."
}
```

---

### 2.3. Quản lý Danh mục Tòa nhà, Tầng & Loại phòng (P2 - Quản trị)

#### `GET /api/v1/buildings`
Lấy danh sách tòa nhà và tầng.
- **Response:** `200 OK`
```json
{
  "status": 200,
  "data": [
    {
      "id": 1,
      "name": "Tòa nhà A",
      "code": "A",
      "floors_count": 6,
      "description": "Khu giảng đường chính ĐH Kiến trúc Đà Nẵng"
    },
    {
      "id": 2,
      "name": "Tòa nhà B",
      "code": "B",
      "floors_count": 5,
      "description": "Khu xưởng vẽ và thực hành kiến trúc"
    }
  ]
}
```

#### `POST /api/v1/buildings`
Tạo mới tòa nhà (yêu cầu quyền Admin / Quản lý).
- **Request Body:**
```json
{
  "name": "Tòa nhà C",
  "code": "C",
  "floors_count": 4,
  "description": "Khu thí nghiệm và công nghệ cao"
}
```

#### `GET /api/v1/room-types`
Lấy danh sách các loại phòng học.
- **Response:** `200 OK`
```json
{
  "status": 200,
  "data": [
    { "id": 1, "name": "Phòng Lý thuyết", "code": "THEORY", "description": "Bàn ghế tiêu chuẩn, máy chiếu" },
    { "id": 2, "name": "Phòng Máy tính CNTT", "code": "LAB", "description": "Dàn máy tính 40 máy i7, mạng LAN gigabit" },
    { "id": 3, "name": "Xưởng vẽ Kiến trúc", "code": "STUDIO", "description": "Bàn vẽ kiến trúc chuyên dụng, giá vẽ" },
    { "id": 4, "name": "Hội trường lớn", "code": "AUDITORIUM", "description": "Sức chứa 250 chỗ, âm thanh ánh sáng sự kiện" }
  ]
}
```

#### `POST /api/v1/room-types`
Tạo mới loại phòng.
- **Request Body:**
```json
{
  "name": "Phòng Hội thảo Khoa học",
  "code": "CONFERENCE",
  "description": "Trang bị màn hình LED lớn và hệ thống họp trực tuyến"
}
```


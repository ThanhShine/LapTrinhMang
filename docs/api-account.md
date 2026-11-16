# DAU RMS - API Contract (Tài khoản, Nền tảng, Phân quyền & Quản trị)

Tài liệu Hợp đồng API RESTful cập nhật cho ứng dụng **DAU RMS** (Hệ thống quản lý phòng học - Trường Đại học Kiến trúc Đà Nẵng).

---

## 1. Quy ước chung

- **Base URL**: `https://api-rms.dau.edu.vn/api/v1` (Cấu hình qua `BuildConfig.BASE_URL`).
- **Format dữ liệu**: `JSON` (Content-Type: `application/json; charset=utf-8`).
- **Múi giờ**: `Asia/Ho_Chi_Minh` (ISO-8601: `YYYY-MM-DDTHH:mm:ssZ`).
- **Chính sách cấp tài khoản**: Tài khoản do Admin tạo (thủ công hoặc qua CSV). Không có chức năng tự đăng ký. Khi đăng nhập lần đầu với mật khẩu tạm, `must_change_password = true` bắt buộc người dùng phải đổi mật khẩu mới trước khi truy cập ứng dụng.

---

## 2. Hợp đồng API RESTful

### 2.1 Đăng nhập (Auth)
- **Endpoint**: `POST /auth/login`
- **Request Body**:
  ```json
  {
    "email": "sv.2051220001@dau.edu.vn",
    "password": "TempPassword123"
  }
  ```
- **Response 200 OK**:
  ```json
  {
    "status": 200,
    "message": "Đăng nhập thành công",
    "data": {
      "access_token": "eyJhbG...",
      "user": {
        "id": 1,
        "username": "2051220001",
        "full_name": "Nguyễn Văn A",
        "email": "sv.2051220001@dau.edu.vn",
        "role": "STUDENT",
        "must_change_password": true,
        "status": "ACTIVE"
      }
    }
  }
  ```

### 2.2 Quên mật khẩu
- Người dùng liên hệ Quản trị viên (Admin) để được cấp lại mật khẩu tạm.

### 2.3 Quản trị Tài khoản Admin (PH5.1)

#### 2.3.1 Tạo tài khoản mới (Sinh mật khẩu tạm)
- **Endpoint**: `POST /admin/users`
- **Request Body**:
  ```json
  {
    "email": "sv.2051220099@dau.edu.vn",
    "full_name": "Nguyễn Văn B",
    "role": "STUDENT",
    "department_id": 10
  }
  ```
- **Response 201 Created**:
  ```json
  {
    "status": 201,
    "message": "Tạo tài khoản thành công",
    "data": {
      "user_id": 99,
      "temporary_password": "TempRandomPass88"
    }
  }
  ```

#### 2.3.2 Nhập danh sách tài khoản từ CSV (Import CSV)
- **Endpoint**: `POST /admin/users/import-csv`
- **Request Body**:
  ```json
  {
    "csv_content": "email,full_name,role,department_id\nsv1@dau.edu.vn,Nguyễn Văn A,STUDENT,10\nsv2@dau.edu.vn,Trần Thị B,STUDENT,10"
  }
  ```
- **Response 200 OK**:
  ```json
  {
    "status": 200,
    "data": {
      "total_rows": 2,
      "success_count": 2,
      "failure_count": 0,
      "results": [
        {
          "line": 1,
          "email": "sv1@dau.edu.vn",
          "status": "SUCCESS",
          "temporary_password": "PassRandom123"
        },
        {
          "line": 2,
          "email": "sv2@dau.edu.vn",
          "status": "SUCCESS",
          "temporary_password": "PassRandom456"
        }
      ]
    }
  }
  ```

#### 2.3.3 Đặt lại mật khẩu (Reset Password)
- **Endpoint**: `POST /admin/users/{id}/reset-password`
- **Response 200 OK**:
  ```json
  {
    "status": 200,
    "message": "Mật khẩu đã được đặt lại",
    "data": {
      "temporary_password": "NewResetPass777"
    }
  }
  ```

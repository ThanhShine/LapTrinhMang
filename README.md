# DAU RMS - Hệ thống quản lý phòng học (Trường Đại học Kiến trúc Đà Nẵng)

Dự án Android ứng dụng quản lý phòng học cho Trường ĐH Kiến trúc Đà Nẵng (DAU RMS) được xây dựng theo kiến trúc Gradle Multi-Module, Jetpack Compose, Material 3, MVVM, Hilt, Retrofit + OkHttp và DataStore mã hóa.

---

## 1. Yêu cầu môi trường & Cấu hình (Environment Requirements)

- **Android Studio**: Android Studio Ladybug (2024.2.1+) hoặc mới hơn.
- **JDK**: Java 17 (OpenJDK 17 / Corretto 17).
- **Android Gradle Plugin (AGP)**: `8.7.2`
- **Kotlin**: `2.0.21`
- **Min SDK**: `26` (Android 8.0) | **Compile SDK**: `35`

---

## 2. Cấu trúc Gradle Multi-Module (Module Structure)

```text
LapTrinhMang/
├── app/                  # Module ứng dụng chính, Hilt Application & Main Navigation Host
├── core/
│   ├── model/            # Data models, Role, Permission, NetworkResult, ApiError
│   ├── network/          # Retrofit, OkHttp, AuthInterceptor, Encrypted TokenManager, MockDataInterceptor
│   └── ui/               # Material 3 Theme, Common UI Components, Dynamic BottomNav, RequirePermission
└── feature/
    ├── account/          # PH1: Đăng ký, Đăng nhập, OTP, Quên mật khẩu, Hồ sơ, FCM Push Notification
    ├── booking/          # Đặt phòng (TV2 phụ trách)
    ├── approval/         # Phê duyệt đơn đặt phòng (TV3 phụ trách)
    ├── checkin/          # Check-in & Báo cáo sự cố (TV4 phụ trách)
    └── admin/            # PH5: Quản lý người dùng, Vai trò & Quyền, Đơn vị, Audit Log
```

---

## 3. Hướng dẫn Clone, Build & Chạy thử (Quick Start)

### 3.1 Clone Repository
```bash
git clone <repository-url>
cd LapTrinhMang
```

### 3.2 Build APK Debug
Sử dụng Gradle Wrapper để biên dịch ứng dụng:
```bash
# Trên Linux / macOS / Git Bash
./gradlew assembleDebug

# Trên Windows Command Prompt / PowerShell
gradlew assembleDebug
```

### 3.3 Chạy Unit Test
```bash
./gradlew test
```

---

## 4. Cấu hình Dữ liệu giả (Mock Data Configuration)

Ứng dụng hỗ trợ bật/tắt Mock Data giả lập API không cần kết nối Server thật thông qua `BuildConfig.USE_MOCK_DATA`.

Cấu hình trong `app/build.gradle` hoặc `core/network/build.gradle`:
```groovy
buildConfigField "Boolean", "USE_MOCK_DATA", "true"  // Set false để gọi Server API thật
```

### Tài khoản thử nghiệm dữ liệu giả (Mock Logins):
- **Quản trị viên (`ADMIN`)**: `admin` / `Password123@`
- **Quản lý phòng (`ROOM_MANAGER`)**: `quanly` / `Password123@`
- **Giảng viên (`LECTURER`)**: `giangvien` / `Password123@`
- **Sinh viên (`STUDENT`)**: `2051220001` / `Password123@`

---

## 5. Quy ước đặt tên nhánh & Commit (Git Branching Conventions)

Dự án áp dụng mô hình phân chia nhánh theo tính năng (Feature Branching):

- Nhánh chính: `main` (chỉ chứa code đã test kỹ và được duyệt PR).
- Nhánh tính năng theo module:
  - Thành viên 1 (TV1): `feature/account-<tên-chức-năng>` hoặc `feature/admin-<tên-chức-năng>`
  - Thành viên 2 (TV2): `feature/booking-<tên-chức-năng>`
  - Thành viên 3 (TV3): `feature/approval-<tên-chức-năng>`
  - Thành viên 4 (TV4): `feature/checkin-<tên-chức-năng>`

### Quy ước cú pháp Commit:
- `feat: ...` : Thêm tính năng mới
- `fix: ...` : Sửa lỗi
- `docs: ...` : Cập nhật tài liệu
- `refactor: ...` : Cấu trúc lại mã nguồn mà không đổi logic
- `test: ...` : Bổ sung hoặc sửa unit test

---

## 6. Tài liệu Hợp đồng API (API Contracts)

Chi tiết tài liệu RESTful API endpoints, định dạng JSON request/response và quy chuẩn mã lỗi vui lòng tham khảo file:
👉 **[`docs/api-account.md`](docs/api-account.md)**

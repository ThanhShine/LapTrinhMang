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

---

## 7. Phân vùng file: ai được sửa gì (tránh xung đột)

Mỗi người chỉ sửa trong module của mình. Xung đột Git thường xảy ra khi nhiều người cùng sửa một file chung.

| Khu vực | Người được sửa | Người khác cần sửa thì |
|---|---|---|
| `feature/account/`, `feature/admin/` | TV1 | Nhắn TV1 |
| `feature/booking/` | TV2 | Nhắn TV2 |
| `feature/approval/` | TV3 | Nhắn TV3 |
| `feature/checkin/` | TV4 (phần thống kê, thiết bị trong `feature/admin/` do TV4 làm trong thư mục con riêng) | Nhắn TV4 |
| `core/network/`, `core/ui/`, `app/` | **Chỉ TV1** | Mở Issue hoặc PR nhỏ riêng, TV1 duyệt |
| `core/model/` | Sửa chung, **phải báo cả nhóm trước** | Xem mục 9 |
| `settings.gradle`, `build.gradle` gốc, `gradle/`, `AndroidManifest.xml` của `app`, NavGraph | **Chỉ TV1** | Nhắn TV1 |
| `docs/api-<module>.md` | Người phụ trách module đó | Nhắn người đó |

Quy tắc kèm theo:
- Không format lại cả file, không "Optimize Imports" hay "Reformat Code" cho file không phải của mình. Việc này làm Git thấy cả file đã đổi.
- Không đổi tên, di chuyển hay xóa file ngoài module của mình.
- Đặt tên tài nguyên có tiền tố theo module để không trùng: `booking_`, `approval_`, `checkin_`, `account_`, `admin_` (ví dụ `booking_title`, `ic_checkin_scan`). Mỗi module giữ `strings.xml` và `colors.xml` riêng của mình, không thêm chuỗi vào `app/` hay `core/`.
- Mỗi module tự khai báo Hilt module riêng (ví dụ `BookingModule.kt`), không gom vào file Hilt chung.

---

## 8. Quy trình làm việc hằng ngày

Làm theo đúng thứ tự này mỗi lần bắt đầu và kết thúc:

```bash
# Lần đầu: lấy nhánh develop và tạo nhánh riêng
git checkout develop
git pull origin develop
git checkout -b feature/booking-<ten-chuc-nang>

# Mỗi lần bắt đầu buổi làm việc: lấy code mới của nhóm về nhánh mình
git checkout develop
git pull origin develop
git checkout feature/booking-<ten-chuc-nang>
git merge develop

# Trong lúc làm: commit nhỏ, thường xuyên
git add <file hoặc thư mục của mình>
git commit -m "feat: mô tả ngắn"

# Kết thúc: đẩy nhánh của mình (không đẩy thẳng lên main hay develop)
git push origin feature/booking-<ten-chuc-nang>
```

Nhánh `develop` là nhánh gộp chung của nhóm (TV1 tạo từ `main`). Code chỉ vào `main` khi cả nhóm đã chạy thử xong.

Lưu ý:
- Dùng `git add <đường dẫn>` thay vì `git add .` để không đưa nhầm file lạ lên.
- Không dùng `git push --force` trên bất kỳ nhánh chung nào.
- Mỗi PR chỉ chứa một chức năng, nhỏ để dễ review.

---

## 9. Model dùng chung và hợp đồng API

- Model nằm trong `core/model/` (ví dụ `Room`, `Booking`, `BookingStatus`, `Role`) dùng chung cho cả nhóm. Muốn **thêm trường** thì thêm trường có giá trị mặc định để code cũ không hỏng. Muốn **đổi tên hoặc xóa** trường thì báo cả nhóm trước và gộp bằng một PR riêng.
- Mỗi người viết hợp đồng API của module mình trong `docs/api-<module>.md` trước khi code (`api-booking.md`, `api-approval.md`, `api-checkin.md`). Người khác đọc, không tự sửa.
- App không tự quyết định trùng lịch, duyệt đơn hay check-in. Việc đó do server quyết định, app chỉ hiển thị kết quả.
- Giờ dùng múi giờ `Asia/Ho_Chi_Minh`, gửi API dạng ISO-8601.

---

## 10. Pull request và review

1. Mở PR từ nhánh `feature/...` của mình vào `develop`.
2. Mô tả PR gồm: làm chức năng nào, cách thử, ảnh chụp màn hình nếu có giao diện.
3. Cần ít nhất 1 người khác review rồi mới gộp. Người tạo PR không tự gộp PR của mình khi chưa có review.
4. Trước khi mở PR phải chạy `./gradlew assembleDebug` và `./gradlew test` thành công.
5. Nếu PR báo conflict: lấy `develop` mới về nhánh mình (`git merge develop`), xử lý conflict trên máy mình rồi push lại, không xử lý trực tiếp trên GitHub khi chưa chắc.

Gợi ý cho chủ repo: vào Settings → Branches, bật bảo vệ cho `main` và `develop` (bắt buộc pull request, bắt buộc 1 review).

---

## 11. Xử lý lỗi thường gặp

| Hiện tượng | Cách xử lý |
|---|---|
| Conflict khi merge | Mở file, tìm `<<<<<<<` `=======` `>>>>>>>`, giữ phần đúng của cả hai bên, xóa các dấu đó, `git add` rồi `git commit`. Không chắc thì hỏi người sửa phần kia. |
| `git push` bị từ chối (rejected) | Lấy code mới về (`git pull --rebase origin <nhánh của mình>`) rồi push lại. |
| Gradle sync lỗi sau khi pull | Bấm **Sync Now**; nếu vẫn lỗi: Build → Clean Project, rồi File → Invalidate Caches → Restart. |
| Android Studio báo "Gradle files have changed" | Bấm **Sync Now**, chờ xong rồi mới Run. |
| Build lỗi do khác phiên bản | Dùng đúng phiên bản ở mục 1 (JDK 17, AGP 8.7.2, Kotlin 2.0.21). Không tự nâng phiên bản. |
| Git báo cả file bị đổi dù không sửa | Do khác kiểu xuống dòng trên Windows. Chạy `git config core.autocrlf true` rồi thử lại. |
| Lỡ commit nhầm file không nên đưa lên | Báo TV1 ngay, đừng tự xóa lịch sử. Nếu là mật khẩu hoặc khóa, phải đổi khóa đó. |

---

## 12. Những thứ KHÔNG được đưa lên GitHub

- `local.properties`, thư mục `.idea/`, thư mục `build/`, `.gradle/`.
- File khóa ký app (`*.jks`, `*.keystore`).
- `google-services.json` thật (FCM): TV1 gửi riêng cho từng thành viên qua kênh nhóm, mỗi người tự đặt vào `app/`.
- Mật khẩu, khóa API, token, địa chỉ server nội bộ.

---

## 13. Checklist trước khi push

- [ ] Đã `git pull` và `git merge develop` vào nhánh của mình, không còn conflict.
- [ ] `./gradlew assembleDebug` thành công.
- [ ] `./gradlew test` thành công.
- [ ] Đã chạy thử app trên emulator hoặc máy thật (Mock Data bật), chức năng của mình hoạt động.
- [ ] `git status` chỉ có file trong module của mình, không có file lạ (`.idea`, `local.properties`, `build/`).
- [ ] Không sửa file chung ở mục 7 khi chưa báo TV1.
- [ ] Commit theo quy ước ở mục 5, một chức năng mỗi PR.

# BÁO CÁO CÁ NHÂN – THÀNH VIÊN 3
## Dự án: DAU RMS – Hệ thống Quản lý Phòng học (Trường ĐH Kiến trúc Đà Nẵng)

---

| Thông tin | Chi tiết |
|---|---|
| **Họ và tên** | Nguyễn Dương Phong |
| **Vai trò** | Thành viên 3 (TV3) |
| **Module phụ trách** | `feature/approval` – Phê duyệt & Quản lý lịch |
| **Nhánh Git** | `feature/approval-phong` |
| **Repository** | https://github.com/ThanhShine/LapTrinhMang |
| **Ngôn ngữ / Nền tảng** | Kotlin · Android · Jetpack Compose · Material 3 |
| **Kiến trúc** | MVVM · Clean Architecture · Hilt DI · Gradle Multi-Module |

---

## 1. Phạm vi công việc được giao

TV3 chịu trách nhiệm **toàn bộ module phê duyệt đơn đặt phòng (PH3)** và **phần quản lý danh mục phòng thuộc giai đoạn PH5**, bao gồm:

| Giai đoạn | Nội dung |
|---|---|
| **PH3** | 9 chức năng phê duyệt & quản lý lịch |
| **PH5** | Quản lý danh mục phòng (CRUD) |
| **Docs** | Tài liệu hợp đồng API `docs/api-approval.md` |

---

## 2. Danh sách chức năng đã hoàn thành

### 2.1. Module Approval (PH3) – 9 chức năng

| STT | Chức năng | Mô tả | Mức ưu tiên |
|---|---|---|---|
| 3.1 | **Danh sách đơn chờ duyệt** | Hiển thị danh sách đơn đặt phòng đang ở trạng thái `PENDING`, hỗ trợ lọc theo phòng, từ khóa tìm kiếm và mức ưu tiên (`NORMAL / HIGH / URGENT`). Phân trang. | P0 |
| 3.2 | **Phê duyệt / Từ chối đơn lẻ** | Xử lý từng đơn với hành động `APPROVE` hoặc `REJECT`. Khi từ chối, bắt buộc nhập lý do. Hiển thị dialog xác nhận trước khi gửi. | P0 |
| 3.3 | **Xem lịch toàn trường** | Lịch sử dụng phòng toàn trường theo ngày, lọc theo tòa nhà / tầng / danh mục phòng. Hiển thị cả khung giờ bị khóa (`LOCKED`). | P0 |
| 3.4 | **Duyệt hàng loạt** | Chọn nhiều đơn cùng lúc, phê duyệt hoặc từ chối tất cả trong một thao tác. Trả về báo cáo kết quả từng đơn (thành công / thất bại / xung đột lịch). | P1 |
| 3.5 | **Điều chỉnh lịch đã duyệt** | Thay đổi phòng hoặc khung giờ cho đơn đã được duyệt. Hệ thống tự gửi thông báo tới người đặt sau khi điều chỉnh. | P1 |
| 3.6 | **Tạm khóa khung giờ phòng** | Khóa một khung giờ cụ thể của phòng (ví dụ: bảo trì). Hệ thống trả danh sách đơn bị ảnh hưởng để người quản lý chủ động xử lý. | P1 |
| 3.7 | **Cấu hình quy trình duyệt & phân công tự động** | Xem và chỉnh sửa các quy tắc duyệt tự động (ví dụ: tự động duyệt cho giảng viên nếu phòng trống, yêu cầu duyệt thủ công cho sinh viên). | P2 |
| 3.8 | **Nhắc duyệt (Reminder)** | Gửi thông báo nhắc nhở tới người có thẩm quyền duyệt khi đơn chờ quá lâu. | P2 |
| 3.9 | **Lịch sử phê duyệt** *(tích hợp trong màn hình chi tiết)* | Xem lại toàn bộ nhật ký xử lý của từng đơn: ai duyệt, lúc nào, lý do. | P2 |

### 2.2. Quản lý Danh mục Phòng (PH5)

| STT | Chức năng | Mô tả |
|---|---|---|
| 5.1 | **Xem danh sách danh mục phòng** | Liệt kê tất cả loại phòng: Phòng Lý thuyết, Lab CNTT, Xưởng Kiến trúc, Hội trường lớn,… |
| 5.2 | **Thêm danh mục phòng mới** | Form nhập tên và mô tả, gọi API `POST /admin/room-categories`. |
| 5.3 | **Sửa danh mục phòng** | Chỉnh sửa tên và mô tả danh mục đã có, gọi API `PUT /admin/room-categories/{id}`. |
| 5.4 | **Xóa danh mục phòng** | Xóa danh mục với xác nhận dialog, gọi API `DELETE /admin/room-categories/{id}`. |

---

## 3. Kiến trúc kỹ thuật đã triển khai

Toàn bộ module `feature/approval` được xây dựng theo mô hình **Clean Architecture + MVVM**, tách biệt rõ ràng 3 tầng:

```
feature/approval/
├── data/
│   ├── ApprovalDtos.kt             ← Data Transfer Objects (JSON mapping)
│   ├── ApprovalApiService.kt       ← Retrofit interface (9 endpoints)
│   ├── MockApprovalDataSource.kt   ← Dữ liệu giả lập (không cần server)
│   └── ApprovalRepositoryImpl.kt   ← Triển khai Repository
├── di/
│   └── ApprovalModule.kt           ← Hilt Dependency Injection
├── domain/
│   ├── model/
│   │   ├── ApprovalItem.kt         ← Domain model: đơn chờ duyệt
│   │   ├── ApprovalRule.kt         ← Domain model: quy tắc duyệt
│   │   ├── ApprovalStatus.kt       ← Enum trạng thái
│   │   ├── BatchApprovalResult.kt  ← Kết quả duyệt hàng loạt
│   │   ├── CampusSchedule.kt       ← Lịch toàn trường
│   │   └── RoomCategory.kt         ← Danh mục phòng
│   ├── ApprovalRepository.kt       ← Repository interface
│   └── ApprovalUseCases.kt         ← Use cases nghiệp vụ
└── ui/
    ├── ApprovalScreen.kt           ← Màn hình chính (TabRow 4 tab)
    ├── ApprovalViewModel.kt        ← ViewModel (StateFlow, coroutines)
    ├── ApprovalUiState.kt          ← UI State (sealed class)
    ├── ApprovalDialogs.kt          ← Dialog duyệt/từ chối
    ├── PendingApprovalCard.kt      ← Card hiển thị đơn chờ
    ├── PendingApprovalsTab.kt      ← Tab 1: Danh sách đơn chờ
    ├── CampusScheduleTab.kt        ← Tab 2: Lịch toàn trường
    ├── ApprovalRulesTab.kt         ← Tab 3: Quy tắc duyệt
    └── RoomCategoryCrudTab.kt      ← Tab 4: Quản lý danh mục phòng
```

### Công nghệ sử dụng

| Công nghệ | Mục đích |
|---|---|
| **Jetpack Compose + Material 3** | Xây dựng toàn bộ giao diện theo declarative UI |
| **Hilt (Dagger)** | Dependency Injection tự động |
| **Retrofit 2 + Gson** | Gọi RESTful API, deserialize JSON |
| **Kotlin Coroutines + Flow** | Xử lý bất đồng bộ, StateFlow quản lý UI state |
| **MVVM Pattern** | ViewModel ↔ Repository ↔ DataSource |
| **MockDataInterceptor** | Giả lập response API, không cần server thật |

---

## 4. Tài liệu hợp đồng API đã viết

File: [`docs/api-approval.md`](https://github.com/ThanhShine/LapTrinhMang/blob/main/docs/api-approval.md)

Tài liệu ghi rõ **9 endpoints RESTful** của module Approval, bao gồm:

| Endpoint | Method | Chức năng |
|---|---|---|
| `/approvals/pending` | `GET` | Danh sách đơn chờ duyệt (phân trang, lọc) |
| `/approvals/{id}/decide` | `POST` | Phê duyệt hoặc từ chối đơn lẻ |
| `/approvals/campus-schedule` | `GET` | Xem lịch toàn trường theo ngày |
| `/approvals/batch-decide` | `POST` | Duyệt hàng loạt |
| `/approvals/schedules/{id}/adjust` | `PUT` | Điều chỉnh lịch đã duyệt |
| `/approvals/rooms/{id}/lock` | `POST` | Tạm khóa khung giờ phòng |
| `/approvals/rules` | `GET` | Lấy danh sách quy tắc duyệt |
| `/approvals/rules/{id}` | `PUT` | Cập nhật quy tắc duyệt |
| `/approvals/remind/{id}` | `POST` | Gửi nhắc duyệt |
| `/admin/room-categories` | `GET / POST` | Quản lý danh mục phòng |
| `/admin/room-categories/{id}` | `PUT / DELETE` | Sửa / Xóa danh mục phòng |

Mỗi endpoint đều có đầy đủ: **Request params / Request body / Response 200 / Mã lỗi đặc thù**.

---

## 5. Unit Test

File: `feature/approval/src/test/.../ApprovalViewModelTest.kt`

Các test case đã viết và chạy thành công:

| Test Case | Mô tả |
|---|---|
| `loadPendingApprovals_success` | Kiểm tra load danh sách đơn chờ duyệt thành công |
| `approveSingle_success` | Kiểm tra duyệt đơn lẻ cập nhật đúng trạng thái UI |
| `rejectSingle_requiresReason` | Kiểm tra từ chối không có lý do bị block |
| `batchApprove_partialFailure` | Kiểm tra duyệt hàng loạt có đơn thất bại vẫn xử lý đúng |
| `loadCampusSchedule_byDate` | Kiểm tra load lịch toàn trường theo ngày |
| `lockRoom_returnsAffectedBookings` | Kiểm tra khóa phòng trả về danh sách bị ảnh hưởng |

**Kết quả:** `BUILD SUCCESSFUL` – Toàn bộ unit test PASS ✅

---

## 6. Git Workflow đã thực hiện

```
main
 └── develop
      └── feature/approval-phong   ← Nhánh làm việc của TV3
```

| Commit | Nội dung |
|---|---|
| `834822c` | `docs: them tai lieu hop dong api cho module approval` |
| `bc9706c` | `feat: hoan thanh toan bo module approval PH3 va danh muc phong PH5` |
| `fddb1fc` | `fix: sua toolchainVersion ve Java 17 phu hop voi moi truong nhom` |

**Quy trình:**
1. Tạo nhánh `feature/approval-phong` từ `develop`.
2. Viết tài liệu API trước khi code (commit `834822c`).
3. Phát triển toàn bộ module theo kiến trúc Clean Architecture.
4. Chạy unit test (`./gradlew :feature:approval:testDebugUnitTest`), build APK (`./gradlew assembleDebug`) thành công.
5. Push nhánh lên remote, tạo Pull Request vào `develop`.

---

## 7. Build & Test Verification

| Lệnh | Kết quả |
|---|---|
| `./gradlew :feature:approval:testDebugUnitTest` | ✅ `BUILD SUCCESSFUL` |
| `./gradlew assembleDebug` (toàn project) | ✅ `BUILD SUCCESSFUL` |
| `./gradlew testDebugUnitTest` (toàn project) | ✅ `BUILD SUCCESSFUL` |

---

## 8. Các vấn đề gặp phải và cách xử lý

| Vấn đề | Nguyên nhân | Cách xử lý |
|---|---|---|
| KSP lỗi `provideApprovalApiService(error.NonExistentClass)` | Thiếu dependency `retrofit` trong `build.gradle` của module | Thêm `implementation libs.retrofit` và `converter-gson` vào `feature/approval/build.gradle` |
| `Material3 Divider` deprecated | API thay đổi trong Material 3 mới | Chuyển sang dùng `HorizontalDivider` |
| Unit test `first { it is Success }` bị `IllegalStateException` | Coroutines dispatcher chưa advance trong test | Dùng `launch(testDispatcher)` kết hợp `testScheduler.advanceUntilIdle()` |
| Build fail: `toolchainVersion=21` không khớp JDK máy | File `gradle/gradle-daemon-jvm.properties` cài sai phiên bản | Sửa về `toolchainVersion=17` và set `JAVA_HOME` đúng |
| Push trực tiếp vào `main` bị từ chối | Branch Protection Rules trên GitHub | Tạo Pull Request từ `develop` → `main` thay vì push trực tiếp |

---

## 9. Nhận xét tự đánh giá

- ✅ Hoàn thành **100%** các chức năng được giao (PH3 + phần PH5 danh mục phòng).
- ✅ Tuân thủ kiến trúc Clean Architecture, MVVM, Hilt DI theo đúng quy định dự án.
- ✅ Viết tài liệu API đầy đủ trước khi code (API-first approach).
- ✅ Viết unit test cho tầng ViewModel, tất cả test đều pass.
- ✅ Tuân thủ quy ước đặt tên nhánh, commit message, và phân vùng file của nhóm.
- ✅ Không can thiệp vào module của thành viên khác.
- ✅ Build toàn project thành công, không conflict với các module khác.

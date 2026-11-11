# HƯỚNG DẪN LÀM VIỆC NHÓM – DAU RMS (Android)

Tài liệu dành cho 3 thành viên: **Trinh (TV2)**, **Phong (TV3)**, **Bao (TV4)**. TV1 (Thành) là người dựng khung app và quản lý repo.

- Repo: https://github.com/ThanhShine/LapTrinhMang
- Mô tả kỹ thuật và cấu trúc module: xem `README.md` trong repo.

---

## 1. Việc của từng người

| Thành viên | Module | Nhánh làm việc | Phụ trách |
|---|---|---|---|
| **Trinh (TV2)** | `feature/booking` | `feature/booking-trinh` | Tra cứu và đặt phòng (PH2, 11 chức năng) + quản lý tòa nhà/tầng, loại phòng (PH5) |
| **Phong (TV3)** | `feature/approval` | `feature/approval-phong` | Phê duyệt và quản lý lịch (PH3, 9 chức năng) + quản lý danh mục phòng (PH5) |
| **Bao (TV4)** | `feature/checkin` | `feature/checkin-bao` | Check-in QR, sự cố (PH4, 7 chức năng, bỏ PH4.6) + thiết bị, thống kê, báo cáo (PH5) |

Mức ưu tiên: **P0** = luồng cốt lõi, làm trước; **P1** = cần để dùng thật; **P2** = bổ sung, làm sau.

Luồng cần chạy được trước tiên (P0): đăng nhập → tìm phòng → đặt phòng → duyệt → check-in QR → báo sự cố.

### Trinh – Tra cứu và đặt phòng
- **P0:** danh sách phòng, chi tiết phòng, tìm phòng trống, lịch sử dụng, tạo yêu cầu đặt phòng, xem yêu cầu của tôi.
- **P1:** sửa yêu cầu, hủy yêu cầu, đặt phòng lặp lại, phòng yêu thích, chia sẻ thông tin đặt phòng.
- **P2:** CRUD tòa nhà và tầng, CRUD loại phòng.
- Trinh là người tạo model dùng chung `Room`, `Booking`, `BookingStatus` – Phong và Bao dùng lại, nên chốt sớm.

### Phong – Phê duyệt và quản lý lịch
- **P0:** đơn chờ duyệt, phê duyệt đơn lẻ (từ chối phải có lý do), xem lịch toàn trường.
- **P1:** duyệt hàng loạt (hiện kết quả từng đơn), điều chỉnh lịch đã duyệt, tạm khóa khung giờ phòng (hiện danh sách lịch bị ảnh hưởng, **không tự xóa lịch đã duyệt**).
- **P2:** phân công duyệt tự động, cấu hình quy trình duyệt, nhắc duyệt, CRUD danh mục phòng.

### Bao – Check-in, sự cố, thống kê
- **P0:** quét QR check-in (CameraX + ML Kit), xác nhận check-out, tạo báo cáo sự cố có ảnh và mức độ (Low/Medium/High/Critical).
- **P1:** danh sách sự cố, tiếp nhận và phân công, nghiệm thu và hoàn tất, đánh giá chất lượng phòng.
- **P2:** CRUD thiết bị phòng, thống kê tần suất sử dụng, thống kê sự cố, báo cáo xuất PDF/Excel.
- Không làm PH4.6 "Cập nhật tiến độ sửa chữa" (đã quyết định bỏ).

---

## 2. Chuẩn bị máy (làm một lần)

1. **Chấp nhận lời mời vào repo:** mở email GitHub "invited you to collaborate" và bấm **Accept invitation** (hoặc vào https://github.com/ThanhShine/LapTrinhMang/invitations khi đã đăng nhập đúng tài khoản). Chưa Accept thì không push được.
2. **Cài Git:** kiểm tra bằng `git --version` trong CMD. Chưa có thì tải ở https://git-scm.com.
3. **Cài Android Studio** (Ladybug 2024.2.1 trở lên) và **JDK 17**.
4. **Dùng đúng phiên bản của nhóm:** AGP `8.7.2`, Kotlin `2.0.21`, minSdk 26, compileSdk 35. **Không tự nâng phiên bản.**
5. **Cấu hình tên và email GitHub của chính mình** để commit hiện đúng tên:
```cmd
git config --global user.name "Ten Cua Ban"
git config --global user.email "email-dang-ky-github"
git config --global core.autocrlf true
```
Email phải trùng email tài khoản GitHub.

---

## 3. Lấy code về lần đầu (làm một lần)

Mở CMD, **mỗi lệnh gõ riêng rồi bấm Enter**:

```cmd
cd /d D:\
git clone https://github.com/ThanhShine/LapTrinhMang.git
cd LapTrinhMang
```

Chuyển sang nhánh của mình:

| Người | Lệnh |
|---|---|
| Trinh | `git checkout feature/booking-trinh` |
| Phong | `git checkout feature/approval-phong` |
| Bao | `git checkout feature/checkin-bao` |

Kiểm tra đang đúng nhánh (dấu `*` nằm ở nhánh của mình):
```cmd
git branch
```

Sau đó mở thư mục `LapTrinhMang` bằng Android Studio (File → Open), bấm **Sync Now**, đợi sync xong rồi bấm Run.

**Đăng nhập thử (Mock Data đang bật):**
- Quản trị viên: `admin` / `Password123@`
- Quản lý phòng: `quanly` / `Password123@`
- Giảng viên: `giangvien` / `Password123@`
- Sinh viên: `2051220001` / `Password123@`

---

## 4. Cấu trúc nhánh

- `main`: bản ổn định, **không ai push trực tiếp**.
- `develop`: nhánh gộp chung của cả nhóm.
- `feature/<module>-<tên>`: nhánh làm việc của từng người.
- Cần làm chức năng nhỏ riêng: tạo nhánh con từ nhánh của mình, ví dụ `feature/booking-trinh-search-room`, xong thì gộp vào `develop` bằng Pull Request.

Luồng code: **nhánh của mình → Pull Request → `develop` → (cả nhóm chạy thử xong) → `main`.**

---

## 5. Quy trình mỗi buổi làm việc

### 5.1. Bắt đầu buổi: lấy code mới của cả nhóm về nhánh mình
```cmd
cd /d D:\LapTrinhMang
git checkout develop
git pull origin develop
git checkout feature/booking-trinh
git merge develop
```
(Phong dùng `feature/approval-phong`, Bao dùng `feature/checkin-bao`.)

Sau đó mở Android Studio, bấm **Sync Now** nếu có thông báo.

### 5.2. Trong lúc làm: commit nhỏ, thường xuyên
```cmd
git status
git add feature/booking/
git commit -m "feat: them man hinh danh sach phong"
```
- Dùng `git add <thư mục của mình>` thay vì `git add .` để không đưa nhầm file lạ.
- Sau `git add`, chạy `git status` kiểm tra file chuyển sang **màu xanh** rồi mới commit.
- Commit **không dấu** để tránh lỗi hiển thị trong CMD.

Quy ước mở đầu commit:

| Tiền tố | Ý nghĩa |
|---|---|
| `feat:` | Thêm tính năng mới |
| `fix:` | Sửa lỗi |
| `docs:` | Cập nhật tài liệu |
| `refactor:` | Cấu trúc lại code, không đổi logic |
| `test:` | Thêm hoặc sửa unit test |

### 5.3. Đẩy code lên GitHub
```cmd
git push origin feature/booking-trinh
```

### 5.4. Mở Pull Request
1. Vào GitHub, repo `LapTrinhMang`, bấm **Compare & pull request** (hoặc tab Pull requests → New pull request).
2. Chọn: **base: `develop`** ← **compare: nhánh của mình**.
3. Ghi mô tả: làm chức năng nào, cách thử, ảnh chụp màn hình nếu có giao diện.
4. Nhờ **ít nhất 1 người** trong nhóm review. Người tạo PR **không tự gộp** khi chưa có review.

### 5.5. Trước khi mở PR, bắt buộc kiểm tra
```cmd
gradlew assembleDebug
gradlew test
```
Cả hai phải thành công.

---

## 6. Quy tắc tránh xung đột (rất quan trọng)

### 6.1. Ai được sửa gì

| Khu vực | Người được sửa |
|---|---|
| `feature/booking/` | Trinh |
| `feature/approval/` | Phong |
| `feature/checkin/` | Bao |
| `feature/account/`, `feature/admin/` | TV1 (Thành); phần thống kê, thiết bị trong `admin` do Bao làm ở thư mục con riêng |
| `core/network/`, `core/ui/`, `app/` | **Chỉ TV1** |
| `settings.gradle`, `build.gradle` gốc, `gradle/`, `AndroidManifest.xml` của `app`, NavGraph | **Chỉ TV1** |
| `core/model/` | Sửa chung, **phải báo cả nhóm trước** |
| `docs/api-<module>.md` | Người phụ trách module đó |

Cần thêm thư viện, thêm route điều hướng hay sửa file chung: **nhắn TV1** hoặc mở Issue, không tự sửa.

### 6.2. Quy tắc code
- **Không** bấm "Reformat Code" hay "Optimize Imports" cho file không phải của mình.
- **Không** đổi tên, di chuyển hay xóa file ngoài module của mình.
- Tài nguyên đặt tên có tiền tố theo module: `booking_`, `approval_`, `checkin_` (ví dụ `booking_title`, `ic_checkin_scan`). Mỗi module giữ `strings.xml`, `colors.xml` riêng, **không** thêm chuỗi vào `app/` hay `core/`.
- Mỗi module tự khai báo Hilt module riêng (ví dụ `BookingModule.kt`), không gom vào file Hilt chung.
- Model dùng chung (`core/model/`): chỉ **thêm trường có giá trị mặc định**. Đổi tên hoặc xóa trường thì báo cả nhóm và gộp bằng một PR riêng.

### 6.3. Quy tắc nghiệp vụ
- App **không tự quyết định** trùng lịch, duyệt đơn hay check-in. Server quyết định, app chỉ hiển thị kết quả và xử lý lỗi.
- Giờ dùng múi giờ `Asia/Ho_Chi_Minh`, gửi API dạng ISO-8601.
- Lỗi chung đã có sẵn: 401 → về màn đăng nhập; 403 → báo không có quyền; 409 → báo trùng lịch hoặc dữ liệu đã đổi, tải lại.
- Ẩn/hiện chức năng theo quyền bằng `hasPermission(...)` của TV1. Việc này chỉ là giao diện, server mới là nơi kiểm tra thật.

### 6.4. Hợp đồng API
- Trước khi code, mỗi người viết `docs/api-<module>.md` (endpoint, JSON request/response) cho module mình: `api-booking.md` (Trinh), `api-approval.md` (Phong), `api-checkin.md` (Bao).
- Khi chưa có backend, dùng dữ liệu giả (Mock Data) theo đúng hợp đồng đã viết.

---

## 7. Những thứ KHÔNG được đưa lên GitHub

- `local.properties`, thư mục `.idea/`, `build/`, `.gradle/`.
- File khóa ký app (`*.jks`, `*.keystore`).
- `google-services.json` thật (FCM): TV1 gửi riêng cho từng người, mỗi người tự đặt vào `app/`, **không commit**.
- Mật khẩu, khóa API, token, địa chỉ server nội bộ.

Nếu lỡ commit nhầm: **báo TV1 ngay**, không tự xóa lịch sử. Nếu là mật khẩu hoặc khóa thì phải đổi khóa đó.

---

## 8. Xử lý lỗi thường gặp

| Hiện tượng | Nguyên nhân | Cách xử lý |
|---|---|---|
| `no changes added to commit` | Chưa chạy `git add` | Chạy `git add <đường dẫn>`, kiểm tra `git status` thấy màu xanh rồi commit lại |
| `src refspec ... does not match any` | Hai lệnh bị dính vào nhau | Bấm **Esc** xóa dòng, gõ lại từng lệnh riêng |
| `not a git repository` | Sai thư mục | `cd /d D:\LapTrinhMang` |
| `error: pathspec ... did not match` | Nhánh chưa về máy | `git fetch origin` rồi `git checkout` lại |
| `rejected` / `non-fast-forward` | Có người đã push trước | `git pull --rebase origin <nhánh của mình>` rồi `git push origin <nhánh của mình>` |
| Push báo 403 | Chưa Accept lời mời hoặc đăng nhập sai tài khoản | Accept lời mời; đăng nhập đúng tài khoản GitHub của mình |
| Conflict khi merge | Hai người sửa cùng chỗ | Xem mục 9 |
| Gradle sync lỗi sau khi pull | Cache hoặc khác phiên bản | Bấm **Sync Now**; nếu vẫn lỗi: Build → Clean Project, File → Invalidate Caches → Restart |
| Git báo cả file đổi dù không sửa | Khác kiểu xuống dòng trên Windows | `git config core.autocrlf true` rồi thử lại |
| Không thấy màn hình gì khi chạy app | Chưa sync Gradle hoặc chạy sai | Bấm Sync Now, chọn thiết bị, bấm Run ▶ |

**Mẹo tránh lỗi gõ lệnh:** mỗi lần chỉ gõ **một lệnh**, Enter, đợi chạy xong rồi gõ lệnh tiếp. Đừng dán nhiều lệnh cùng lúc. Nếu dòng lệnh còn chữ thừa thì bấm **Esc** để xóa cả dòng.

---

## 9. Cách xử lý conflict

1. Lấy code mới về nhánh của mình: `git merge develop`.
2. Git báo conflict ở file nào thì mở file đó, tìm các dấu:
```
<<<<<<< HEAD
(code của bạn)
=======
(code từ develop)
>>>>>>> develop
```
3. Giữ phần đúng (thường là giữ cả hai bên cho hợp lý), **xóa các dòng dấu** `<<<<<<<`, `=======`, `>>>>>>>`.
4. Lưu file, rồi:
```cmd
git add <file đã sửa>
git commit -m "fix: xu ly conflict voi develop"
```
5. Chạy `gradlew assembleDebug` để chắc app vẫn build được, rồi push.

Không chắc giữ phần nào thì **hỏi người sửa phần kia** thay vì đoán.

---

## 10. Checklist trước khi push

- [ ] Đã `git pull` và `git merge develop` vào nhánh của mình, không còn conflict.
- [ ] `gradlew assembleDebug` thành công.
- [ ] `gradlew test` thành công.
- [ ] Đã chạy thử app trên emulator hoặc máy thật, chức năng của mình hoạt động.
- [ ] `git status` chỉ có file trong module của mình, không có file lạ (`.idea`, `local.properties`, `build/`).
- [ ] Không sửa file chung ở mục 6.1 khi chưa báo TV1.
- [ ] Commit có tiền tố đúng quy ước, mỗi PR một chức năng.

---

## 11. Lộ trình đề xuất (6 tuần, có thể co giãn)

| Tuần | Trinh | Phong | Bao |
|---|---|---|---|
| 1 | Chốt model dùng chung và `api-booking.md`; danh sách và chi tiết phòng (dữ liệu giả) | `api-approval.md`; màn đơn chờ duyệt (dữ liệu giả) | `api-checkin.md`; thử CameraX + ML Kit, thiết kế màn quét QR và form sự cố |
| 2 | Tìm phòng trống, lịch sử dụng | Duyệt đơn lẻ, lịch toàn trường | Quét QR check-in, check-out |
| 3 | Tạo yêu cầu, yêu cầu của tôi | Nối thông báo kết quả duyệt | Tạo sự cố có ảnh; **cả nhóm chạy thử toàn luồng P0** |
| 4 | Sửa, hủy, đặt lặp, yêu thích, chia sẻ | Duyệt hàng loạt, điều chỉnh lịch, khóa giờ | Danh sách, phân công, nghiệm thu sự cố; đánh giá phòng |
| 5 | CRUD tòa nhà, tầng, loại phòng | Cấu hình duyệt; CRUD danh mục phòng | CRUD thiết bị; thống kê và biểu đồ |
| 6 | Kiểm thử, sửa lỗi | Kiểm thử, sửa lỗi | Xuất báo cáo; kiểm thử; đóng gói APK |

**Mốc quan trọng:** cuối tuần 3, luồng đặt phòng → duyệt → check-in phải chạy được trên máy thật. Mỗi cuối tuần cả nhóm họp 30 phút để gộp `develop` và chạy thử.

---

## 12. Khi cần hỗ trợ

- Việc liên quan khung app, điều hướng, phân quyền, đăng nhập, thông báo, file chung: **hỏi TV1 (Thành)**.
- Việc liên quan model `Room`, `Booking`: **hỏi Trinh**.
- Gặp lỗi Git không hiểu: chụp màn hình CMD gửi vào nhóm, **không tự chạy các lệnh xóa lịch sử** như `git push --force` hay `git reset --hard`.

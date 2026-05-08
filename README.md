# Mobile-LexaApp-EnglishPlatform
An english learning platform for every ages, every peoples.

## Thong tin can thiet de thuc thi chuong trinh

### 1) Moi truong phat trien can co
- **OS**: Windows/macOS/Linux (duoc test tot nhat tren Windows voi Android Studio).
- **Android Studio**: phien ban moi (khuyen nghi Android Studio Koala tro len).
- **JDK**: 17 (phu hop AGP `8.13.2`).
- **Android SDK**:
  - `compileSdk = 34`
  - `targetSdk = 34`
  - `minSdk = 24`
- **Gradle Wrapper**: du an dung `gradle-8.13`.

### 2) Clone va mo du an
```bash
git clone <repo-url>
cd Mobile-LexaApp-EnglishPlatform
```
- Mo thu muc du an bang Android Studio.
- Cho Gradle sync xong truoc khi build/run.

### 3) Cau hinh local bat buoc
Du an doc bien tu file `local.properties`:
- `SERVER_BASE_URL`
- `GEMINI_API_KEY`

Vi du:
```properties
sdk.dir=C\:\\Users\\<your-user>\\AppData\\Local\\Android\\Sdk
SERVER_BASE_URL=http://10.0.2.2:8081/
GEMINI_API_KEY=your_gemini_key
```

Luu y:
- `10.0.2.2` la loopback tu Android Emulator ve may host.
- Neu chay tren thiet bi that, doi `SERVER_BASE_URL` thanh IP LAN cua may chay backend (vd `http://192.168.1.10:8081/`).

### 4) Firebase / Google services
Du an dung Firebase (Auth/Messaging), can them file:
- `app/google-services.json`

Ban can:
- Tao Firebase project.
- Dang ky Android app voi `applicationId = com.home.lexa`.
- Tai `google-services.json` ve va dat vao thu muc `app/`.

### 5) Backend va CSDL
- App mobile **khong chua CSDL server** trong repo nay.
- Can co backend dang chay va truy cap duoc qua `SERVER_BASE_URL`.
- Cac schema/ten CSDL server phu thuoc repo backend (khong nam trong project mobile nay).
- Neu backend yeu cau migration/seed du lieu, thuc hien ben repo backend truoc khi test app.

### 6) Tai khoan de dang nhap
- Can tai khoan test do backend cap (email/password hoac login bang Google/Firebase theo cau hinh backend).
- Nen chuan bi toi thieu:
  - 01 tai khoan hoc vien (student)
  - 01 tai khoan giao vien (teacher)

### 7) Cach run app
- Chon `app` configuration trong Android Studio.
- Chay tren:
  - Android Emulator (khuyen nghi API 30+), hoac
  - Thiet bi that da bat USB debugging.
- Nhan **Run**.

### 8) Kiem tra nhanh sau khi khoi dong
- Dang nhap thanh cong.
- Vao man hinh course.
- Vao speaking practice, ghi am thu 1 cau (can cap quyen microphone).
- Kiem tra app goi duoc API backend (khong bi loi ket noi/401/500).

### 9) Mot so loi cau hinh thuong gap
- **Khong sync duoc Gradle**: kiem tra JDK 17 va internet.
- **Khong goi duoc backend tren emulator**: dung `10.0.2.2` thay vi `localhost`.
- **Firebase loi**: thieu/sai `app/google-services.json`.
- **Loi Gemini**: `GEMINI_API_KEY` rong hoac sai.
- **Mic khong hoat dong**: chua cap quyen `RECORD_AUDIO`.

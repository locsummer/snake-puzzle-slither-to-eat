# 🐍 Snake Puzzle: Slither to Eat! (Landscape Edition)

[![Download APK](https://img.shields.io/badge/Download-APK%20(Direct)-brightgreen?style=for-the-badge&logo=android)](https://github.com/locsummer/snake-puzzle-slither-to-eat/raw/main/snake-puzzle.apk)
[![Platform - Android](https://img.shields.io/badge/Platform-Android%20Kotlin-blue?style=for-the-badge&logo=kotlin)](android/)
[![Platform - iOS](https://img.shields.io/badge/Platform-iOS%20Swift-orange?style=for-the-badge&logo=swift)](ios/)
[![Levels - 50 Verified](https://img.shields.io/badge/Levels-50%20(BFS%20Verified)-purple?style=for-the-badge)](levels/)

> 📥 **Link tải file APK trực tiếp:** [**Click vào đây để tải `snake-puzzle.apk`**](https://github.com/locsummer/snake-puzzle-slither-to-eat/raw/main/snake-puzzle.apk)

---

Một phiên bản tái hiện hoàn chỉnh, nâng cấp toàn diện và tối ưu hóa cao cấp của tựa game giải đố nổi tiếng **Snake Puzzle: Slither to Eat** (lấy cảm hứng từ cơ chế vật lý khối cứng của *Apple Worm / Snakebird*), được xây dựng chuẩn Native song song cho cả hai nền tảng:

- 🍏 **iOS:** Viết 100% bằng **Swift** & **SwiftUI** (Khóa cứng xoay ngang **Landscape**, hỗ trợ iOS 15.0+).
- 🤖 **Android:** Viết 100% bằng **Kotlin** & **Jetpack Compose** (Khóa cứng xoay ngang **Landscape**, hỗ trợ Android 7.0 / SDK 24+).
- 📦 **File cài đặt Android APK sẵn có:** Đã build sẵn file `snake-puzzle.apk` ngay tại thư mục gốc của repository!

---

## 🌟 Những nâng cấp vượt bậc trong phiên bản Master

### 1. 📱 Thiết kế chuẩn Màn hình Ngang (Landscape First)
- Cố định xoay ngang `sensorLandscape` trên Android và `UIInterfaceOrientationLandscape` trên iOS.
- Bố cục HUD công thái học (Ergonomic Landscape UI):
  - **Cụm D-Pad ngón tay cái:** Bố trí bên trái (mặc định) hoặc chuyển sang bên phải trong Cài đặt để phù hợp cho người thuận tay trái/phải.
  - **Khu vực bàn cờ giải đố trung tâm:** Tự động co giãn theo tỷ lệ màn hình (Aspect Ratio Auto-fit), tối ưu vùng hiển thị và an toàn với tai thỏ / Dynamic Island.
  - **Cụm nút tiện ích bên phải:** Undo (hoàn tác không giới hạn), Reset (chơi lại tức thì), Skins (đổi giao diện rắn), Levels (danh sách màn chơi), Settings (cài đặt âm thanh và điều khiển).
  - **Thanh trạng thái trên đỉnh:** Hiển thị tên Thế giới, Level hiện tại, số Táo cần ăn, số Bước đi (Moves), và Banner thông báo trạng thái.

### 2. 🐍 Tạo hình nhân vật Rắn 2.5D sống động & Biểu cảm đáng yêu
- Tạo hình Rắn hoạt hình 2.5D có chiều sâu, bóng đổ và vảy sáng bóng.
- Mắt rắn to tròn, có tròng đen linh hoạt tự động liếc nhìn theo hướng di chuyển tiếp theo.
- Lưỡi rắn thè ra thụt vào tinh nghịch khi trườn, má hồng dễ thương và miệng há to phấn khích khi chuẩn bị ngoạm táo.
- Cơ thể uốn lượn mềm mại khi đổi góc và thu nhỏ dần về phía đuôi.

### 3. 🎭 Hệ thống 5 Skin Rắn độc đáo (Unlockable Snake Skins)
1. 🟢 **Emerald Viper (Lục Xà):** Màu xanh ngọc truyền thống với vảy sáng rực rỡ và đốm bụng vàng chanh.
2. 🟡 **Golden Dragon (Kim Long):** Vảy vàng hoàng gia, mắt rồng hổ phách lấp lánh và sừng rồng mini.
3. 🟣 **Cyber Neon (Điện Tử Viễn Tưởng):** Thân màu Cyan & Magenta phát quang rực rỡ phong cách tương lai.
4. 🍓 **Strawberry Candy (Kẹo Dâu Tây):** Màu hồng phấn pastel ngọt ngào với đốm hạt dâu tây dễ thương.
5. 🌑 **Shadow Phantom (Bóng Đêm Hư Vô):** Thân tím đen huyền bí viền hào quang tím ma thuật bí ẩn.

### 4. 🌍 Hệ thống 50 Level hoàn chỉnh trải dài qua 5 Thế giới (100% Solvable)
Toàn bộ 50 màn chơi đều được phân tích, mô phỏng và **kiểm chứng giải thành công 100% bằng thuật toán BFS (Breadth-First Search)**:
- 🌿 **Thế giới 1: Green Meadows (Level 1–10):** Làm quen với cơ chế trườn, ăn táo, bắc cầu qua hố và vào cổng.
- 💎 **Thế giới 2: Crystal Caverns (Level 11–20):** Hang động pha lê lung linh, mê cung đá hẹp và vượt bẫy gai.
- 🏛️ **Thế giới 3: Sunken Ruins (Level 21–30):** Tàn tích cổ rêu phong ngập nước, câu đố bậc thang đa tầng và thử thách trọng lực.
- ☁️ **Thế giới 4: Sky Citadel (Level 31–40):** Thành cổ trên mây, vực sâu thăm thẳm đòi hỏi tính toán điểm rơi chuẩn xác.
- 🌋 **Thế giới 5: Magma Core (Level 41–50):** Dung nham rực lửa, bẫy gai dày đặc và câu đố đại sư đầy kịch tính.

### 5. 🔊 Bộ vi xử lý Âm thanh Chiptune Tổng hợp (Procedural Audio Synthesizer)
- **Nhạc nền BGM Chiptune:** Tự động phát vòng lặp nhạc nền phiêu lưu retro 8-bit nhẹ nhàng êm ái mà không tốn dung lượng tải tài nguyên âm thanh ngoài.
- **Hiệu ứng âm thanh (SFX):**
  - Âm trườn (Slither whoosh)
  - Âm ngoạm táo giòn tan (Apple crunch)
  - Âm kích hoạt cổng xoáy không gian (Portal open chime)
  - Âm rơi tự do (Gravity whoosh)
  - Âm va chạm bẫy gai (Spike hit)
  - Âm chiến thắng giai điệu vui tươi (Victory arpeggio fanfare)
- Bật/tắt BGM, SFX và Rung haptic độc lập trong mục Settings.

### 6. 🛠️ Khắc phục triệt để toàn bộ lỗi Gameplay
- ✅ **Lỗi không di chuyển:** Tách biệt luồng xử lý phím ảo D-Pad và cử chỉ vuốt, tối ưu hóa vùng chạm không bị xung đột, loại bỏ triệt để hiện tượng đứng hình hoặc nuốt input.
- ✅ **Lỗi ăn táo:** Đồng bộ tọa độ lưới giữa đầu rắn và vị trí quả táo; khi chạm táo, rắn lập tức kích hoạt sự kiện ăn, phát âm thanh, kéo dài thân và kích hoạt hiệu ứng hạt táo.
- ✅ **Lỗi cổng dịch chuyển:** 
  - Khi chưa ăn hết táo, cổng ở trạng thái khóa (hiển thị ổ khóa và vòng tròn ma thuật màu tím). Nếu người chơi trườn vào cổng khi chưa đủ táo, game sẽ hiển thị thông báo: `🔒 Cổng đang khóa! Hãy thu thập đủ táo trước!`.
  - Khi ăn hết quả táo cuối cùng, cổng xoáy bừng sáng rực rỡ và xoay tròn ma thuật kèm âm thanh kích hoạt. Người chơi chỉ cần di chuyển đầu rắn vào tâm cổng để lập tức qua màn.
- ✅ **Cơ chế trọng lực & khối cứng:** Tính toán chính xác toàn bộ điểm tựa của các đốt thân rắn trên nền đất cứng; rơi tự do mượt mà khi mất điểm tựa.

---

## 🎮 Cách Điều Khiển & Phím Tắt

| Thao tác | Mô tả |
|---|---|
| **D-Pad Ảo** | Chạm vào các mũi tên Lên, Xuống, Trái, Phải trên màn hình |
| **Vuốt màn hình (Swipe)** | Vuốt nhanh theo hướng muốn rắn trườn |
| **Undo (↩️)** | Quay lui 1 bước di chuyển (hoàn tác không giới hạn) |
| **Reset (🔄)** | Đặt lại vị trí ban đầu của màn chơi |
| **Skins (🎭)** | Mở tủ đồ chọn trang phục và hiệu ứng cho rắn |
| **Levels (🗺️)** | Xem danh sách 50 màn chơi và số sao đã đạt |
| **Settings (⚙️)** | Tùy chỉnh BGM, SFX, Rung, và Đổi tay cầm D-Pad (Trái / Phải) |

---

## 📁 Cấu trúc Thư mục Dự án

```
snake-puzzle-slither-to-eat/
├── snake-puzzle.apk             # 📦 File APK debug đã build hoàn chỉnh (~15.6MB)
├── levels/
│   └── levels.json              # 50 màn chơi 5 Thế giới (100% BFS Solved)
├── ios/                         # 🍏 Dự án Native iOS (Swift + SwiftUI)
│   ├── Package.swift            # SPM Package Manifest
│   ├── SnakePuzzle.xcodeproj    # File project Xcode chuẩn
│   └── SnakePuzzle/
│       ├── App/                 # SnakePuzzleApp.swift, Info.plist (Landscape locked)
│       ├── Models/              # SnakeSkin, Position, Direction, CellType, GameState, Level
│       ├── Engine/              # SnakePuzzleEngine, LevelRepository
│       ├── Audio/               # SoundManager (CoreAudio / Tone & BGM Synthesizer)
│       ├── Views/               # LandscapeGameHUD, GameBoardRenderer, Modals, Settings...
│       └── Resources/           # levels.json
└── android/                     # 🤖 Dự án Native Android (Kotlin + Jetpack Compose)
    ├── build.gradle.kts
    ├── settings.gradle.kts
    ├── gradlew / gradlew.bat
    └── app/
        ├── build.gradle.kts
        └── src/main/
            ├── AndroidManifest.xml   # Khóa sensorLandscape & Safe Area
            ├── assets/levels.json    # 50 màn chơi đồng bộ
            ├── java/com/snakepuzzle/game/
            │   ├── MainActivity.kt   # Immersive Landscape Fullscreen
            │   ├── model/            # SnakeSkin, Position, Direction, CellType, GameState, Level
            │   ├── engine/           # SnakePuzzleEngine, LevelRepository
            │   ├── audio/            # SoundManager (AudioTrack PCM BGM & SFX Synthesizer)
            │   └── ui/               # LandscapeGameHUD, GameCanvas 2.5D, Modals, Theme
            └── res/values/           # strings.xml, colors.xml, themes.xml
```

---

## 🚀 Cài đặt & Chạy Game

### 1. Cài đặt trực tiếp file APK lên điện thoại Android
File APK đã được biên dịch sẵn tại:
```
snake-puzzle.apk
```
Bạn chỉ cần gửi file `snake-puzzle.apk` vào điện thoại Android qua Zalo / Telegram / Google Drive / cáp USB và nhấn Cài đặt (Install) để trải nghiệm ngay lập tức!

### 2. Build lại mã nguồn Android qua dòng lệnh (Gradle)
```bash
cd android
./gradlew assembleDebug
```
File APK mới sẽ được tạo tại: `android/app/build/outputs/apk/debug/app-debug.apk`.

### 3. Chạy dự án iOS trên macOS (Xcode)
1. Mở file dự án trong Xcode:
   ```bash
   open ios/SnakePuzzle.xcodeproj
   ```
2. Chọn thiết bị giả lập (ví dụ iPhone 15 Pro Max) hoặc cắm iPhone thật.
3. Bấm tổ hợp phím **Cmd + R** để chạy game ở chế độ màn hình ngang tự động.

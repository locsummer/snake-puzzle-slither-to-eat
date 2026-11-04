# 🐍 Snake Puzzle: Slither to Eat!

Một phiên bản tái hiện đầy đủ và chuẩn xác của tựa game giải đố nổi tiếng **Snake Puzzle: Slither to Eat** (tương tự cơ chế *Apple Worm / Snakebird*), được xây dựng chuẩn Native cho cả hai nền tảng di động:

- 🍏 **iOS:** Viết hoàn toàn bằng **Swift** & **SwiftUI** (Hỗ trợ iOS 15.0+).
- 🤖 **Android:** Viết hoàn toàn bằng **Kotlin** & **Jetpack Compose** (Hỗ trợ Android 7.0 / SDK 24+).

---

## 🎮 Cơ chế & Luật chơi (Core Gameplay & Physics)

1. **Di chuyển theo lưới (Grid-based Movement):**
   - Rắn có thể di chuyển 4 hướng: **Lên (Up)**, **Xuống (Down)**, **Trái (Left)**, **Phải (Right)**.
   - Điều khiển bằng thao tác vuốt màn hình (**Swipe**) hoặc sử dụng cụm nút điều hướng ảo (**Virtual D-Pad**).
   - Rắn không thể quay đầu gập ngược 180° vào đốt thân liền kề và không thể cắn vào thân mình.

2. **Ăn táo & Kéo dài thân (Slither to Eat & Grow):**
   - Khi đầu rắn ăn một quả táo (`A`), thân rắn sẽ dài thêm 1 đốt ở lượt tiếp theo.
   - Thân rắn dài ra giúp bắc cầu qua các hố sâu hoặc khe hở, nhưng cũng khiến việc xoay xở trong các đường hầm hẹp trở nên khó khăn hơn.

3. **Trọng lực & Điểm tựa (Gravity & Platforming):**
   - Rắn chịu tác động của trọng lực. Cả cơ thể rắn hoạt động như một khối cứng liên kết.
   - **Quy tắc giá đỡ (Support Rule):** Rắn sẽ giữ thăng bằng trên không nếu có **ít nhất một đốt thân** nằm trực tiếp trên bề mặt khối đất/đá vững chắc (`S`).
   - Nếu không có bất kỳ đốt nào được chống đỡ bên dưới, toàn bộ cơ thể rắn sẽ **rơi tự do (Fall)** từng ô cho đến khi tiếp đất, chạm bẫy gai hoặc rơi ra khỏi bản đồ.

4. **Chướng ngại vật & Cổng dịch chuyển (Hazards & Portal):**
   - **Bẫy gai (`X`):** Chạm vào gai nhọn sẽ khiến rắn bị tiêu diệt ngay lập tức.
   - **Cổng xoáy dịch chuyển (`O`):** Cửa thoát hiểm của màn chơi. Khi bạn ăn đủ số lượng táo trong màn, cổng xoáy không gian sẽ kích hoạt. Di chuyển đầu rắn vào tâm cổng xoáy để hoàn thành màn chơi!

5. **Tính năng hỗ trợ người chơi:**
   - ↩️ **Undo không giới hạn (Unlimited Undos):** Cho phép bạn quay lại từng bước đi trước đó để thử nghiệm các hướng đi khác nhau.
   - 🔄 **Restart tức thì (Instant Reset):** Đặt lại màn chơi ngay lập tức.
   - ⭐ **Hệ thống đánh giá 3 sao:** Đánh giá số bước di chuyển tối ưu cho mỗi màn.
   - 🎵 **Hiệu ứng âm thanh & Rung phản hồi (Synthesized SFX & Haptics):** Tự động phát âm thanh tiếng trườn, tiếng cắn táo giòn tan, tiếng rơi, tiếng chiến thắng arpeggio vui nhộn và rung xúc giác không cần tải file ngoài.

---

## 📁 Cấu trúc Thư mục Dự án

```
snake-puzzle-slither-to-eat/
├── levels/
│   └── levels.json               # 15 màn chơi được thiết kế & kiểm chứng bằng thuật toán BFS
├── ios/                          # 🍏 Dự án Native iOS (Swift + SwiftUI)
│   ├── Package.swift             # Hỗ trợ Swift Package Manager
│   ├── SnakePuzzle.xcodeproj     # File dự án Xcode chuẩn
│   └── SnakePuzzle/
│       ├── App/
│       │   ├── SnakePuzzleApp.swift
│       │   └── Info.plist
│       ├── Models/               # Position, Direction, CellType, GameState, Level
│       ├── Engine/               # SnakePuzzleEngine, LevelRepository
│       ├── Audio/                # SoundManager (CoreAudio / Tone Synthesizer)
│       ├── Views/                # GameView, GameBoardRenderer, VirtualDPadView, Modals...
│       └── Resources/            # levels.json
└── android/                      # 🤖 Dự án Native Android (Kotlin + Jetpack Compose)
    ├── build.gradle.kts
    ├── settings.gradle.kts
    ├── gradlew / gradlew.bat
    └── app/
        ├── build.gradle.kts
        └── src/main/
            ├── AndroidManifest.xml
            ├── assets/levels.json
            ├── java/com/snakepuzzle/game/
            │   ├── MainActivity.kt
            │   ├── model/        # Position, Direction, CellType, GameState, Level
            │   ├── engine/       # SnakePuzzleEngine, LevelRepository
            │   ├── audio/        # SoundManager (AudioTrack PCM Synthesizer)
            │   └── ui/           # Compose GameScreen, Canvas Renderer, DPad, Dialogs
            └── res/values/       # strings.xml, colors.xml, themes.xml
```

---

## 🚀 Hướng dẫn Chạy & Biên dịch

### 1. iOS (Swift / Xcode)

**Yêu cầu:** macOS với Xcode 15+ (iOS 15.0+ Deployment Target).

**Cách chạy bằng Xcode:**
1. Mở thư mục `ios/SnakePuzzle.xcodeproj` trong Xcode:
   ```bash
   open ios/SnakePuzzle.xcodeproj
   ```
2. Chọn Simulator (ví dụ: iPhone 15, iPhone 16) hoặc thiết bị thật.
3. Nhấn **Run (`Cmd + R`)** để khởi động và chơi game.

**Cách build bằng Swift Package Manager (Command Line):**
```bash
cd ios
swift build
```

---

### 2. Android (Kotlin / Android Studio)

**Yêu cầu:** Android Studio Hedgehog / Iguana / Jellyfish (hoặc mới hơn), JDK 17+.

**Cách chạy bằng Android Studio:**
1. Khởi động Android Studio.
2. Chọn **Open** và dẫn tới thư mục `d:\snake-puzzle-slither-to-eat\android`.
3. Chờ Gradle đồng bộ (Sync Project with Gradle Files).
4. Chọn máy ảo Android Emulator hoặc cắm điện thoại đã bật USB Debugging.
5. Nhấn nút **Run (`Shift + F10`)**.

**Cách build APK qua dòng lệnh (Gradle):**
```bash
cd android
./gradlew assembleDebug
```
*File APK xuất ra tại: `android/app/build/outputs/apk/debug/app-debug.apk`.*

---

## 🗺️ Danh sách 15 Màn chơi & Lời giải chuẩn (Verified 100%)

Tất cả 15 màn chơi đều đã được kiểm tra tính giải được qua thuật toán tìm kiếm theo chiều rộng (BFS):

| Màn | Tên Màn Chơi | Kích Thước | Số Táo | Số bước tối ưu | Chuỗi bước di chuyển giải màn |
|:---:|:---|:---:|:---:|:---:|:---|
| **1** | First Bite | 8 x 5 | 1 | 8 | `L L U R R R R R` |
| **2** | Across the Gap | 9 x 5 | 1 | 5 | `R R R R R` |
| **3** | Safe Fall | 8 x 6 | 1 | 7 | `R R R U U R R` |
| **4** | Spike Crossing | 9 x 6 | 1 | 5 | `R R R R R` |
| **5** | Double Growth | 10 x 6 | 2 | 16 | `R R R R U L L L L U R R R R R R` |
| **6** | Terraces | 9 x 6 | 1 | 9 | `U R R R U U R R R` |
| **7** | The High Portal | 9 x 7 | 2 | 9 | `R U R R U U R R R` |
| **8** | The S-Bend | 9 x 6 | 1 | 12 | `R R R R R D D L L L L L` |
| **9** | The Pit | 9 x 6 | 1 | 7 | `R R R U U R D` |
| **10** | Pillar Steps | 10 x 6 | 2 | 7 | `R R R R R R R` |
| **11** | The Bridge | 11 x 6 | 2 | 10 | `U R R R R U R R R R` |
| **12** | Two Level Pit | 9 x 7 | 2 | 12 | `R R R R R D D L U L D R` |
| **13** | Spike Overpass | 11 x 6 | 1 | 7 | `R R R R R R R` |
| **14** | Zig Zag | 10 x 7 | 2 | 14 | `D R R R D D R R R U U U R R` |
| **15** | Grandmaster | 11 x 8 | 2 | 21 | `R R R R R R R U L L L L L L L U R R D D L` |

*(Ký hiệu: `U` = Up, `D` = Down, `L` = Left, `R` = Right)*

---

## 🎨 Thiết kế Đồ hoạ & Điểm nhấn UX
- **Biểu cảm của Rắn:** Mắt rắn sinh động, con ngươi tự động liếc nhìn theo hướng di chuyển, má hồng dễ thương.
- **Môi trường & Địa hình:** Khối đất khối đá màu sắc ấm áp, thảm cỏ xanh mượt viền phía trên bề mặt.
- **Cổng xoáy không gian:** Hiệu ứng xoay tròn huyền ảo và phát sáng rực rỡ khi mở khóa.
- **Quả táo:** Hiệu ứng nảy nhẹ (squash & stretch pulse) thu hút ánh nhìn.

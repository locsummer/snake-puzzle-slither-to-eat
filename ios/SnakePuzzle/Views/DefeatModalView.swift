import SwiftUI

public struct DefeatModalView: View {
    public let reason: String
    public let score: Int
    public let highScore: Int
    public let levelReached: Int
    public let onRestartFromBeginning: () -> Void
    public let onViewScoreboard: () -> Void

    public var body: some View {
        ZStack {
            Color.black.opacity(0.55)
                .ignoresSafeArea()

            VStack(spacing: 14) {
                Text("💀 HẾT 3 TIM - GAME OVER!")
                    .font(.system(size: 22, weight: .black, design: .rounded))
                    .foregroundColor(Color(red: 0.88, green: 0.15, blue: 0.15))

                Text("\(reason)\nBạn đã dùng hết cả 3 tim!")
                    .font(.system(size: 14, weight: .medium, design: .rounded))
                    .foregroundColor(GameTheme.textSecondary)
                    .multilineTextAlignment(.center)

                // Score Board Card
                VStack(spacing: 6) {
                    Text("TỔNG ĐIỂM: \(score) PTS")
                        .font(.system(size: 18, weight: .black, design: .rounded))
                        .foregroundColor(Color(red: 0.60, green: 0.11, blue: 0.11))

                    Text("Màn chơi dừng lại: LV \(levelReached)")
                        .font(.system(size: 13, weight: .bold, design: .rounded))
                        .foregroundColor(Color(red: 0.28, green: 0.33, blue: 0.41))

                    Text("✅ Đã lưu kết quả vào Bảng Điểm Kỷ Lục!")
                        .font(.system(size: 12, weight: .semibold, design: .rounded))
                        .foregroundColor(Color(red: 0.08, green: 0.50, blue: 0.24))
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 14)
                .frame(maxWidth: .infinity)
                .background(Color(red: 0.99, green: 0.95, blue: 0.95))
                .cornerRadius(14)

                // Action Buttons
                VStack(spacing: 10) {
                    Button(action: onRestartFromBeginning) {
                        Text("🔄 CHƠI LẠI TỪ LEVEL 1 (HỒI 3 TIM)")
                            .font(.system(size: 14, weight: .black, design: .rounded))
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 12)
                            .background(Color(red: 0.86, green: 0.15, blue: 0.15))
                            .cornerRadius(12)
                    }

                    Button(action: onViewScoreboard) {
                        Text("🏆 Xem Bảng Điểm Kỷ Lục")
                            .font(.system(size: 14, weight: .bold, design: .rounded))
                            .foregroundColor(Color(red: 0.12, green: 0.23, blue: 0.54))
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 11)
                            .background(Color(white: 0.94))
                            .cornerRadius(12)
                    }
                }
            }
            .padding(24)
            .background(Color.white)
            .cornerRadius(22)
            .frame(maxWidth: 420)
            .shadow(radius: 16)
        }
    }
}

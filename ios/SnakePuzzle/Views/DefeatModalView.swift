import SwiftUI

public struct DefeatModalView: View {
    public let reason: String
    public let score: Int
    public let highScore: Int
    public let canUndo: Bool
    public let onUndo: () -> Void
    public let onRestartFromBeginning: () -> Void
    public let onSelectLevel: () -> Void

    public var body: some View {
        ZStack {
            Color.black.opacity(0.55)
                .ignoresSafeArea()

            VStack(spacing: 14) {
                Text("💀 BẠN ĐÃ THUA CUỘC!")
                    .font(.system(size: 24, weight: .black, design: .rounded))
                    .foregroundColor(Color(red: 0.88, green: 0.15, blue: 0.15))

                Text(reason)
                    .font(.system(size: 14, weight: .medium, design: .rounded))
                    .foregroundColor(GameTheme.textSecondary)
                    .multilineTextAlignment(.center)

                // Score Board Card
                VStack(spacing: 6) {
                    Text("ĐIỂM LƯỢT NÀY: \(score) PTS")
                        .font(.system(size: 16, weight: .black, design: .rounded))
                        .foregroundColor(Color(red: 0.12, green: 0.16, blue: 0.23))

                    Text("👑 Kỷ lục: \(highScore) PTS")
                        .font(.system(size: 13, weight: .bold, design: .rounded))
                        .foregroundColor(Color(red: 0.71, green: 0.33, blue: 0.04))

                    Text("⚠️ Luật chơi: Chết thì phải chơi lại từ Level 1!")
                        .font(.system(size: 12, weight: .bold, design: .rounded))
                        .foregroundColor(Color(red: 0.86, green: 0.15, blue: 0.15))
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 12)
                .frame(maxWidth: .infinity)
                .background(Color(red: 0.97, green: 0.98, blue: 0.99))
                .cornerRadius(14)

                // Action Buttons
                VStack(spacing: 10) {
                    Button(action: onRestartFromBeginning) {
                        Text("🔄 CHƠI LẠI TỪ ĐẦU (LEVEL 1)")
                            .font(.system(size: 15, weight: .black, design: .rounded))
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 12)
                            .background(Color(red: 0.86, green: 0.15, blue: 0.15))
                            .cornerRadius(12)
                    }

                    HStack(spacing: 10) {
                        if canUndo {
                            Button(action: onUndo) {
                                HStack {
                                    Image(systemName: "arrow.uturn.backward")
                                    Text("Hoàn tác (Undo)")
                                }
                                .font(.system(size: 13, weight: .bold, design: .rounded))
                                .foregroundColor(.white)
                                .frame(maxWidth: .infinity)
                                .padding(.vertical, 10)
                                .background(Color(red: 0.15, green: 0.39, blue: 0.92))
                                .cornerRadius(12)
                            }
                        }

                        Button(action: onSelectLevel) {
                            Text("Màn chơi")
                                .font(.system(size: 13, weight: .bold, design: .rounded))
                                .foregroundColor(GameTheme.textSecondary)
                                .frame(maxWidth: .infinity)
                                .padding(.vertical, 10)
                                .background(Color(white: 0.94))
                                .cornerRadius(12)
                        }
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

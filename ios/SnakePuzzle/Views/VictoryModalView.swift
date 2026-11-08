import SwiftUI

public struct VictoryModalView: View {
    public let levelId: Int
    public let levelName: String
    public let world: String
    public let moves: Int
    public let optimalMoves: Int
    public let stars: Int
    public let levelScore: Int
    public let totalScore: Int
    public let highScore: Int
    public let isNewHighScore: Bool
    public let hasNextLevel: Bool
    public let onNextLevel: () -> Void
    public let onReplay: () -> Void
    public let onSelectLevel: () -> Void

    public var body: some View {
        ZStack {
            Color.black.opacity(0.5)
                .ignoresSafeArea()

            VStack(spacing: 14) {
                Text("🎉 HOÀN THÀNH MÀN CHƠI!")
                    .font(.system(size: 20, weight: .black, design: .rounded))
                    .foregroundColor(Color(red: 0.09, green: 0.64, blue: 0.29))

                Text("Màn \(levelId): \(levelName)")
                    .font(.system(size: 14, weight: .semibold, design: .rounded))
                    .foregroundColor(GameTheme.textSecondary)

                // Stars display
                HStack(spacing: 10) {
                    ForEach(1...3, id: \.self) { starIndex in
                        Image(systemName: starIndex <= stars ? "star.fill" : "star")
                            .font(.system(size: 38))
                            .foregroundColor(starIndex <= stars ? GameTheme.starActive : GameTheme.starInactive)
                            .shadow(color: starIndex <= stars ? GameTheme.starActive.opacity(0.5) : Color.clear, radius: 6, x: 0, y: 2)
                    }
                }

                // Score Card
                VStack(spacing: 4) {
                    Text("+\(levelScore) PTS (Bước đi: \(moves) / \(optimalMoves))")
                        .font(.system(size: 13, weight: .bold, design: .rounded))
                        .foregroundColor(Color(red: 0.08, green: 0.50, blue: 0.24))

                    Text("TỔNG ĐIỂM: \(totalScore) PTS")
                        .font(.system(size: 16, weight: .black, design: .rounded))
                        .foregroundColor(Color(red: 0.09, green: 0.40, blue: 0.20))

                    if isNewHighScore {
                        Text("🔥 KỶ LỤC MỚI (NEW RECORD)! 🔥")
                            .font(.system(size: 12, weight: .black, design: .rounded))
                            .foregroundColor(Color(red: 0.71, green: 0.33, blue: 0.04))
                    } else {
                        Text("Kỷ lục hiện tại: \(highScore) PTS")
                            .font(.system(size: 12, weight: .semibold, design: .rounded))
                            .foregroundColor(Color(red: 0.39, green: 0.45, blue: 0.55))
                    }
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 10)
                .frame(maxWidth: .infinity)
                .background(Color(red: 0.94, green: 0.99, blue: 0.96))
                .cornerRadius(14)

                HStack(spacing: 10) {
                    if hasNextLevel {
                        Button(action: onNextLevel) {
                            HStack {
                                Text("Màn Tiếp Theo")
                                Image(systemName: "arrow.right.circle.fill")
                            }
                            .font(.system(size: 14, weight: .bold, design: .rounded))
                            .foregroundColor(.white)
                            .padding(.horizontal, 14)
                            .padding(.vertical, 12)
                            .background(Color(red: 0.09, green: 0.64, blue: 0.29))
                            .cornerRadius(12)
                        }
                    }

                    Button(action: onReplay) {
                        Text("Chơi Lại")
                            .font(.system(size: 14, weight: .bold, design: .rounded))
                            .foregroundColor(GameTheme.textPrimary)
                            .padding(.horizontal, 14)
                            .padding(.vertical, 12)
                            .background(Color(white: 0.94))
                            .cornerRadius(12)
                    }

                    Button(action: onSelectLevel) {
                        Text("Danh Sách")
                            .font(.system(size: 14, weight: .bold, design: .rounded))
                            .foregroundColor(GameTheme.textSecondary)
                            .padding(.horizontal, 14)
                            .padding(.vertical, 12)
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

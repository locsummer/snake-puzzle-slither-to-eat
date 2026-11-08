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
    public let elapsedSeconds: Int
    public let timeBonus: Int
    public let isNewHighScore: Bool
    public let hasNextLevel: Bool
    public let onNextLevel: () -> Void
    public let onReplay: () -> Void
    public let onSelectLevel: () -> Void

    public var body: some View {
        ZStack {
            Color.black.opacity(0.5)
                .ignoresSafeArea()

            VStack(spacing: 10) {
                // Header with Title and Stars in one row
                HStack(alignment: .center) {
                    VStack(alignment: .leading, spacing: 2) {
                        Text("🎉 HOÀN THÀNH MÀN CHƠI!")
                            .font(.system(size: 17, weight: .black, design: .rounded))
                            .foregroundColor(Color(red: 0.09, green: 0.64, blue: 0.29))

                        Text("Màn \(levelId): \(levelName) (\(world))")
                            .font(.system(size: 12, weight: .semibold, design: .rounded))
                            .foregroundColor(GameTheme.textSecondary)
                    }

                    Spacer()

                    // Stars display
                    HStack(spacing: 4) {
                        ForEach(1...3, id: \.self) { starIndex in
                            Image(systemName: starIndex <= stars ? "star.fill" : "star")
                                .font(.system(size: 26))
                                .foregroundColor(starIndex <= stars ? GameTheme.starActive : GameTheme.starInactive)
                        }
                    }
                }

                // Score Card with Time Bonus (Compact Row)
                HStack {
                    Text("⏱️ \(elapsedSeconds)s (+\(timeBonus) PTS)")
                        .font(.system(size: 12, weight: .bold, design: .rounded))
                        .foregroundColor(Color(red: 0.08, green: 0.50, blue: 0.24))

                    Spacer()

                    Text("MÀN NÀY: +\(levelScore) PTS")
                        .font(.system(size: 12, weight: .black, design: .rounded))
                        .foregroundColor(Color(red: 0.09, green: 0.40, blue: 0.20))

                    Spacer()

                    Text("TỔNG: \(totalScore) PTS")
                        .font(.system(size: 14, weight: .black, design: .rounded))
                        .foregroundColor(Color(red: 0.08, green: 0.33, blue: 0.18))
                }
                .padding(.horizontal, 14)
                .padding(.vertical, 8)
                .frame(maxWidth: .infinity)
                .background(Color(red: 0.94, green: 0.99, blue: 0.96))
                .cornerRadius(12)

                if isNewHighScore {
                    Text("🔥 KỶ LỤC MỚI (NEW RECORD)! 🔥")
                        .font(.system(size: 11, weight: .black, design: .rounded))
                        .foregroundColor(Color(red: 0.71, green: 0.33, blue: 0.04))
                }

                // Action Buttons Row (Always 100% visible & prominent!)
                HStack(spacing: 10) {
                    Button(action: onNextLevel) {
                        HStack {
                            Text(hasNextLevel ? "NEXT LEVEL ▶" : "Chơi Lại")
                            Image(systemName: "arrow.right.circle.fill")
                        }
                        .font(.system(size: 15, weight: .black, design: .rounded))
                        .foregroundColor(.white)
                        .padding(.horizontal, 16)
                        .padding(.vertical, 12)
                        .frame(maxWidth: .infinity)
                        .background(Color(red: 0.09, green: 0.64, blue: 0.29))
                        .cornerRadius(12)
                        .shadow(color: Color.black.opacity(0.15), radius: 3, x: 0, y: 2)
                    }

                    Button(action: onReplay) {
                        Text("Chơi Lại")
                            .font(.system(size: 13, weight: .bold, design: .rounded))
                            .foregroundColor(GameTheme.textPrimary)
                            .padding(.horizontal, 12)
                            .padding(.vertical, 12)
                            .background(Color(white: 0.94))
                            .cornerRadius(12)
                    }

                    Button(action: onSelectLevel) {
                        Text("Chọn Màn")
                            .font(.system(size: 13, weight: .bold, design: .rounded))
                            .foregroundColor(GameTheme.textSecondary)
                            .padding(.horizontal, 12)
                            .padding(.vertical, 12)
                            .background(Color(white: 0.94))
                            .cornerRadius(12)
                    }
                }
                .padding(.top, 4)
            }
            .padding(18)
            .background(Color.white)
            .cornerRadius(20)
            .frame(maxWidth: 500)
            .shadow(radius: 16)
        }
    }
}

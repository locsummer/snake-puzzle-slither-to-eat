import SwiftUI

public struct VictoryModalView: View {
    public let levelId: Int
    public let levelName: String
    public let world: String
    public let moves: Int
    public let optimalMoves: Int
    public let stars: Int
    public let hasNextLevel: Bool
    public let onNextLevel: () -> Void
    public let onReplay: () -> Void
    public let onSelectLevel: () -> Void

    public var body: some View {
        ZStack {
            Color.black.opacity(0.45)
                .ignoresSafeArea()

            VStack(spacing: 16) {
                Text("🎉 LEVEL COMPLETE!")
                    .font(.system(size: 22, weight: .black, design: .rounded))
                    .foregroundColor(GameTheme.textPrimary)

                Text("Level \(levelId): \(levelName) (\(world))")
                    .font(.system(size: 14, weight: .semibold, design: .rounded))
                    .foregroundColor(GameTheme.textSecondary)

                // Stars display
                HStack(spacing: 10) {
                    ForEach(1...3, id: \.self) { starIndex in
                        Image(systemName: starIndex <= stars ? "star.fill" : "star")
                            .font(.system(size: 40))
                            .foregroundColor(starIndex <= stars ? GameTheme.starActive : GameTheme.starInactive)
                            .shadow(color: starIndex <= stars ? GameTheme.starActive.opacity(0.5) : Color.clear, radius: 6, x: 0, y: 2)
                    }
                }

                Text("Moves: \(moves) (Optimal: \(optimalMoves))")
                    .font(.system(size: 15, weight: .bold, design: .rounded))
                    .foregroundColor(GameTheme.textPrimary)

                HStack(spacing: 10) {
                    if hasNextLevel {
                        Button(action: onNextLevel) {
                            HStack {
                                Text("Next Level")
                                Image(systemName: "arrow.right.circle.fill")
                            }
                            .font(.system(size: 15, weight: .bold, design: .rounded))
                            .foregroundColor(.white)
                            .padding(.horizontal, 14)
                            .padding(.vertical, 12)
                            .background(Color(red: 0.18, green: 0.80, blue: 0.44))
                            .cornerRadius(12)
                        }
                    }

                    Button(action: onReplay) {
                        Text("Replay")
                            .font(.system(size: 15, weight: .bold, design: .rounded))
                            .foregroundColor(GameTheme.textPrimary)
                            .padding(.horizontal, 14)
                            .padding(.vertical, 12)
                            .background(Color(white: 0.94))
                            .cornerRadius(12)
                    }

                    Button(action: onSelectLevel) {
                        Text("Levels")
                            .font(.system(size: 15, weight: .bold, design: .rounded))
                            .foregroundColor(GameTheme.textPrimary)
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

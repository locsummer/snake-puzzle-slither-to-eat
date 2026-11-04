import SwiftUI

public struct VictoryModalView: View {
    public let levelId: Int
    public let levelName: String
    public let moves: Int
    public let stars: Int
    public let hasNextLevel: Bool
    public let onNextLevel: () -> Void
    public let onReplay: () -> Void
    public let onSelectLevel: () -> Void

    public var body: some View {
        ZStack {
            Color.black.opacity(0.45)
                .ignoresSafeArea()

            VStack(spacing: 20) {
                // Header
                Text("🎉 LEVEL COMPLETE!")
                    .font(.system(size: 24, weight: .black, design: .rounded))
                    .foregroundColor(GameTheme.textPrimary)

                Text(levelName)
                    .font(.system(size: 16, weight: .semibold, design: .rounded))
                    .foregroundColor(GameTheme.textSecondary)

                // Stars display
                HStack(spacing: 12) {
                    ForEach(1...3, id: \.self) { starIndex in
                        Image(systemName: starIndex <= stars ? "star.fill" : "star")
                            .font(.system(size: 44))
                            .foregroundColor(starIndex <= stars ? GameTheme.starActive : GameTheme.starInactive)
                            .shadow(color: starIndex <= stars ? GameTheme.starActive.opacity(0.5) : Color.clear, radius: 8, x: 0, y: 3)
                    }
                }
                .padding(.vertical, 4)

                // Moves counter
                VStack(spacing: 4) {
                    Text("Moves Taken: \(moves)")
                        .font(.system(size: 18, weight: .bold, design: .rounded))
                        .foregroundColor(GameTheme.textPrimary)
                }

                // Action Buttons
                VStack(spacing: 12) {
                    if hasNextLevel {
                        Button(action: onNextLevel) {
                            HStack {
                                Text("Next Level")
                                Image(systemName: "arrow.right.circle.fill")
                            }
                            .font(.system(size: 18, weight: .bold, design: .rounded))
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 14)
                            .background(
                                LinearGradient(
                                    colors: [Color(red: 0.25, green: 0.80, blue: 0.35), Color(red: 0.15, green: 0.65, blue: 0.25)],
                                    startPoint: .top,
                                    endPoint: .bottom
                                )
                            )
                            .cornerRadius(14)
                            .shadow(color: Color.green.opacity(0.35), radius: 6, x: 0, y: 3)
                        }
                    }

                    HStack(spacing: 12) {
                        Button(action: onReplay) {
                            HStack {
                                Image(systemName: "arrow.counterclockwise")
                                Text("Replay")
                            }
                            .font(.system(size: 16, weight: .bold, design: .rounded))
                            .foregroundColor(GameTheme.textPrimary)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 12)
                            .background(Color(white: 0.94))
                            .cornerRadius(12)
                        }

                        Button(action: onSelectLevel) {
                            HStack {
                                Image(systemName: "square.grid.2x2")
                                Text("Levels")
                            }
                            .font(.system(size: 16, weight: .bold, design: .rounded))
                            .foregroundColor(GameTheme.textPrimary)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 12)
                            .background(Color(white: 0.94))
                            .cornerRadius(12)
                        }
                    }
                }
                .padding(.top, 8)
            }
            .padding(28)
            .background(
                RoundedRectangle(cornerRadius: 24)
                    .fill(Color.white)
                    .shadow(color: Color.black.opacity(0.2), radius: 20, x: 0, y: 10)
            )
            .padding(.horizontal, 32)
        }
        .transition(.opacity.combined(with: .scale(scale: 0.9)))
    }
}

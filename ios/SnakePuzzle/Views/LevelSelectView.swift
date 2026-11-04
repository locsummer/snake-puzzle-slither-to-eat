import SwiftUI

public struct LevelSelectView: View {
    @ObservedObject var repository = LevelRepository.shared
    public let onSelectLevel: (Level) -> Void
    public let onClose: () -> Void

    private let columns = [
        GridItem(.adaptive(minimum: 88, maximum: 110), spacing: 14)
    ]

    public var body: some View {
        NavigationView {
            ZStack {
                LinearGradient(
                    colors: [GameTheme.backgroundTop, GameTheme.backgroundBottom],
                    startPoint: .top,
                    endPoint: .bottom
                )
                .ignoresSafeArea()

                ScrollView {
                    LazyVGrid(columns: columns, spacing: 14) {
                        ForEach(repository.levels) { lvl in
                            let unlocked = repository.isLevelUnlocked(lvl.id)
                            let stars = repository.stars(for: lvl.id)
                            let best = repository.bestMoves(for: lvl.id)

                            Button(action: {
                                if unlocked {
                                    SoundManager.shared.playClick()
                                    onSelectLevel(lvl)
                                }
                            }) {
                                VStack(spacing: 6) {
                                    if unlocked {
                                        Text("\(lvl.id)")
                                            .font(.system(size: 26, weight: .black, design: .rounded))
                                            .foregroundColor(GameTheme.textPrimary)

                                        Text(lvl.name)
                                            .font(.system(size: 10, weight: .semibold, design: .rounded))
                                            .foregroundColor(GameTheme.textSecondary)
                                            .lineLimit(1)
                                            .minimumScaleFactor(0.8)

                                        // Stars
                                        HStack(spacing: 2) {
                                            ForEach(1...3, id: \.self) { s in
                                                Image(systemName: s <= stars ? "star.fill" : "star")
                                                    .font(.system(size: 11))
                                                    .foregroundColor(s <= stars ? GameTheme.starActive : GameTheme.starInactive)
                                            }
                                        }

                                        if best > 0 {
                                            Text("\(best) moves")
                                                .font(.system(size: 9, weight: .medium, design: .rounded))
                                                .foregroundColor(Color.gray)
                                        }
                                    } else {
                                        Image(systemName: "lock.fill")
                                            .font(.system(size: 24))
                                            .foregroundColor(Color.gray.opacity(0.5))

                                        Text("Level \(lvl.id)")
                                            .font(.system(size: 11, weight: .medium, design: .rounded))
                                            .foregroundColor(Color.gray.opacity(0.6))
                                    }
                                }
                                .frame(maxWidth: .infinity, minHeight: 96)
                                .background(
                                    RoundedRectangle(cornerRadius: 16)
                                        .fill(unlocked ? Color.white : Color.white.opacity(0.5))
                                        .shadow(color: Color.black.opacity(unlocked ? 0.08 : 0.02), radius: 6, x: 0, y: 3)
                                )
                                .overlay(
                                    RoundedRectangle(cornerRadius: 16)
                                        .stroke(unlocked ? Color.white : Color.clear, lineWidth: 1.5)
                                )
                            }
                            .buttonStyle(PlainButtonStyle())
                            .disabled(!unlocked)
                        }
                    }
                    .padding(20)
                }
            }
            .navigationTitle("Select Level")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button(action: onClose) {
                        Image(systemName: "xmark.circle.fill")
                            .font(.system(size: 24))
                            .foregroundColor(GameTheme.textSecondary)
                    }
                }
            }
        }
    }
}

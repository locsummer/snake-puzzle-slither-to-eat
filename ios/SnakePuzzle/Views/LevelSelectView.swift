import SwiftUI

public struct LevelSelectView: View {
    @ObservedObject var repository = LevelRepository.shared
    public let onSelectLevel: (Level) -> Void
    public let onClose: () -> Void

    private let worlds = [
        "Green Meadows",
        "Crystal Caverns",
        "Sunken Ruins",
        "Sky Citadel",
        "Magma Core"
    ]

    @State private var selectedWorldIndex: Int = 0

    private let columns = [
        GridItem(.adaptive(minimum: 90, maximum: 110), spacing: 12)
    ]

    public var body: some View {
        NavigationView {
            ZStack {
                Color(white: 0.96).ignoresSafeArea()

                VStack(spacing: 0) {
                    // World Tabs Selector
                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: 8) {
                            ForEach(0..<worlds.count, id: \.self) { idx in
                                let isSelected = selectedWorldIndex == idx
                                Button(action: {
                                    selectedWorldIndex = idx
                                }) {
                                    Text("World \(idx + 1): \(worlds[idx])")
                                        .font(.system(size: 13, weight: isSelected ? .bold : .medium))
                                        .padding(.horizontal, 12)
                                        .padding(.vertical, 8)
                                        .background(isSelected ? Color(red: 0.18, green: 0.80, blue: 0.44) : Color.white)
                                        .foregroundColor(isSelected ? .white : GameTheme.textPrimary)
                                        .cornerRadius(12)
                                        .shadow(color: Color.black.opacity(isSelected ? 0.15 : 0.05), radius: 3)
                                }
                            }
                        }
                        .padding(.horizontal, 16)
                        .padding(.vertical, 10)
                    }
                    .background(Color.white)

                    // Levels Grid
                    let currentWorld = worlds[selectedWorldIndex]
                    let levelsInWorld = repository.levels.filter { $0.world == currentWorld }

                    ScrollView {
                        LazyVGrid(columns: columns, spacing: 12) {
                            ForEach(levelsInWorld) { lvl in
                                let unlocked = repository.isLevelUnlocked(lvl.id)
                                let stars = repository.stars(for: lvl.id)
                                let best = repository.bestMoves(for: lvl.id)

                                Button(action: {
                                    if unlocked {
                                        SoundManager.shared.playClick()
                                        onSelectLevel(lvl)
                                    }
                                }) {
                                    VStack(spacing: 4) {
                                        if unlocked {
                                            Text("\(lvl.id)")
                                                .font(.system(size: 22, weight: .black, design: .rounded))
                                                .foregroundColor(GameTheme.textPrimary)

                                            Text(lvl.name)
                                                .font(.system(size: 10, weight: .semibold))
                                                .foregroundColor(GameTheme.textSecondary)
                                                .lineLimit(1)

                                            HStack(spacing: 2) {
                                                ForEach(1...3, id: \.self) { s in
                                                    Image(systemName: s <= stars ? "star.fill" : "star")
                                                        .font(.system(size: 10))
                                                        .foregroundColor(s <= stars ? GameTheme.starActive : GameTheme.starInactive)
                                                }
                                            }

                                            if best > 0 {
                                                Text("\(best) moves")
                                                    .font(.system(size: 9))
                                                    .foregroundColor(.gray)
                                            }
                                        } else {
                                            Image(systemName: "lock.fill")
                                                .font(.system(size: 20))
                                                .foregroundColor(Color.gray.opacity(0.5))
                                            Text("LV \(lvl.id)")
                                                .font(.system(size: 10))
                                                .foregroundColor(.gray)
                                        }
                                    }
                                    .frame(maxWidth: .infinity, minHeight: 80)
                                    .background(unlocked ? Color.white : Color.white.opacity(0.5))
                                    .cornerRadius(14)
                                    .shadow(color: Color.black.opacity(unlocked ? 0.08 : 0.02), radius: 4)
                                }
                                .disabled(!unlocked)
                            }
                        }
                        .padding(16)
                    }
                }
            }
            .navigationTitle("Select Level (50 Levels)")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button(action: onClose) {
                        Image(systemName: "xmark.circle.fill")
                            .font(.system(size: 22))
                            .foregroundColor(GameTheme.textSecondary)
                    }
                }
            }
        }
    }
}

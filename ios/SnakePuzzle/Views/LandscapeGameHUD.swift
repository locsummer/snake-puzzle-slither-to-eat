import SwiftUI

public struct LandscapeGameHUD<Content: View>: View {
    public let level: Level
    public let state: GameState
    public let canUndo: Bool
    public let isDpadOnLeft: Bool
    public let onDirection: (Direction) -> Void
    public let onUndo: () -> Void
    public let onRestart: () -> Void
    public let onOpenLevelSelect: () -> Void
    public let onOpenSkinSelect: () -> Void
    public let onOpenSettings: () -> Void
    public let content: () -> Content

    public init(
        level: Level,
        state: GameState,
        canUndo: Bool,
        isDpadOnLeft: Bool,
        onDirection: @escaping (Direction) -> Void,
        onUndo: @escaping () -> Void,
        onRestart: @escaping () -> Void,
        onOpenLevelSelect: @escaping () -> Void,
        onOpenSkinSelect: @escaping () -> Void,
        onOpenSettings: @escaping () -> Void,
        @ViewBuilder content: @escaping () -> Content
    ) {
        self.level = level
        self.state = state
        self.canUndo = canUndo
        self.isDpadOnLeft = isDpadOnLeft
        self.onDirection = onDirection
        self.onUndo = onUndo
        self.onRestart = onRestart
        self.onOpenLevelSelect = onOpenLevelSelect
        self.onOpenSkinSelect = onOpenSkinSelect
        self.onOpenSettings = onOpenSettings
        self.content = content
    }

    public var body: some View {
        ZStack {
            // 1. Center Game Board
            content()
                .padding(.leading, isDpadOnLeft ? 140 : 16)
                .padding(.trailing, !isDpadOnLeft ? 140 : 76)
                .padding(.top, 50)
                .padding(.bottom, 10)

            // 2. Top Info Bar
            VStack {
                HStack {
                    // Level info pill
                    HStack(spacing: 6) {
                        Text("LV \(level.id)")
                            .font(.system(size: 14, weight: .black, design: .rounded))
                            .foregroundColor(GameTheme.textPrimary)
                        Text("• \(level.name) (\(level.world))")
                            .font(.system(size: 12, weight: .semibold, design: .rounded))
                            .foregroundColor(GameTheme.textSecondary)
                    }
                    .padding(.horizontal, 12)
                    .padding(.vertical, 6)
                    .background(Color.white.opacity(0.92))
                    .cornerRadius(12)
                    .shadow(color: Color.black.opacity(0.08), radius: 3, x: 0, y: 1)

                    Spacer()

                    // Notice Message Banner
                    if let notice = state.noticeMessage {
                        Text(notice)
                            .font(.system(size: 12, weight: .bold, design: .rounded))
                            .foregroundColor(.white)
                            .padding(.horizontal, 14)
                            .padding(.vertical, 5)
                            .background(state.isPortalUnlocked ? Color(red: 0.18, green: 0.80, blue: 0.44) : Color(red: 0.90, green: 0.49, blue: 0.13))
                            .cornerRadius(12)
                            .shadow(color: Color.black.opacity(0.1), radius: 3, x: 0, y: 1)
                            .transition(.scale.combined(with: .opacity))
                    }

                    Spacer()

                    // Apples remaining & Steps
                    HStack(spacing: 8) {
                        let remaining = state.remainingApples.count
                        let total = level.applePositions.count
                        HStack(spacing: 4) {
                            Text("🍎")
                                .font(.system(size: 13))
                            Text("\(total - remaining)/\(total)")
                                .font(.system(size: 13, weight: .bold, design: .rounded))
                                .foregroundColor(remaining == 0 ? Color(red: 0.15, green: 0.68, blue: 0.38) : GameTheme.textPrimary)
                        }
                        .padding(.horizontal, 10)
                        .padding(.vertical, 6)
                        .background(Color.white.opacity(0.92))
                        .cornerRadius(12)

                        HStack(spacing: 4) {
                            Image(systemName: "figure.walk")
                                .font(.system(size: 12, weight: .bold))
                                .foregroundColor(GameTheme.textPrimary)
                            Text("\(state.movesCount)")
                                .font(.system(size: 13, weight: .black, design: .rounded))
                                .foregroundColor(GameTheme.textPrimary)
                        }
                        .padding(.horizontal, 10)
                        .padding(.vertical, 6)
                        .background(Color.white.opacity(0.92))
                        .cornerRadius(12)
                    }
                }
                .padding(.horizontal, 24)
                .padding(.top, 8)

                Spacer()
            }

            // 3. Thumb D-Pad
            VStack {
                Spacer()
                HStack {
                    if !isDpadOnLeft { Spacer() }
                    VirtualDPadView(onDirection: onDirection)
                        .padding(isDpadOnLeft ? .leading : .trailing, 20)
                        .padding(.bottom, 12)
                    if isDpadOnLeft { Spacer() }
                }
            }

            // 4. Action Buttons Panel (Undo, Reset, Skins, Levels, Config)
            HStack {
                if isDpadOnLeft { Spacer() }
                VStack(spacing: 10) {
                    CircleActionButton(icon: "arrow.uturn.backward", label: "Undo", enabled: canUndo, onClick: onUndo)
                    CircleActionButton(icon: "arrow.clockwise", label: "Reset", enabled: true, onClick: onRestart)
                    CircleActionButton(icon: "paintpalette.fill", label: "Skins", enabled: true, onClick: onOpenSkinSelect)
                    CircleActionButton(icon: "square.grid.2x2.fill", label: "Levels", enabled: true, onClick: onOpenLevelSelect)
                    CircleActionButton(icon: "gearshape.fill", label: "Settings", enabled: true, onClick: onOpenSettings)
                }
                .padding(isDpadOnLeft ? .trailing : .leading, 16)
                if !isDpadOnLeft { Spacer() }
            }
        }
    }
}

private struct CircleActionButton: View {
    let icon: String
    let label: String
    let enabled: Bool
    let onClick: () -> Void

    var body: some View {
        Button(action: onClick) {
            ZStack {
                Circle()
                    .fill(Color.white.opacity(enabled ? 0.95 : 0.45))
                    .shadow(color: Color.black.opacity(enabled ? 0.1 : 0), radius: 3, x: 0, y: 1)

                Image(systemName: icon)
                    .font(.system(size: 16, weight: .bold))
                    .foregroundColor(enabled ? GameTheme.textPrimary : GameTheme.textSecondary.opacity(0.4))
            }
            .frame(width: 44, height: 44)
        }
        .disabled(!enabled)
        .buttonStyle(PlainButtonStyle())
    }
}

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
                .padding(.leading, isDpadOnLeft ? 130 : 20)
                .padding(.trailing, !isDpadOnLeft ? 130 : 20)
                .padding(.top, 56)
                .padding(.bottom, 12)

            // 2. Top-Left: Glossy Blue Restart Button & Level Indicator (Exact to screenshot)
            VStack {
                HStack(spacing: 10) {
                    GlossyBlueButton(icon: "arrow.clockwise", label: "Restart", enabled: true, onClick: onRestart)

                    Text("LV \(level.id)")
                        .font(.system(size: 15, weight: .black, design: .rounded))
                        .foregroundColor(Color(red: 0.11, green: 0.31, blue: 0.61))
                        .padding(.horizontal, 12)
                        .padding(.vertical, 6)
                        .background(Color.white.opacity(0.92))
                        .cornerRadius(14)
                        .overlay(
                            RoundedRectangle(cornerRadius: 14)
                                .stroke(Color(red: 0.13, green: 0.45, blue: 0.84), lineWidth: 2)
                        )
                        .shadow(color: Color.black.opacity(0.08), radius: 3, x: 0, y: 1)

                    Spacer()
                }
                .padding(.leading, 16)
                .padding(.top, 12)

                Spacer()
            }

            // 3. Top-Center: Score, Best Score & Apples Pill
            VStack {
                HStack(spacing: 12) {
                    HStack(spacing: 12) {
                        // Current Score
                        HStack(spacing: 4) {
                            Text("⭐")
                                .font(.system(size: 13))
                            Text("SCORE: \(state.score)")
                                .font(.system(size: 13, weight: .black, design: .rounded))
                                .foregroundColor(Color(red: 0.10, green: 0.21, blue: 0.36))
                        }

                        Rectangle()
                            .fill(Color.gray.opacity(0.3))
                            .frame(width: 1.5, height: 16)

                        // Best Score
                        HStack(spacing: 4) {
                            Text("👑")
                                .font(.system(size: 13))
                            Text("BEST: \(state.highScore)")
                                .font(.system(size: 13, weight: .bold, design: .rounded))
                                .foregroundColor(Color(red: 0.71, green: 0.33, blue: 0.04))
                        }

                        Rectangle()
                            .fill(Color.gray.opacity(0.3))
                            .frame(width: 1.5, height: 16)

                        // Apples remaining
                        let remaining = state.remainingApples.count
                        let total = level.applePositions.count
                        HStack(spacing: 4) {
                            Text("🍎")
                                .font(.system(size: 13))
                            Text("\(total - remaining)/\(total)")
                                .font(.system(size: 13, weight: .black, design: .rounded))
                                .foregroundColor(remaining == 0 ? Color(red: 0.09, green: 0.64, blue: 0.29) : Color(red: 0.86, green: 0.15, blue: 0.15))
                        }
                    }
                    .padding(.horizontal, 14)
                    .padding(.vertical, 6)
                    .background(Color.white.opacity(0.95))
                    .cornerRadius(18)
                    .overlay(
                        RoundedRectangle(cornerRadius: 18)
                            .stroke(Color(red: 0.13, green: 0.45, blue: 0.84), lineWidth: 2)
                    )
                    .shadow(color: Color.black.opacity(0.1), radius: 4, x: 0, y: 2)

                    // Notice Message Banner
                    if let notice = state.noticeMessage {
                        Text(notice)
                            .font(.system(size: 12, weight: .bold, design: .rounded))
                            .foregroundColor(.white)
                            .padding(.horizontal, 12)
                            .padding(.vertical, 6)
                            .background(state.isPortalUnlocked ? Color(red: 0.09, green: 0.64, blue: 0.29) : Color(red: 0.92, green: 0.35, blue: 0.05))
                            .cornerRadius(14)
                            .shadow(color: Color.black.opacity(0.12), radius: 3, x: 0, y: 1)
                            .transition(.scale.combined(with: .opacity))
                    }
                }
                .padding(.top, 12)

                Spacer()
            }

            // 4. Top-Right: Glossy Blue Action Buttons (Undo, Skins, Levels, Settings)
            VStack {
                HStack(spacing: 10) {
                    Spacer()
                    GlossyBlueButton(icon: "arrow.uturn.backward", label: "Undo", enabled: canUndo, onClick: onUndo)
                    GlossyBlueButton(icon: "paintpalette.fill", label: "Skins", enabled: true, onClick: onOpenSkinSelect)
                    GlossyBlueButton(icon: "square.grid.2x2.fill", label: "Levels", enabled: true, onClick: onOpenLevelSelect)
                    GlossyBlueButton(icon: "gearshape.fill", label: "Settings", enabled: true, onClick: onOpenSettings)
                }
                .padding(.trailing, 16)
                .padding(.top, 12)

                Spacer()
            }

            // 5. Thumb D-Pad
            VStack {
                Spacer()
                HStack {
                    if !isDpadOnLeft { Spacer() }
                    VirtualDPadView(onDirection: onDirection)
                        .padding(isDpadOnLeft ? .leading : .trailing, 18)
                        .padding(.bottom, 14)
                    if isDpadOnLeft { Spacer() }
                }
            }
        }
    }
}

/**
 * Glossy circular blue button matching the reference screenshot.
 */
private struct GlossyBlueButton: View {
    let icon: String
    let label: String
    let enabled: Bool
    let onClick: () -> Void

    var body: some View {
        Button(action: onClick) {
            ZStack {
                Circle()
                    .fill(
                        LinearGradient(
                            colors: enabled ? [Color(red: 0.34, green: 0.71, blue: 0.97), Color(red: 0.12, green: 0.44, blue: 0.84)] : [Color.gray, Color.gray.opacity(0.7)],
                            startPoint: .top,
                            endPoint: .bottom
                        )
                    )
                    .overlay(
                        Circle().stroke(Color.white, lineWidth: 2.8)
                    )
                    .shadow(color: Color.black.opacity(enabled ? 0.25 : 0.05), radius: 3, x: 0, y: 2)

                // Top highlight curve
                VStack {
                    Capsule()
                        .fill(Color.white.opacity(0.35))
                        .frame(width: 24, height: 10)
                        .padding(.top, 3)
                    Spacer()
                }

                Image(systemName: icon)
                    .font(.system(size: 18, weight: .black))
                    .foregroundColor(.white)
            }
            .frame(width: 44, height: 44)
        }
        .disabled(!enabled)
        .buttonStyle(PlainButtonStyle())
    }
}

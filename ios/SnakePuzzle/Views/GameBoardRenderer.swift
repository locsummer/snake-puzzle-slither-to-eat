import SwiftUI

public struct GameBoardRenderer: View {
    public let level: Level
    public let state: GameState
    public let skin: SnakeSkin

    @State private var portalRotation: Double = 0
    @State private var applePulse: CGFloat = 1.0
    @State private var tongueFlick: CGFloat = 0.0

    public init(level: Level, state: GameState, skin: SnakeSkin) {
        self.level = level
        self.state = state
        self.skin = skin
    }

    public var body: some View {
        GeometryReader { geometry in
            let availableWidth = geometry.size.width
            let availableHeight = geometry.size.height

            let cellWidth = availableWidth / CGFloat(level.width)
            let cellHeight = availableHeight / CGFloat(level.height)
            let cellSize = min(cellWidth, cellHeight)

            let boardWidth = cellSize * CGFloat(level.width)
            let boardHeight = cellSize * CGFloat(level.height)
            let offsetX = (availableWidth - boardWidth) / 2
            let offsetY = (availableHeight - boardHeight) / 2

            let baseDirt = GameTheme.blockBase(for: level.world)
            let topTrim = GameTheme.blockTrim(for: level.world)

            ZStack(alignment: .topLeading) {
                // Background grid card
                RoundedRectangle(cornerRadius: 16)
                    .fill(Color.black.opacity(0.18))
                    .frame(width: boardWidth + 8, height: boardHeight + 8)
                    .offset(x: -4, y: -4)

                RoundedRectangle(cornerRadius: 14)
                    .fill(Color.black.opacity(0.15))
                    .frame(width: boardWidth, height: boardHeight)
                    .overlay(
                        RoundedRectangle(cornerRadius: 14)
                            .stroke(Color.white.opacity(0.2), lineWidth: 1.5)
                    )

                // Grid lines
                Canvas { context, size in
                    for x in 0...level.width {
                        let px = CGFloat(x) * cellSize
                        var path = Path()
                        path.move(to: CGPoint(x: px, y: 0))
                        path.addLine(to: CGPoint(x: px, y: size.height))
                        context.stroke(path, with: .color(Color.white.opacity(0.1)), lineWidth: 0.5)
                    }
                    for y in 0...level.height {
                        let py = CGFloat(y) * cellSize
                        var path = Path()
                        path.move(to: CGPoint(x: 0, y: py))
                        path.addLine(to: CGPoint(x: size.width, y: py))
                        context.stroke(path, with: .color(Color.white.opacity(0.1)), lineWidth: 0.5)
                    }
                }
                .frame(width: boardWidth, height: boardHeight)

                // 1. Solid blocks (Ground & Walls)
                ForEach(Array(level.solidPositions), id: \.self) { pos in
                    let isTopSurface = !level.solidPositions.contains(Position(x: pos.x, y: pos.y - 1))
                    ZStack {
                        RoundedRectangle(cornerRadius: 5)
                            .fill(baseDirt)
                        if isTopSurface {
                            VStack(spacing: 0) {
                                RoundedRectangle(cornerRadius: 4)
                                    .fill(topTrim)
                                    .frame(height: cellSize * 0.28)
                                Spacer()
                            }
                        }
                    }
                    .frame(width: cellSize - 1, height: cellSize - 1)
                    .position(
                        x: CGFloat(pos.x) * cellSize + cellSize / 2,
                        y: CGFloat(pos.y) * cellSize + cellSize / 2
                    )
                }

                // 2. Spikes
                ForEach(Array(level.spikePositions), id: \.self) { pos in
                    SpikeView(cellSize: cellSize)
                        .position(
                            x: CGFloat(pos.x) * cellSize + cellSize / 2,
                            y: CGFloat(pos.y) * cellSize + cellSize / 2
                        )
                }

                // 3. Portal
                if let portal = level.portalPosition {
                    PortalView(cellSize: cellSize, isUnlocked: state.isPortalUnlocked, rotation: portalRotation)
                        .position(
                            x: CGFloat(portal.x) * cellSize + cellSize / 2,
                            y: CGFloat(portal.y) * cellSize + cellSize / 2
                        )
                }

                // 4. Apples
                ForEach(Array(state.remainingApples), id: \.self) { pos in
                    AppleView(cellSize: cellSize, scale: applePulse)
                        .position(
                            x: CGFloat(pos.x) * cellSize + cellSize / 2,
                            y: CGFloat(pos.y) * cellSize + cellSize / 2
                        )
                }

                // 5. 2.5D Animated Cute Snake
                SnakeView(
                    snake: state.snake,
                    facing: state.facingDirection,
                    skin: skin,
                    cellSize: cellSize,
                    tongueFlick: tongueFlick
                )
            }
            .frame(width: boardWidth, height: boardHeight)
            .offset(x: offsetX, y: offsetY)
        }
        .onAppear {
            withAnimation(.linear(duration: 3.0).repeatForever(autoreverses: false)) {
                portalRotation = 360
            }
            withAnimation(.easeInOut(duration: 0.65).repeatForever(autoreverses: true)) {
                applePulse = 1.12
            }
            withAnimation(.easeInOut(duration: 0.85).repeatForever(autoreverses: true)) {
                tongueFlick = 1.0
            }
        }
    }
}

private struct SpikeView: View {
    let cellSize: CGFloat

    var body: some View {
        ZStack {
            Path { path in
                let w = cellSize * 0.82
                let h = cellSize * 0.75
                path.move(to: CGPoint(x: -w/2, y: h/2))
                path.addLine(to: CGPoint(x: 0, y: -h/2))
                path.addLine(to: CGPoint(x: w/2, y: h/2))
                path.closeSubpath()
            }
            .fill(
                LinearGradient(
                    colors: [GameTheme.spikePoint, GameTheme.spikeBase],
                    startPoint: .top,
                    endPoint: .bottom
                )
            )
        }
    }
}

private struct PortalView: View {
    let cellSize: CGFloat
    let isUnlocked: Bool
    let rotation: Double

    var body: some View {
        ZStack {
            // Glow
            Circle()
                .fill(
                    RadialGradient(
                        colors: [isUnlocked ? GameTheme.portalGlow.opacity(0.85) : Color.gray.opacity(0.3), Color.clear],
                        center: .center,
                        startRadius: cellSize * 0.1,
                        endRadius: cellSize * 0.65
                    )
                )
                .frame(width: cellSize * 1.3, height: cellSize * 1.3)

            // Swirl Ring
            Circle()
                .strokeBorder(
                    AngularGradient(
                        gradient: Gradient(colors: isUnlocked ? [GameTheme.portalOuter, GameTheme.portalGlow, GameTheme.portalInner, GameTheme.portalOuter] : [Color.gray, Color.black, Color.gray]),
                        center: .center
                    ),
                    lineWidth: cellSize * 0.16
                )
                .frame(width: cellSize * 0.85, height: cellSize * 0.85)
                .rotationEffect(.degrees(isUnlocked ? rotation : 0))

            // Center Abyss
            Circle()
                .fill(GameTheme.portalInner)
                .frame(width: cellSize * 0.55, height: cellSize * 0.55)

            if !isUnlocked {
                Image(systemName: "lock.fill")
                    .font(.system(size: cellSize * 0.32, weight: .bold))
                    .foregroundColor(.white.opacity(0.9))
            }
        }
    }
}

private struct AppleView: View {
    let cellSize: CGFloat
    let scale: CGFloat

    var body: some View {
        ZStack {
            // Wooden Stem
            RoundedRectangle(cornerRadius: 1)
                .fill(GameTheme.appleStem)
                .frame(width: cellSize * 0.08, height: cellSize * 0.20)
                .offset(x: 0, y: -cellSize * 0.30)

            // Green Leaf
            Capsule()
                .fill(GameTheme.appleLeaf)
                .frame(width: cellSize * 0.18, height: cellSize * 0.1)
                .rotationEffect(.degrees(-35))
                .offset(x: cellSize * 0.12, y: -cellSize * 0.30)

            // Fruit Body
            Circle()
                .fill(
                    RadialGradient(
                        colors: [GameTheme.appleGleam, GameTheme.appleRed, Color(red: 0.55, green: 0.05, blue: 0.05)],
                        center: .topLeading,
                        startRadius: 2,
                        endRadius: cellSize * 0.42
                    )
                )
                .frame(width: cellSize * 0.68, height: cellSize * 0.68)
                .scaleEffect(scale)
                .shadow(color: GameTheme.appleRed.opacity(0.4), radius: 4, x: 0, y: 2)
        }
    }
}

private struct SnakeView: View {
    let snake: [Position]
    let facing: Direction
    let skin: SnakeSkin
    let cellSize: CGFloat
    let tongueFlick: CGFloat

    var body: some View {
        ZStack {
            ForEach(0..<snake.count, id: \.self) { index in
                let pos = snake[index]
                let isHead = index == 0
                let isTail = index == snake.count - 1
                let segSize = cellSize * (isHead ? 0.90 : (isTail ? 0.70 : 0.82))

                let segColor = isHead ? skin.headColor : (isTail ? skin.tailColor : skin.bodyColor)

                ZStack {
                    Circle()
                        .fill(
                            RadialGradient(
                                colors: [skin.accentColor, segColor, segColor.opacity(0.85)],
                                center: .topLeading,
                                startRadius: 2,
                                endRadius: segSize * 0.6
                            )
                        )
                        .frame(width: segSize, height: segSize)
                        .shadow(color: Color.black.opacity(0.2), radius: 2, x: 0, y: 1)

                    if isHead {
                        SnakeFaceView(facing: facing, skin: skin, size: segSize, tongueFlick: tongueFlick)
                    } else {
                        Circle()
                            .fill(skin.accentColor.opacity(0.55))
                            .frame(width: segSize * 0.35, height: segSize * 0.35)
                    }
                }
                .position(
                    x: CGFloat(pos.x) * cellSize + cellSize / 2,
                    y: CGFloat(pos.y) * cellSize + cellSize / 2
                )
                .animation(.spring(response: 0.2, dampingFraction: 0.75), value: pos)
            }
        }
    }
}

private struct SnakeFaceView: View {
    let facing: Direction
    let skin: SnakeSkin
    let size: CGFloat
    let tongueFlick: CGFloat

    var body: some View {
        let eyeSize = size * 0.32
        let pupilSize = eyeSize * 0.52

        let pupilOffset: (x: CGFloat, y: CGFloat) = {
            switch facing {
            case .up: return (0, -eyeSize * 0.22)
            case .down: return (0, eyeSize * 0.22)
            case .left: return (-eyeSize * 0.22, 0)
            case .right: return (eyeSize * 0.22, 0)
            }
        }()

        let eyeSpacing = size * 0.24
        let eyeVertical = -size * 0.08

        ZStack {
            // Little forked tongue
            if tongueFlick > 0.4 {
                let tongueLength = size * 0.28 * tongueFlick
                Capsule()
                    .fill(Color(red: 1.0, green: 0.16, blue: 0.30))
                    .frame(width: 3, height: tongueLength)
                    .offset(x: pupilOffset.x * 2.5, y: pupilOffset.y * 2.5)
            }

            // Left Eye
            ZStack {
                Circle().fill(skin.eyeColor)
                Circle().fill(skin.pupilColor)
                    .frame(width: pupilSize, height: pupilSize)
                    .offset(x: pupilOffset.x, y: pupilOffset.y)
                Circle().fill(Color.white)
                    .frame(width: pupilSize * 0.35, height: pupilSize * 0.35)
                    .offset(x: pupilOffset.x - 1, y: pupilOffset.y - 1)
            }
            .frame(width: eyeSize, height: eyeSize)
            .offset(x: -eyeSpacing, y: eyeVertical)

            // Right Eye
            ZStack {
                Circle().fill(skin.eyeColor)
                Circle().fill(skin.pupilColor)
                    .frame(width: pupilSize, height: pupilSize)
                    .offset(x: pupilOffset.x, y: pupilOffset.y)
                Circle().fill(Color.white)
                    .frame(width: pupilSize * 0.35, height: pupilSize * 0.35)
                    .offset(x: pupilOffset.x - 1, y: pupilOffset.y - 1)
            }
            .frame(width: eyeSize, height: eyeSize)
            .offset(x: eyeSpacing, y: eyeVertical)

            // Rosy cheeks
            Circle()
                .fill(skin.cheekColor)
                .frame(width: eyeSize * 0.65, height: eyeSize * 0.65)
                .offset(x: -eyeSpacing * 1.35, y: eyeVertical + eyeSize * 0.72)
            Circle()
                .fill(skin.cheekColor)
                .frame(width: eyeSize * 0.65, height: eyeSize * 0.65)
                .offset(x: eyeSpacing * 1.35, y: eyeVertical + eyeSize * 0.72)
        }
    }
}

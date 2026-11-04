import SwiftUI

public struct GameBoardRenderer: View {
    public let level: Level
    public let state: GameState
    public let portalOpen: Bool

    @State private var portalRotation: Double = 0
    @State private var applePulse: CGFloat = 1.0

    public init(level: Level, state: GameState, portalOpen: Bool) {
        self.level = level
        self.state = state
        self.portalOpen = portalOpen
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

            ZStack(alignment: .topLeading) {
                // Background grid card
                RoundedRectangle(cornerRadius: 16)
                    .fill(GameTheme.gridBackground)
                    .frame(width: boardWidth, height: boardHeight)
                    .overlay(
                        RoundedRectangle(cornerRadius: 16)
                            .stroke(Color.white.opacity(0.4), lineWidth: 2)
                    )

                // Grid lines
                Canvas { context, size in
                    for x in 0...level.width {
                        let px = CGFloat(x) * cellSize
                        var path = Path()
                        path.move(to: CGPoint(x: px, y: 0))
                        path.addLine(to: CGPoint(x: px, y: size.height))
                        context.stroke(path, with: .color(GameTheme.gridLine), lineWidth: 0.5)
                    }
                    for y in 0...level.height {
                        let py = CGFloat(y) * cellSize
                        var path = Path()
                        path.move(to: CGPoint(x: 0, y: py))
                        path.addLine(to: CGPoint(x: size.width, y: py))
                        context.stroke(path, with: .color(GameTheme.gridLine), lineWidth: 0.5)
                    }
                }
                .frame(width: boardWidth, height: boardHeight)

                // 1. Solid blocks (Ground & Walls)
                ForEach(Array(level.solidPositions), id: \.self) { pos in
                    let isTopSurface = !level.solidPositions.contains(Position(x: pos.x, y: pos.y - 1))
                    ZStack {
                        RoundedRectangle(cornerRadius: 4)
                            .fill(GameTheme.solidDirt)
                        if isTopSurface {
                            VStack(spacing: 0) {
                                RoundedRectangle(cornerRadius: 3)
                                    .fill(GameTheme.solidGrassTop)
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

                // 3. Portal / Exit Hole
                if let portal = level.portalPosition {
                    PortalView(cellSize: cellSize, isOpen: portalOpen, rotation: portalRotation)
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

                // 5. Snake (Body and Head)
                SnakeView(snake: state.snake, facing: state.facingDirection, cellSize: cellSize)
            }
            .frame(width: boardWidth, height: boardHeight)
            .offset(x: offsetX, y: offsetY)
        }
        .onAppear {
            withAnimation(.linear(duration: 4.0).repeatForever(autoreverses: false)) {
                portalRotation = 360
            }
            withAnimation(.easeInOut(duration: 0.8).repeatForever(autoreverses: true)) {
                applePulse = 1.08
            }
        }
    }
}

// Subview: Spike
private struct SpikeView: View {
    let cellSize: CGFloat

    var body: some View {
        ZStack {
            Path { path in
                let w = cellSize * 0.85
                let h = cellSize * 0.75
                path.move(to: CGPoint(x: -w/2, y: h/2))
                path.addLine(to: CGPoint(x: 0, y: -h/2))
                path.addLine(to: CGPoint(x: w/2, y: h/2))
                path.closeSubpath()
            }
            .fill(
                LinearGradient(
                    colors: [GameTheme.spikeTip, GameTheme.spikeBase],
                    startPoint: .top,
                    endPoint: .bottom
                )
            )
        }
    }
}

// Subview: Portal
private struct PortalView: View {
    let cellSize: CGFloat
    let isOpen: Bool
    let rotation: Double

    var body: some View {
        ZStack {
            // Glow
            Circle()
                .fill(
                    RadialGradient(
                        colors: [isOpen ? GameTheme.portalGlow.opacity(0.8) : Color.gray.opacity(0.3), Color.clear],
                        center: .center,
                        startRadius: cellSize * 0.1,
                        endRadius: cellSize * 0.6
                    )
                )
                .frame(width: cellSize * 1.2, height: cellSize * 1.2)

            // Swirl Ring
            Circle()
                .strokeBorder(
                    AngularGradient(
                        gradient: Gradient(colors: [GameTheme.portalOuter, GameTheme.portalGlow, GameTheme.portalInner, GameTheme.portalOuter]),
                        center: .center
                    ),
                    lineWidth: cellSize * 0.16
                )
                .frame(width: cellSize * 0.8, height: cellSize * 0.8)
                .rotationEffect(.degrees(rotation))

            // Center Abyss
            Circle()
                .fill(GameTheme.portalInner)
                .frame(width: cellSize * 0.5, height: cellSize * 0.5)

            if !isOpen {
                Image(systemName: "lock.fill")
                    .font(.system(size: cellSize * 0.3, weight: .bold))
                    .foregroundColor(.white.opacity(0.8))
            }
        }
    }
}

// Subview: Apple
private struct AppleView: View {
    let cellSize: CGFloat
    let scale: CGFloat

    var body: some View {
        ZStack {
            // Stem
            RoundedRectangle(cornerRadius: 1)
                .fill(GameTheme.appleStem)
                .frame(width: cellSize * 0.08, height: cellSize * 0.22)
                .offset(x: 0, y: -cellSize * 0.32)

            // Leaf
            Capsule()
                .fill(GameTheme.appleLeaf)
                .frame(width: cellSize * 0.18, height: cellSize * 0.1)
                .rotationEffect(.degrees(-35))
                .offset(x: cellSize * 0.12, y: -cellSize * 0.32)

            // Apple fruit body
            Circle()
                .fill(
                    RadialGradient(
                        colors: [Color(red: 1.0, green: 0.45, blue: 0.45), GameTheme.appleRed],
                        center: .topLeading,
                        startRadius: 2,
                        endRadius: cellSize * 0.4
                    )
                )
                .frame(width: cellSize * 0.68, height: cellSize * 0.68)
                .shadow(color: GameTheme.appleRed.opacity(0.4), radius: 4, x: 0, y: 2)
                .scaleEffect(scale)
        }
    }
}

// Subview: Snake
private struct SnakeView: View {
    let snake: [Position]
    let facing: Direction
    let cellSize: CGFloat

    var body: some View {
        ZStack {
            // Body segments
            ForEach(0..<snake.count, id: \.self) { index in
                let pos = snake[index]
                let isHead = index == 0
                let isTail = index == snake.count - 1
                let segSize = cellSize * (isHead ? 0.88 : (isTail ? 0.72 : 0.82))

                let fraction = CGFloat(index) / CGFloat(max(1, snake.count - 1))
                let segColor = isHead ? GameTheme.snakeHead : (isTail ? GameTheme.snakeTail : GameTheme.snakeBody)

                ZStack {
                    RoundedRectangle(cornerRadius: segSize * 0.42)
                        .fill(segColor)
                        .frame(width: segSize, height: segSize)
                        .shadow(color: Color.black.opacity(0.18), radius: 2, x: 0, y: 1)

                    if isHead {
                        SnakeFaceView(facing: facing, size: segSize)
                    } else {
                        // Body stripe detail
                        Circle()
                            .fill(GameTheme.snakeStripe.opacity(0.4))
                            .frame(width: segSize * 0.35, height: segSize * 0.35)
                    }
                }
                .position(
                    x: CGFloat(pos.x) * cellSize + cellSize / 2,
                    y: CGFloat(pos.y) * cellSize + cellSize / 2
                )
                .animation(.spring(response: 0.22, dampingFraction: 0.75), value: pos)
            }
        }
    }
}

// Subview: Snake Eyes & Face
private struct SnakeFaceView: View {
    let facing: Direction
    let size: CGFloat

    var body: some View {
        let eyeSize = size * 0.28
        let pupilSize = eyeSize * 0.52

        // Pupil offset depends on facing direction
        let pupilOffset: (x: CGFloat, y: CGFloat) = {
            switch facing {
            case .up: return (0, -eyeSize * 0.22)
            case .down: return (0, eyeSize * 0.22)
            case .left: return (-eyeSize * 0.22, 0)
            case .right: return (eyeSize * 0.22, 0)
            }
        }()

        let eyeSpacing = size * 0.22
        let eyeVertical = -size * 0.08

        ZStack {
            // Left Eye
            ZStack {
                Circle().fill(GameTheme.snakeEye)
                Circle().fill(GameTheme.snakePupil)
                    .frame(width: pupilSize, height: pupilSize)
                    .offset(x: pupilOffset.x, y: pupilOffset.y)
            }
            .frame(width: eyeSize, height: eyeSize)
            .offset(x: -eyeSpacing, y: eyeVertical)

            // Right Eye
            ZStack {
                Circle().fill(GameTheme.snakeEye)
                Circle().fill(GameTheme.snakePupil)
                    .frame(width: pupilSize, height: pupilSize)
                    .offset(x: pupilOffset.x, y: pupilOffset.y)
            }
            .frame(width: eyeSize, height: eyeSize)
            .offset(x: eyeSpacing, y: eyeVertical)

            // Cute rosy cheeks
            Circle()
                .fill(Color.pink.opacity(0.45))
                .frame(width: eyeSize * 0.6, height: eyeSize * 0.6)
                .offset(x: -eyeSpacing * 1.35, y: eyeVertical + eyeSize * 0.8)
            Circle()
                .fill(Color.pink.opacity(0.45))
                .frame(width: eyeSize * 0.6, height: eyeSize * 0.6)
                .offset(x: eyeSpacing * 1.35, y: eyeVertical + eyeSize * 0.8)
        }
    }
}

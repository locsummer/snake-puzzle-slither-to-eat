import SwiftUI

public struct GameBoardRenderer: View {
    public let level: Level
    public let state: GameState
    public let skin: SnakeSkin

    @State private var portalRotation: Double = 0
    @State private var applePulse: CGFloat = 1.0

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

            ZStack {
                // 1. Sunburst Comic Sky Background
                SunburstSkyView()

                // 2. Game Board Elements
                ZStack(alignment: .topLeading) {
                    // Connected Brown Dirt Platforms with outer black outline & wavy top edges
                    Canvas { context, size in
                        let outlineWidth: CGFloat = 3.5

                        // Pass 1: Fill all solid cells with warm earth brown
                        for pos in level.solidPositions {
                            let cellX = CGFloat(pos.x) * cellSize
                            let cellY = CGFloat(pos.y) * cellSize

                            let rect = CGRect(x: cellX, y: cellY, width: cellSize, height: cellSize)
                            context.fill(Path(rect), with: .color(Color(red: 0.42, green: 0.23, blue: 0.10))) // #6B3A19

                            // Top surface organic highlight if exposed
                            if !level.solidPositions.contains(Position(x: pos.x, y: pos.y - 1)) {
                                let trimH = cellSize * 0.16
                                let trimRect = CGRect(x: cellX, y: cellY, width: cellSize, height: trimH)
                                context.fill(Path(trimRect), with: .color(Color(red: 0.55, green: 0.30, blue: 0.13))) // #8B4D22

                                // Wavy line
                                var wavy = Path()
                                wavy.move(to: CGPoint(x: cellX, y: cellY + trimH))
                                wavy.addQuadCurve(to: CGPoint(x: cellX + cellSize * 0.5, y: cellY + trimH), control: CGPoint(x: cellX + cellSize * 0.25, y: cellY + trimH - 2))
                                wavy.addQuadCurve(to: CGPoint(x: cellX + cellSize, y: cellY + trimH), control: CGPoint(x: cellX + cellSize * 0.75, y: cellY + trimH + 2))
                                context.stroke(wavy, with: .color(Color(red: 0.23, green: 0.11, blue: 0.04)), lineWidth: 2.2)
                            }
                        }

                        // Pass 2: Draw bold cartoon outlines along exposed edges
                        for pos in level.solidPositions {
                            let cellX = CGFloat(pos.x) * cellSize
                            let cellY = CGFloat(pos.y) * cellSize

                            let hasTop = level.solidPositions.contains(Position(x: pos.x, y: pos.y - 1))
                            let hasBottom = level.solidPositions.contains(Position(x: pos.x, y: pos.y + 1))
                            let hasLeft = level.solidPositions.contains(Position(x: pos.x - 1, y: pos.y))
                            let hasRight = level.solidPositions.contains(Position(x: pos.x + 1, y: pos.y))

                            let darkBorder = Color(red: 0.13, green: 0.06, blue: 0.02)

                            if !hasTop {
                                var p = Path()
                                p.move(to: CGPoint(x: cellX - outlineWidth/2, y: cellY))
                                p.addLine(to: CGPoint(x: cellX + cellSize + outlineWidth/2, y: cellY))
                                context.stroke(p, with: .color(darkBorder), lineWidth: outlineWidth)
                            }
                            if !hasBottom {
                                var p = Path()
                                p.move(to: CGPoint(x: cellX - outlineWidth/2, y: cellY + cellSize))
                                p.addLine(to: CGPoint(x: cellX + cellSize + outlineWidth/2, y: cellY + cellSize))
                                context.stroke(p, with: .color(darkBorder), lineWidth: outlineWidth)
                            }
                            if !hasLeft {
                                var p = Path()
                                p.move(to: CGPoint(x: cellX, y: cellY - outlineWidth/2))
                                p.addLine(to: CGPoint(x: cellX, y: cellY + cellSize + outlineWidth/2))
                                context.stroke(p, with: .color(darkBorder), lineWidth: outlineWidth)
                            }
                            if !hasRight {
                                var p = Path()
                                p.move(to: CGPoint(x: cellX + cellSize, y: cellY - outlineWidth/2))
                                p.addLine(to: CGPoint(x: cellX + cellSize, y: cellY + cellSize + outlineWidth/2))
                                context.stroke(p, with: .color(darkBorder), lineWidth: outlineWidth)
                            }
                        }
                    }
                    .frame(width: boardWidth, height: boardHeight)

                    // 3. Spikes
                    ForEach(Array(level.spikePositions), id: \.self) { pos in
                        SpikeView(cellSize: cellSize)
                            .position(
                                x: CGFloat(pos.x) * cellSize + cellSize / 2,
                                y: CGFloat(pos.y) * cellSize + cellSize / 2
                            )
                    }

                    // 4. Black Spiral Vortex Exit Portal
                    if let portal = level.portalPosition {
                        BlackSpiralPortalView(cellSize: cellSize, isUnlocked: state.isPortalUnlocked, rotation: portalRotation)
                            .position(
                                x: CGFloat(portal.x) * cellSize + cellSize / 2,
                                y: CGFloat(portal.y) * cellSize + cellSize / 2
                            )
                    }

                    // 5. Glossy Red Apples
                    ForEach(Array(state.remainingApples), id: \.self) { pos in
                        CartoonAppleView(cellSize: cellSize, scale: applePulse)
                            .position(
                                x: CGFloat(pos.x) * cellSize + cellSize / 2,
                                y: CGFloat(pos.y) * cellSize + cellSize / 2
                            )
                    }

                    // 6. The Apple Worm with Purple Lips & Googly Eyes
                    AppleWormSnakeView(
                        snake: state.snake,
                        facing: state.facingDirection,
                        skin: skin,
                        isDead: state.isDead,
                        cellSize: cellSize
                    )
                }
                .frame(width: boardWidth, height: boardHeight)
                .offset(x: offsetX, y: offsetY)
            }
        }
        .onAppear {
            withAnimation(.linear(duration: 3.5).repeatForever(autoreverses: false)) {
                portalRotation = 360
            }
            withAnimation(.easeInOut(duration: 0.70).repeatForever(autoreverses: true)) {
                applePulse = 1.08
            }
        }
    }
}

/**
 * Sunburst Sky Background with comic radial rays and rolling hills.
 */
private struct SunburstSkyView: View {
    var body: some View {
        GeometryReader { geo in
            let w = geo.size.width
            let h = geo.size.height

            ZStack {
                // Base Sky Gradient
                LinearGradient(
                    colors: [Color(red: 0.42, green: 0.76, blue: 0.98), Color(red: 0.22, green: 0.59, blue: 0.93)],
                    startPoint: .top,
                    endPoint: .bottom
                )

                // Sunburst rays
                Canvas { context, size in
                    let origin = CGPoint(x: size.width / 2, y: -size.height * 0.1)
                    let maxDist = hypot(size.width, size.height) * 1.5
                    let rayCount = 20

                    for i in stride(from: 0, to: rayCount, by: 2) {
                        let a1 = CGFloat(i) * 2 * .pi / CGFloat(rayCount)
                        let a2 = CGFloat(i + 1) * 2 * .pi / CGFloat(rayCount)

                        var ray = Path()
                        ray.move(to: origin)
                        ray.addLine(to: CGPoint(x: origin.x + cos(a1) * maxDist, y: origin.y + sin(a1) * maxDist))
                        ray.addLine(to: CGPoint(x: origin.x + cos(a2) * maxDist, y: origin.y + sin(a2) * maxDist))
                        ray.closeSubpath()

                        context.fill(ray, with: .color(Color.white.opacity(0.13)))
                    }
                }

                // Rolling Green Hills at bottom
                VStack {
                    Spacer()
                    Canvas { context, size in
                        let h = size.height
                        let w = size.width

                        var hillBack = Path()
                        hillBack.move(to: CGPoint(x: 0, y: h))
                        hillBack.addLine(to: CGPoint(x: 0, y: h * 0.4))
                        hillBack.addCurve(to: CGPoint(x: w * 0.7, y: h * 0.35), control1: CGPoint(x: w * 0.25, y: h * 0.2), control2: CGPoint(x: w * 0.45, y: h * 0.45))
                        hillBack.addCurve(to: CGPoint(x: w, y: h * 0.38), control1: CGPoint(x: w * 0.85, y: h * 0.25), control2: CGPoint(x: w * 0.95, y: h * 0.4))
                        hillBack.addLine(to: CGPoint(x: w, y: h))
                        hillBack.closeSubpath()
                        context.fill(hillBack, with: .color(Color(red: 0.33, green: 0.60, blue: 0.10)))

                        var hillFront = Path()
                        hillFront.move(to: CGPoint(x: 0, y: h))
                        hillFront.addLine(to: CGPoint(x: 0, y: h * 0.55))
                        hillFront.addCurve(to: CGPoint(x: w * 0.65, y: h * 0.50), control1: CGPoint(x: w * 0.2, y: h * 0.35), control2: CGPoint(x: w * 0.4, y: h * 0.6))
                        hillFront.addCurve(to: CGPoint(x: w, y: h * 0.48), control1: CGPoint(x: w * 0.8, y: h * 0.38), control2: CGPoint(x: w * 0.92, y: h * 0.55))
                        hillFront.addLine(to: CGPoint(x: w, y: h))
                        hillFront.closeSubpath()
                        context.fill(hillFront, with: .color(Color(red: 0.44, green: 0.71, blue: 0.15)))
                    }
                    .frame(height: h * 0.22)
                }
            }
        }
        .ignoresSafeArea()
    }
}

/**
 * Black Spiral Vortex Portal matching reference screenshot.
 */
private struct BlackSpiralPortalView: View {
    let cellSize: CGFloat
    let isUnlocked: Bool
    let rotation: Double

    var body: some View {
        let r = cellSize * 0.44

        ZStack {
            // Dark base background with outer border
            Circle()
                .fill(Color(red: 0.09, green: 0.09, blue: 0.09))
                .frame(width: r * 2, height: r * 2)
                .overlay(
                    Circle().stroke(Color(red: 0.13, green: 0.06, blue: 0.02), lineWidth: 3.5)
                )

            // Swirl Archimedean Spiral Lines
            Canvas { context, size in
                let cx = size.width / 2
                let cy = size.height / 2
                let spiralRadius = r * 0.95

                var path = Path()
                let points = 70
                let turns: CGFloat = 3.2

                for i in 0...points {
                    let t = CGFloat(i) / CGFloat(points)
                    let angle = t * 2 * .pi * turns
                    let curR = spiralRadius * (1.0 - t)
                    let px = cx + cos(angle) * curR
                    let py = cy + sin(angle) * curR
                    if i == 0 { path.move(to: CGPoint(x: px, y: py)) }
                    else { path.addLine(to: CGPoint(x: px, y: py)) }
                }

                context.stroke(path, with: .color(isUnlocked ? Color.gray : Color(white: 0.25)), lineWidth: cellSize * 0.08)
            }
            .frame(width: r * 2, height: r * 2)
            .rotationEffect(.degrees(isUnlocked ? rotation : 0))

            // Center Dark Hole
            Circle()
                .fill(Color.black)
                .frame(width: r * 0.55, height: r * 0.55)

            if !isUnlocked {
                Image(systemName: "lock.fill")
                    .font(.system(size: cellSize * 0.30, weight: .bold))
                    .foregroundColor(.white.opacity(0.85))
            }
        }
    }
}

/**
 * Cartoon Red Apple with stem, green leaf, specular highlight, and bold outline.
 */
private struct CartoonAppleView: View {
    let cellSize: CGFloat
    let scale: CGFloat

    var body: some View {
        let r = cellSize * 0.36

        ZStack {
            // Wooden Curved Stem
            Path { path in
                path.move(to: CGPoint(x: 0, y: -r + 3))
                path.addQuadCurve(to: CGPoint(x: -cellSize * 0.05, y: -r - cellSize * 0.18), control: CGPoint(x: -cellSize * 0.08, y: -r - cellSize * 0.12))
                path.addLine(to: CGPoint(x: cellSize * 0.02, y: -r - cellSize * 0.18))
                path.addQuadCurve(to: CGPoint(x: cellSize * 0.03, y: -r + 3), control: CGPoint(x: 0, y: -r - cellSize * 0.10))
                path.closeSubpath()
            }
            .fill(Color(red: 0.26, green: 0.13, blue: 0.03))
            .overlay(
                Path { path in
                    path.move(to: CGPoint(x: 0, y: -r + 3))
                    path.addQuadCurve(to: CGPoint(x: -cellSize * 0.05, y: -r - cellSize * 0.18), control: CGPoint(x: -cellSize * 0.08, y: -r - cellSize * 0.12))
                }.stroke(Color.black, lineWidth: 1.5)
            )

            // Green Leaf pointing right
            Path { path in
                path.move(to: CGPoint(x: 0, y: -r - cellSize * 0.05))
                path.addQuadCurve(to: CGPoint(x: cellSize * 0.24, y: -r - cellSize * 0.08), control: CGPoint(x: cellSize * 0.16, y: -r - cellSize * 0.18))
                path.addQuadCurve(to: CGPoint(x: 0, y: -r - cellSize * 0.05), control: CGPoint(x: cellSize * 0.12, y: -r - cellSize * 0.01))
                path.closeSubpath()
            }
            .fill(Color(red: 0.12, green: 0.66, blue: 0.16))
            .overlay(
                Path { path in
                    path.move(to: CGPoint(x: 0, y: -r - cellSize * 0.05))
                    path.addQuadCurve(to: CGPoint(x: cellSize * 0.24, y: -r - cellSize * 0.08), control: CGPoint(x: cellSize * 0.16, y: -r - cellSize * 0.18))
                }.stroke(Color.black, lineWidth: 1.5)
            )

            // Red Apple Fruit Body with bold outline
            Circle()
                .fill(
                    RadialGradient(
                        colors: [Color(red: 1.0, green: 0.64, blue: 0.64), Color(red: 0.85, green: 0.10, blue: 0.10), Color(red: 0.58, green: 0.04, blue: 0.04)],
                        center: .topLeading,
                        startRadius: 2,
                        endRadius: r * 1.3
                    )
                )
                .frame(width: r * 2, height: r * 2)
                .overlay(
                    Circle().stroke(Color.black, lineWidth: 2.8)
                )
                .scaleEffect(scale)

            // Specular crescent shine
            Circle()
                .trim(from: 0.45, to: 0.75)
                .stroke(Color.white.opacity(0.6), lineWidth: 3.5)
                .frame(width: r * 1.3, height: r * 1.3)
                .offset(x: -r * 0.2, y: -r * 0.2)
        }
    }
}

/**
 * The Apple Worm with big googly eyes, PURPLE/MAGENTA LIPS, and lime-green underbelly.
 */
private struct AppleWormSnakeView: View {
    let snake: [Position]
    let facing: Direction
    let skin: SnakeSkin
    let isDead: Bool
    let cellSize: CGFloat

    var body: some View {
        let bodyRadius = cellSize * 0.40

        ZStack {
            // Pass 1: Bold black outline behind entire snake
            ForEach(0..<snake.count, id: \.self) { i in
                let pos = snake[i]
                Circle()
                    .fill(Color.black)
                    .frame(width: (bodyRadius + 3.5) * 2, height: (bodyRadius + 3.5) * 2)
                    .position(x: CGFloat(pos.x) * cellSize + cellSize / 2, y: CGFloat(pos.y) * cellSize + cellSize / 2)
            }

            // Pass 2: Green body segments
            ForEach(0..<snake.count, id: \.self) { i in
                let pos = snake[i]
                let isHead = (i == 0)

                ZStack {
                    Circle()
                        .fill(isHead ? skin.headColor : skin.bodyColor)
                        .frame(width: bodyRadius * 2, height: bodyRadius * 2)

                    if isHead {
                        // Lime throat underbelly
                        Circle()
                            .trim(from: 0.2, to: 0.8)
                            .stroke(skin.throatColor, lineWidth: bodyRadius * 0.35)
                            .frame(width: bodyRadius * 1.4, height: bodyRadius * 1.4)

                        // THE ICONIC PURPLE LIPS!
                        PurpleLipsView(bodyRadius: bodyRadius, facing: facing, lipsColor: skin.lipsColor)

                        // GOOGLY EYES
                        GooglyEyesView(bodyRadius: bodyRadius, facing: facing, isDead: isDead)
                    }
                }
                .position(x: CGFloat(pos.x) * cellSize + cellSize / 2, y: CGFloat(pos.y) * cellSize + cellSize / 2)
            }
        }
    }
}

private struct PurpleLipsView: View {
    let bodyRadius: CGFloat
    let facing: Direction
    let lipsColor: Color

    var body: some View {
        let lipW = bodyRadius * 0.48
        let lipH = bodyRadius * 0.72

        let offset: (x: CGFloat, y: CGFloat) = {
            switch facing {
            case .left: return (-bodyRadius - lipW * 0.25, bodyRadius * 0.12)
            case .right: return (bodyRadius + lipW * 0.25, bodyRadius * 0.12)
            case .up: return (0, -bodyRadius - lipH * 0.25)
            case .down: return (0, bodyRadius + lipH * 0.25)
            }
        }()

        RoundedRectangle(cornerRadius: lipW / 2)
            .fill(lipsColor)
            .frame(width: lipW, height: lipH)
            .overlay(
                RoundedRectangle(cornerRadius: lipW / 2)
                    .stroke(Color.black, lineWidth: 2.2)
            )
            .offset(x: offset.x, y: offset.y)
    }
}

private struct GooglyEyesView: View {
    let bodyRadius: CGFloat
    let facing: Direction
    let isDead: Bool

    var body: some View {
        let eyeR = bodyRadius * 0.38
        let pupilR = eyeR * 0.50
        let eyeSpacing = bodyRadius * 0.34
        let eyeY = -bodyRadius * 0.65

        let pupilOffset: (x: CGFloat, y: CGFloat) = {
            switch facing {
            case .up: return (0, -eyeR * 0.35)
            case .down: return (0, eyeR * 0.35)
            case .left: return (-eyeR * 0.40, 0)
            case .right: return (eyeR * 0.40, 0)
            }
        }()

        ZStack {
            // Left Eye
            EyeSingleView(eyeR: eyeR, pupilR: pupilR, pupilOffset: pupilOffset, isDead: isDead)
                .offset(x: -eyeSpacing, y: eyeY)

            // Right Eye
            EyeSingleView(eyeR: eyeR, pupilR: pupilR, pupilOffset: pupilOffset, isDead: isDead)
                .offset(x: eyeSpacing, y: eyeY)
        }
    }
}

private struct EyeSingleView: View {
    let eyeR: CGFloat
    let pupilR: CGFloat
    let pupilOffset: (x: CGFloat, y: CGFloat)
    let isDead: Bool

    var body: some View {
        ZStack {
            Circle()
                .fill(Color.white)
                .frame(width: eyeR * 2, height: eyeR * 2)
                .overlay(
                    Circle().stroke(Color.black, lineWidth: 2.2)
                )

            if isDead {
                Image(systemName: "xmark")
                    .font(.system(size: eyeR * 0.9, weight: .black))
                    .foregroundColor(.black)
            } else {
                Circle()
                    .fill(Color(white: 0.08))
                    .frame(width: pupilR * 2, height: pupilR * 2)
                    .offset(x: pupilOffset.x, y: pupilOffset.y)

                Circle()
                    .fill(Color.white)
                    .frame(width: pupilR * 0.7, height: pupilR * 0.7)
                    .offset(x: pupilOffset.x - pupilR * 0.25, y: pupilOffset.y - pupilR * 0.25)
            }
        }
    }
}

private struct SpikeView: View {
    let cellSize: CGFloat

    var body: some View {
        let w = cellSize * 0.85
        let h = cellSize * 0.78

        Path { path in
            path.move(to: CGPoint(x: -w / 2, y: h / 2))
            path.addLine(to: CGPoint(x: 0, y: -h / 2))
            path.addLine(to: CGPoint(x: w / 2, y: h / 2))
            path.closeSubpath()
        }
        .fill(
            LinearGradient(
                colors: [Color(red: 0.90, green: 0.22, blue: 0.21), Color(red: 0.38, green: 0.49, blue: 0.55)],
                startPoint: .top,
                endPoint: .bottom
            )
        )
        .overlay(
            Path { path in
                path.move(to: CGPoint(x: -w / 2, y: h / 2))
                path.addLine(to: CGPoint(x: 0, y: -h / 2))
                path.addLine(to: CGPoint(x: w / 2, y: h / 2))
                path.closeSubpath()
            }.stroke(Color(red: 0.13, green: 0.06, blue: 0.02), lineWidth: 3.2)
        )
    }
}

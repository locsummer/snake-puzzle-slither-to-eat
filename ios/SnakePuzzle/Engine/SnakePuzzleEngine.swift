import Foundation

public enum MoveResult {
    case blocked
    case moved
    case appleEaten(position: Position, portalJustOpened: Bool)
    case portalLockedNotice
    case levelWon
    case levelLost(reason: String)
}

public final class SnakePuzzleEngine: ObservableObject {
    public let level: Level
    @Published public private(set) var currentState: GameState
    @Published public private(set) var canUndo: Bool = false

    private var history: [GameState] = []
    public let solidPositions: Set<Position>
    public let spikePositions: Set<Position>
    public let portalPosition: Position?

    public init(level: Level) {
        self.level = level
        self.solidPositions = level.solidPositions
        self.spikePositions = level.spikePositions
        self.portalPosition = level.portalPosition

        let initialApples = Set(level.applePositions)
        let initialSnake = level.snakePositions

        var initialFacing: Direction = .right
        if initialSnake.count > 1 {
            let dx = initialSnake[0].x - initialSnake[1].x
            let dy = initialSnake[0].y - initialSnake[1].y
            if dx > 0 { initialFacing = .right }
            else if dx < 0 { initialFacing = .left }
            else if dy > 0 { initialFacing = .down }
            else if dy < 0 { initialFacing = .up }
        }

        let isUnlocked = initialApples.isEmpty

        self.currentState = GameState(
            snake: initialSnake,
            remainingApples: initialApples,
            movesCount: 0,
            status: .playing,
            facingDirection: initialFacing,
            noticeMessage: isUnlocked ? "Portal is open! Reach the vortex!" : nil,
            isPortalUnlocked: isUnlocked
        )
    }

    public func reset() {
        history.removeAll()
        canUndo = false
        let initialApples = Set(level.applePositions)
        let initialSnake = level.snakePositions
        var initialFacing: Direction = .right
        if initialSnake.count > 1 {
            let dx = initialSnake[0].x - initialSnake[1].x
            let dy = initialSnake[0].y - initialSnake[1].y
            if dx > 0 { initialFacing = .right }
            else if dx < 0 { initialFacing = .left }
            else if dy > 0 { initialFacing = .down }
            else if dy < 0 { initialFacing = .up }
        }
        let isUnlocked = initialApples.isEmpty
        currentState = GameState(
            snake: initialSnake,
            remainingApples: initialApples,
            movesCount: 0,
            status: .playing,
            facingDirection: initialFacing,
            noticeMessage: isUnlocked ? "Portal is open! Reach the vortex!" : nil,
            isPortalUnlocked: isUnlocked
        )
    }

    public func undo() -> Bool {
        guard let previous = history.popLast() else { return false }
        currentState = previous
        canUndo = !history.isEmpty
        return true
    }

    @discardableResult
    public func move(in direction: Direction) -> MoveResult {
        guard currentState.status == .playing else { return .blocked }

        let currentSnake = currentState.snake
        guard let head = currentSnake.first else { return .blocked }

        let newHead = head + direction.delta

        // 1. Cannot reverse directly into neck
        if currentSnake.count > 1 && newHead == currentSnake[1] {
            return .blocked
        }

        // 2. Out of bounds check
        if newHead.x < 0 || newHead.x >= level.width || newHead.y < 0 || newHead.y >= level.height {
            return .blocked
        }

        // 3. Solid wall collision
        if solidPositions.contains(newHead) {
            return .blocked
        }

        let isEatingApple = currentState.remainingApples.contains(newHead)

        // 4. Body collision check: if not eating apple, the tail moves away
        let occupied = isEatingApple ? currentSnake : Array(currentSnake.dropLast())
        if occupied.contains(newHead) {
            return .blocked
        }

        // Save state for undo
        history.append(currentState)
        canUndo = true

        // Form new snake
        var nextSnake: [Position] = [newHead]
        if isEatingApple {
            nextSnake.append(contentsOf: currentSnake)
        } else {
            nextSnake.append(contentsOf: currentSnake.dropLast())
        }

        var nextApples = currentState.remainingApples
        var appleEatenThisTurn = false
        if isEatingApple {
            nextApples.remove(newHead)
            appleEatenThisTurn = true
        }

        let isPortalNowUnlocked = nextApples.isEmpty
        let portalJustOpened = appleEatenThisTurn && isPortalNowUnlocked && !currentState.isPortalUnlocked

        var notice: String? = nil
        if portalJustOpened {
            notice = "✨ PORTAL OPEN! Slither to the vortex!"
        }

        var nextStatus: GameStatus = .playing

        // Check if head moved onto portal
        let reachedPortal = (portalPosition != nil && newHead == portalPosition)
        if reachedPortal {
            if isPortalNowUnlocked {
                nextStatus = .won
            } else {
                notice = "🔒 Portal locked! Collect all apples first!"
            }
        }

        // Check if head hit spike directly
        if spikePositions.contains(newHead) && nextStatus != .won {
            nextStatus = .lost(reason: "Ouch! You hit the sharp spikes!")
        }

        // Gravity loop
        var resolvedSnake: [Position] = nextSnake
        if nextStatus == .playing {
            var falling = true
            while falling {
                if resolvedSnake.contains(where: { spikePositions.contains($0) }) {
                    nextStatus = .lost(reason: "Ouch! You fell into spikes!")
                    falling = false
                    break
                }

                // Check support
                var supported = false
                for seg in resolvedSnake {
                    let below = Position(x: seg.x, y: seg.y + 1)
                    if solidPositions.contains(below) {
                        supported = true
                        break
                    }
                }

                if supported {
                    falling = false
                } else {
                    resolvedSnake = resolvedSnake.map { Position(x: $0.x, y: $0.y + 1) }

                    if let portal = portalPosition, resolvedSnake.first == portal && isPortalNowUnlocked {
                        nextStatus = .won
                        falling = false
                        break
                    }

                    if resolvedSnake.contains(where: { $0.y >= level.height }) {
                        nextStatus = .lost(reason: "You fell into the infinite abyss!")
                        falling = false
                        break
                    }
                }
            }
        }

        // Settle check
        if nextStatus == .playing {
            if resolvedSnake.contains(where: { spikePositions.contains($0) }) {
                nextStatus = .lost(reason: "Ouch! You landed on spikes!")
            } else if let portal = portalPosition, resolvedSnake.first == portal && isPortalNowUnlocked {
                nextStatus = .won
            }
        }

        currentState = GameState(
            snake: resolvedSnake,
            remainingApples: nextApples,
            movesCount: currentState.movesCount + 1,
            status: nextStatus,
            facingDirection: direction,
            noticeMessage: notice,
            isPortalUnlocked: isPortalNowUnlocked,
            lastEatenPosition: appleEatenThisTurn ? newHead : nil
        )

        switch nextStatus {
        case .won:
            return .levelWon
        case .lost(let reason):
            return .levelLost(reason: reason)
        case .playing:
            if appleEatenThisTurn {
                return .appleEaten(position: newHead, portalJustOpened: portalJustOpened)
            } else if reachedPortal && !isPortalNowUnlocked {
                return .portalLockedNotice
            } else {
                return .moved
            }
        }
    }
}

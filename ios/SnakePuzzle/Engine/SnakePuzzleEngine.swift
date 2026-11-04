import Foundation

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

        self.currentState = GameState(
            snake: initialSnake,
            remainingApples: initialApples,
            movesCount: 0,
            status: .playing,
            facingDirection: initialFacing
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
        currentState = GameState(
            snake: initialSnake,
            remainingApples: initialApples,
            movesCount: 0,
            status: .playing,
            facingDirection: initialFacing
        )
    }

    public func undo() -> Bool {
        guard let previous = history.popLast() else { return false }
        currentState = previous
        canUndo = !history.isEmpty
        return true
    }

    @discardableResult
    public func move(in direction: Direction) -> Bool {
        guard currentState.status == .playing else { return false }

        let currentSnake = currentState.snake
        guard let head = currentSnake.first else { return false }

        let newHead = head + direction.delta

        // 1. Cannot reverse directly into neck
        if currentSnake.count > 1 && newHead == currentSnake[1] {
            return false
        }

        // 2. Out of bounds
        if newHead.x < 0 || newHead.x >= level.width || newHead.y < 0 || newHead.y >= level.height {
            return false
        }

        // 3. Solid wall collision
        if solidPositions.contains(newHead) {
            return false
        }

        let isEatingApple = currentState.remainingApples.contains(newHead)

        // 4. Body collision check
        let occupied = isEatingApple ? currentSnake : Array(currentSnake.dropLast())
        if occupied.contains(newHead) {
            return false
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
        if isEatingApple {
            nextApples.remove(newHead)
        }

        var nextStatus: GameStatus = .playing

        // Check if head hit spike directly
        if spikePositions.contains(newHead) {
            currentState = GameState(
                snake: nextSnake,
                remainingApples: nextApples,
                movesCount: currentState.movesCount + 1,
                status: .lost(reason: "Ouch! You hit the sharp spikes!"),
                facingDirection: direction
            )
            return true
        }

        // Gravity loop
        var falling = true
        while falling {
            // Check spike collision
            for seg in nextSnake {
                if spikePositions.contains(seg) {
                    nextStatus = .lost(reason: "Ouch! You fell into spikes!")
                    falling = false
                    break
                }
            }
            if nextStatus != .playing { break }

            // Check support: Is at least one segment resting on a solid block directly underneath?
            var supported = false
            for seg in nextSnake {
                let below = Position(x: seg.x, y: seg.y + 1)
                if solidPositions.contains(below) {
                    supported = true
                    break
                }
            }

            if supported {
                falling = false
            } else {
                // Drop 1 cell down
                nextSnake = nextSnake.map { Position(x: $0.x, y: $0.y + 1) }

                // Check out of bounds / abyss
                if nextSnake.contains(where: { $0.y >= level.height }) {
                    nextStatus = .lost(reason: "You fell into the infinite abyss!")
                    falling = false
                    break
                }
            }
        }

        // Check spike collision after settling
        if nextStatus == .playing {
            for seg in nextSnake {
                if spikePositions.contains(seg) {
                    nextStatus = .lost(reason: "Ouch! You landed on spikes!")
                    break
                }
            }
        }

        // Win check: Head at portal and all apples collected
        if nextStatus == .playing, let portal = portalPosition {
            if nextApples.isEmpty && nextSnake.first == portal {
                nextStatus = .won
            }
        }

        currentState = GameState(
            snake: nextSnake,
            remainingApples: nextApples,
            movesCount: currentState.movesCount + 1,
            status: nextStatus,
            facingDirection: direction
        )

        return true
    }
}

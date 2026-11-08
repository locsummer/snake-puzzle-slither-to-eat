import Foundation

public enum MoveResult {
    case blocked
    case moved
    case appleEaten(position: Position, portalJustOpened: Bool, points: Int)
    case portalLockedNotice
    case levelWon(earnedScore: Int, totalScore: Int, timeBonus: Int)
    case lifeLost(remainingLives: Int, reason: String)
    case gameOver(finalScore: Int, reason: String)
}

public final class SnakePuzzleEngine: ObservableObject {
    public let level: Level
    public let initialScore: Int
    public let highScore: Int
    public let initialLives: Int

    @Published public private(set) var currentState: GameState
    @Published public private(set) var canUndo: Bool = false

    private var history: [GameState] = []
    public let solidPositions: Set<Position>
    public let spikePositions: Set<Position>
    public let portalPosition: Position?

    public init(level: Level, initialScore: Int = 0, highScore: Int = 0, initialLives: Int = 3) {
        self.level = level
        self.initialScore = initialScore
        self.highScore = highScore
        self.initialLives = initialLives
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
            remainingApples = initialApples,
            movesCount = 0,
            status: .playing,
            facingDirection = initialFacing,
            noticeMessage = isUnlocked ? "Portal is open! Reach the vortex!" : nil,
            isPortalUnlocked = isUnlocked,
            score = initialScore,
            highScore = highScore,
            levelScore = 0,
            lives = initialLives,
            elapsedSeconds = 0,
            timeBonus = 0
        )
    }

    public func tickTimer() {
        if case .playing = currentState.status {
            currentState.elapsedSeconds += 1
        }
    }

    public func reset(keepLives: Int? = nil) {
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
            remainingApples = initialApples,
            movesCount = 0,
            status: .playing,
            facingDirection = initialFacing,
            noticeMessage = isUnlocked ? "Portal is open! Reach the vortex!" : nil,
            isPortalUnlocked = isUnlocked,
            score = initialScore,
            highScore = highScore,
            levelScore = 0,
            lives = keepLives ?? currentState.lives,
            elapsedSeconds = 0,
            timeBonus = 0
        )
    }

    public func undo() -> Bool {
        guard let previous = history.popLast() else { return false }
        currentState = previous
        canUndo = !history.isEmpty
        return true
    }

    @discardableResult
    public func move(_ direction: Direction) -> MoveResult {
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

        // 4. Body collision
        let occupied = isEatingApple ? currentSnake : Array(currentSnake.dropLast())
        if occupied.contains(newHead) {
            return .blocked
        }

        history.append(currentState)
        canUndo = true

        var nextSnake: [Position] = [newHead]
        if isEatingApple {
            nextSnake.append(contentsOf: currentSnake)
        } else {
            nextSnake.append(contentsOf: currentSnake.dropLast())
        }

        var nextApples = currentState.remainingApples
        var appleEatenThisTurn = false
        var nextLevelScore = currentState.levelScore
        if isEatingApple {
            nextApples.remove(newHead)
            appleEatenThisTurn = true
            nextLevelScore += 100
        }

        let isPortalNowUnlocked = nextApples.isEmpty
        let portalJustOpened = appleEatenThisTurn && isPortalNowUnlocked && !currentState.isPortalUnlocked

        var notice: String? = nil
        if portalJustOpened {
            notice = "✨ PORTAL OPEN! Slither to the vortex!"
        }

        var nextStatus: GameStatus = .playing
        var deathReason: String? = nil

        let reachedPortal = (portalPosition != nil && newHead == portalPosition)
        if reachedPortal {
            if isPortalNowUnlocked {
                nextStatus = .won
            } else {
                notice = "🔒 Portal locked! Collect all apples first!"
            }
        }

        if spikePositions.contains(newHead) && nextStatus != .won {
            deathReason = "Ouch! You hit the sharp spikes!"
        }

        var resolvedSnake = nextSnake
        if nextStatus == .playing && deathReason == null {
            var falling = true
            while falling {
                if resolvedSnake.contains(where: { spikePositions.contains($0) }) {
                    deathReason = "Ouch! You fell into spikes!"
                    falling = false
                    break
                }

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
                        deathReason = "You fell into the infinite abyss!"
                        falling = false
                        break
                    }
                }
            }
        }

        if nextStatus == .playing && deathReason == nil {
            if resolvedSnake.contains(where: { spikePositions.contains($0) }) {
                deathReason = "Ouch! You landed on spikes!"
            } else if let portal = portalPosition, resolvedSnake.first == portal && isPortalNowUnlocked {
                nextStatus = .won
            }
        }

        let newMoves = currentState.movesCount + 1
        var totalRunScore = initialScore + nextLevelScore
        var earnedTimeBonus = 0

        if nextStatus == .won {
            let optimalBonus = max(0, (level.optimalMoves * 2 - newMoves) * 15)
            earnedTimeBonus = max(50, 1000 - currentState.elapsedSeconds * 15)
            let winBonus = 500 + optimalBonus + earnedTimeBonus
            nextLevelScore += winBonus
            totalRunScore = initialScore + nextLevelScore
        }

        var resultingLives = currentState.lives
        if let reason = deathReason {
            resultingLives = currentState.lives - 1
            if resultingLives > 0 {
                nextStatus = .lifeLost(reason: reason, remainingLives: resultingLives)
            } else {
                nextStatus = .gameOver(reason: reason, finalScore: totalRunScore)
            }
        }

        currentState = GameState(
            snake = resolvedSnake,
            remainingApples = nextApples,
            movesCount = newMoves,
            status = nextStatus,
            facingDirection = direction,
            noticeMessage = notice,
            isPortalUnlocked = isPortalNowUnlocked,
            lastEatenPosition: appleEatenThisTurn ? newHead : nil,
            score: totalRunScore,
            highScore: max(highScore, totalRunScore),
            levelScore: nextLevelScore,
            lives: resultingLives,
            elapsedSeconds: currentState.elapsedSeconds,
            timeBonus: earnedTimeBonus
        )

        switch nextStatus {
        case .won:
            return .levelWon(earnedScore: nextLevelScore, totalScore: totalRunScore, timeBonus: earnedTimeBonus)
        case .lifeLost(let reason, let lives):
            return .lifeLost(remainingLives: lives, reason: reason)
        case .gameOver(let reason, let score):
            return .gameOver(finalScore: score, reason: reason)
        case .playing:
            if appleEatenThisTurn {
                return .appleEaten(position: newHead, portalJustOpened: portalJustOpened, points: 100)
            } else if reachedPortal && !isPortalNowUnlocked {
                return .portalLockedNotice
            } else {
                return .moved
            }
        }
    }
}

import Foundation

public enum GameStatus: Equatable {
    case playing
    case won
    case lost(reason: String)
}

public struct GameState: Equatable {
    public var snake: [Position]
    public var remainingApples: Set<Position>
    public var movesCount: Int
    public var status: GameStatus
    public var facingDirection: Direction
    public var noticeMessage: String?
    public var isPortalUnlocked: Bool
    public var lastEatenPosition: Position?
    public var score: Int
    public var highScore: Int
    public var levelScore: Int

    public init(
        snake: [Position],
        remainingApples: Set<Position>,
        movesCount: Int = 0,
        status: GameStatus = .playing,
        facingDirection: Direction = .right,
        noticeMessage: String? = nil,
        isPortalUnlocked: Bool = false,
        lastEatenPosition: Position? = nil,
        score: Int = 0,
        highScore: Int = 0,
        levelScore: Int = 0
    ) {
        self.snake = snake
        self.remainingApples = remainingApples
        self.movesCount = movesCount
        self.status = status
        self.facingDirection = facingDirection
        self.noticeMessage = noticeMessage
        self.isPortalUnlocked = isPortalUnlocked
        self.lastEatenPosition = lastEatenPosition
        self.score = score
        self.highScore = highScore
        self.levelScore = levelScore
    }

    public var head: Position {
        snake.first ?? Position(x: 0, y: 0)
    }

    public var isApplesCollected: Bool {
        remainingApples.isEmpty
    }

    public var isDead: Bool {
        if case .lost = status { return true }
        return false
    }
}

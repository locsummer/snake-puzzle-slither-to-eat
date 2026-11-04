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

    public init(
        snake: [Position],
        remainingApples: Set<Position>,
        movesCount: Int = 0,
        status: GameStatus = .playing,
        facingDirection: Direction = .right
    ) {
        self.snake = snake
        self.remainingApples = remainingApples
        self.movesCount = movesCount
        self.status = status
        self.facingDirection = facingDirection
    }

    public var head: Position {
        snake.first ?? Position(x: 0, y: 0)
    }

    public var isApplesCollected: Bool {
        remainingApples.isEmpty
    }
}

package com.snakepuzzle.game.model

sealed class GameStatus {
    object Playing : GameStatus()
    object Won : GameStatus()
    data class Lost(val reason: String) : GameStatus()
}

data class GameState(
    val snake: List<Position>,
    val remainingApples: Set<Position>,
    val movesCount: Int = 0,
    val status: GameStatus = GameStatus.Playing,
    val facingDirection: Direction = Direction.RIGHT
) {
    val head: Position
        get() = snake.firstOrNull() ?: Position(0, 0)

    val isApplesCollected: Boolean
        get() = remainingApples.isEmpty()
}

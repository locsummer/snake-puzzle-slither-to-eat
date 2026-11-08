package com.snakepuzzle.game.model

data class Particle(
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val color: Long,
    val alpha: Float = 1.0f,
    val size: Float = 8f
)

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
    val facingDirection: Direction = Direction.RIGHT,
    val noticeMessage: String? = null,
    val isPortalUnlocked: Boolean = false,
    val lastEatenPosition: Position? = null,
    val score: Int = 0,
    val highScore: Int = 0,
    val levelScore: Int = 0
) {
    val head: Position
        get() = snake.firstOrNull() ?: Position(0, 0)

    val isApplesCollected: Boolean
        get() = remainingApples.isEmpty()

    val isDead: Boolean
        get() = status is GameStatus.Lost
}

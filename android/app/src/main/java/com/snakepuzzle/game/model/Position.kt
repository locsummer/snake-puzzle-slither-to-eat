package com.snakepuzzle.game.model

data class Position(val x: Int, val y: Int) {
    operator fun plus(other: Position): Position = Position(x + other.x, y + other.y)
}

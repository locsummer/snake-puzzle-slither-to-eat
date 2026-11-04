package com.snakepuzzle.game.model

enum class Direction(val code: String, val dx: Int, val dy: Int) {
    UP("U", 0, -1),
    DOWN("D", 0, 1),
    LEFT("L", -1, 0),
    RIGHT("R", 1, 0);

    val delta: Position get() = Position(dx, dy)

    val opposite: Direction
        get() = when (this) {
            UP -> DOWN
            DOWN -> UP
            LEFT -> RIGHT
            RIGHT -> LEFT
        }
}

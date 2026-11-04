package com.snakepuzzle.game.model

enum class CellType(val symbol: Char) {
    EMPTY('.'),
    SOLID('S'),
    APPLE('A'),
    PORTAL('O'),
    SPIKE('X')
}

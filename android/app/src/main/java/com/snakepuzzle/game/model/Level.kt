package com.snakepuzzle.game.model

data class Level(
    val id: Int,
    val name: String,
    val world: String = "Green Meadows",
    val width: Int,
    val height: Int,
    val grid: List<String>,
    val snake: List<List<Int>>,
    val targetApples: Int,
    val optimalMoves: Int = 10
) {
    val snakePositions: List<Position>
        get() = snake.map { Position(it[0], it[1]) }

    val applePositions: List<Position>
        get() {
            val list = mutableListOf<Position>()
            for (y in 0 until height) {
                if (y < grid.size) {
                    val row = grid[y]
                    for (x in 0 until width) {
                        if (x < row.length && row[x] == 'A') {
                            list.add(Position(x, y))
                        }
                    }
                }
            }
            return list
        }

    val portalPosition: Position?
        get() {
            for (y in 0 until height) {
                if (y < grid.size) {
                    val row = grid[y]
                    for (x in 0 until width) {
                        if (x < row.length && row[x] == 'O') {
                            return Position(x, y)
                        }
                    }
                }
            }
            return null
        }

    val spikePositions: Set<Position>
        get() {
            val set = mutableSetOf<Position>()
            for (y in 0 until height) {
                if (y < grid.size) {
                    val row = grid[y]
                    for (x in 0 until width) {
                        if (x < row.length && row[x] == 'X') {
                            set.add(Position(x, y))
                        }
                    }
                }
            }
            return set
        }

    val solidPositions: Set<Position>
        get() {
            val set = mutableSetOf<Position>()
            for (y in 0 until height) {
                if (y < grid.size) {
                    val row = grid[y]
                    for (x in 0 until width) {
                        if (x < row.length && row[x] == 'S') {
                            set.add(Position(x, y))
                        }
                    }
                }
            }
            return set
        }
}

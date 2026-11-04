package com.snakepuzzle.game.engine

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.snakepuzzle.game.model.*

class SnakePuzzleEngine(val level: Level) {
    val solidPositions: Set<Position> = level.solidPositions
    val spikePositions: Set<Position> = level.spikePositions
    val portalPosition: Position? = level.portalPosition

    private val history = ArrayDeque<GameState>()

    var currentState by mutableStateOf(createInitialState())
        private set

    var canUndo by mutableStateOf(false)
        private set

    private fun createInitialState(): GameState {
        val initialApples = level.applePositions.toSet()
        val initialSnake = level.snakePositions

        var initialFacing = Direction.RIGHT
        if (initialSnake.size > 1) {
            val dx = initialSnake[0].x - initialSnake[1].x
            val dy = initialSnake[0].y - initialSnake[1].y
            initialFacing = when {
                dx > 0 -> Direction.RIGHT
                dx < 0 -> Direction.LEFT
                dy > 0 -> Direction.DOWN
                dy < 0 -> Direction.UP
                else -> Direction.RIGHT
            }
        }

        return GameState(
            snake = initialSnake,
            remainingApples = initialApples,
            movesCount = 0,
            status = GameStatus.Playing,
            facingDirection = initialFacing
        )
    }

    fun reset() {
        history.clear()
        canUndo = false
        currentState = createInitialState()
    }

    fun undo(): Boolean {
        if (history.isEmpty()) return false
        val previous = history.removeLast()
        currentState = previous
        canUndo = history.isNotEmpty()
        return true
    }

    fun move(direction: Direction): Boolean {
        if (currentState.status !is GameStatus.Playing) return false

        val currentSnake = currentState.snake
        val head = currentSnake.firstOrNull() ?: return false
        val newHead = head + direction.delta

        // 1. Cannot reverse directly into neck
        if (currentSnake.size > 1 && newHead == currentSnake[1]) {
            return false
        }

        // 2. Out of bounds
        if (newHead.x < 0 || newHead.x >= level.width || newHead.y < 0 || newHead.y >= level.height) {
            return false
        }

        // 3. Solid wall collision
        if (solidPositions.contains(newHead)) {
            return false
        }

        val isEatingApple = currentState.remainingApples.contains(newHead)

        // 4. Body collision check
        val occupied = if (isEatingApple) currentSnake else currentSnake.dropLast(1)
        if (occupied.contains(newHead)) {
            return false
        }

        // Save state for undo
        history.addLast(currentState)
        canUndo = true

        // Form new snake
        val nextSnake = mutableListOf<Position>()
        nextSnake.add(newHead)
        if (isEatingApple) {
            nextSnake.addAll(currentSnake)
        } else {
            nextSnake.addAll(currentSnake.dropLast(1))
        }

        val nextApples = currentState.remainingApples.toMutableSet()
        if (isEatingApple) {
            nextApples.remove(newHead)
        }

        var nextStatus: GameStatus = GameStatus.Playing

        // Check if head hit spike directly
        if (spikePositions.contains(newHead)) {
            currentState = GameState(
                snake = nextSnake,
                remainingApples = nextApples,
                movesCount = currentState.movesCount + 1,
                status = GameStatus.Lost("Ouch! You hit the sharp spikes!"),
                facingDirection = direction
            )
            return true
        }

        // Gravity loop
        var falling = true
        var resolvedSnake: List<Position> = nextSnake
        while (falling) {
            // Check spike collision during fall
            for (seg in resolvedSnake) {
                if (spikePositions.contains(seg)) {
                    nextStatus = GameStatus.Lost("Ouch! You fell into spikes!")
                    falling = false
                    break
                }
            }
            if (nextStatus !is GameStatus.Playing) break

            // Check support: Is at least one segment resting on a solid block directly underneath?
            var supported = false
            for (seg in resolvedSnake) {
                val below = Position(seg.x, seg.y + 1)
                if (solidPositions.contains(below)) {
                    supported = true
                    break
                }
            }

            if (supported) {
                falling = false
            } else {
                // Drop 1 cell down
                resolvedSnake = resolvedSnake.map { Position(it.x, it.y + 1) }

                // Check out of bounds / abyss
                if (resolvedSnake.any { it.y >= level.height }) {
                    nextStatus = GameStatus.Lost("You fell into the infinite abyss!")
                    falling = false
                    break
                }
            }
        }

        // Check spike collision after settling
        if (nextStatus is GameStatus.Playing) {
            for (seg in resolvedSnake) {
                if (spikePositions.contains(seg)) {
                    nextStatus = GameStatus.Lost("Ouch! You landed on spikes!")
                    break
                }
            }
        }

        // Win check
        if (nextStatus is GameStatus.Playing && portalPosition != null) {
            if (nextApples.isEmpty() && resolvedSnake.firstOrNull() == portalPosition) {
                nextStatus = GameStatus.Won
            }
        }

        currentState = GameState(
            snake = resolvedSnake,
            remainingApples = nextApples,
            movesCount = currentState.movesCount + 1,
            status = nextStatus,
            facingDirection = direction
        )

        return true
    }
}

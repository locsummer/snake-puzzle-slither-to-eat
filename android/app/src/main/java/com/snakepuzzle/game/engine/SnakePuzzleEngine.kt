package com.snakepuzzle.game.engine

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.snakepuzzle.game.model.*
import kotlin.math.max

class SnakePuzzleEngine(
    val level: Level,
    val initialScore: Int = 0,
    val highScore: Int = 0,
    val initialLives: Int = 3
) {
    val solidPositions: Set<Position> = level.solidPositions
    val spikePositions: Set<Position> = level.spikePositions
    val portalPosition: Position? = level.portalPosition

    private val history = ArrayDeque<GameState>()

    var currentState by mutableStateOf(createInitialState())
        private set

    var canUndo by mutableStateOf(false)
        private set

    private fun createInitialState(lives: Int = initialLives): GameState {
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

        val portalUnlocked = initialApples.isEmpty()

        return GameState(
            snake = initialSnake,
            remainingApples = initialApples,
            movesCount = 0,
            status = GameStatus.Playing,
            facingDirection = initialFacing,
            noticeMessage = if (portalUnlocked) "Portal is open! Reach the vortex!" else null,
            isPortalUnlocked = portalUnlocked,
            score = initialScore,
            highScore = highScore,
            levelScore = 0,
            lives = lives,
            elapsedSeconds = 0,
            timeBonus = 0
        )
    }

    fun tickTimer() {
        if (currentState.status is GameStatus.Playing) {
            currentState = currentState.copy(elapsedSeconds = currentState.elapsedSeconds + 1)
        }
    }

    fun reset(keepLives: Int = currentState.lives) {
        history.clear()
        canUndo = false
        currentState = createInitialState(lives = keepLives)
    }

    fun undo(): Boolean {
        if (history.isEmpty()) return false
        val previous = history.removeLast()
        currentState = previous
        canUndo = history.isNotEmpty()
        return true
    }

    sealed class MoveResult {
        object Blocked : MoveResult()
        object Moved : MoveResult()
        data class AppleEaten(val pos: Position, val portalJustOpened: Boolean, val points: Int) : MoveResult()
        object PortalLockedNotice : MoveResult()
        data class LevelWon(val earnedScore: Int, val totalScore: Int, val timeBonus: Int) : MoveResult()
        data class LifeLost(val remainingLives: Int, val reason: String) : MoveResult()
        data class GameOver(val finalScore: Int, val reason: String) : MoveResult()
    }

    fun move(direction: Direction): MoveResult {
        if (currentState.status !is GameStatus.Playing) return MoveResult.Blocked

        val currentSnake = currentState.snake
        val head = currentSnake.firstOrNull() ?: return MoveResult.Blocked
        val newHead = head + direction.delta

        // 1. Cannot reverse directly into neck
        if (currentSnake.size > 1 && newHead == currentSnake[1]) {
            return MoveResult.Blocked
        }

        // 2. Out of bounds check
        if (newHead.x < 0 || newHead.x >= level.width || newHead.y < 0 || newHead.y >= level.height) {
            return MoveResult.Blocked
        }

        // 3. Solid wall collision
        if (solidPositions.contains(newHead)) {
            return MoveResult.Blocked
        }

        val isEatingApple = currentState.remainingApples.contains(newHead)

        // 4. Body collision
        val occupied = if (isEatingApple) currentSnake else currentSnake.dropLast(1)
        if (occupied.contains(newHead)) {
            return MoveResult.Blocked
        }

        // Save current state for undo
        history.addLast(currentState)
        canUndo = true

        // Form new snake segments
        val nextSnake = mutableListOf<Position>()
        nextSnake.add(newHead)
        if (isEatingApple) {
            nextSnake.addAll(currentSnake) // Grows +1
        } else {
            nextSnake.addAll(currentSnake.dropLast(1)) // Advances normally
        }

        val nextApples = currentState.remainingApples.toMutableSet()
        var appleEatenThisTurn = false
        var nextLevelScore = currentState.levelScore
        if (isEatingApple) {
            nextApples.remove(newHead)
            appleEatenThisTurn = true
            nextLevelScore += 100 // +100 points for eating an apple
        }

        val isPortalNowUnlocked = nextApples.isEmpty()
        val portalJustOpened = appleEatenThisTurn && isPortalNowUnlocked && !currentState.isPortalUnlocked

        var notice: String? = null
        if (portalJustOpened) {
            notice = "✨ PORTAL OPEN! Slither to the vortex!"
        }

        var nextStatus: GameStatus = GameStatus.Playing
        var deathReason: String? = null

        // Check if head moved onto portal
        val reachedPortal = (portalPosition != null && newHead == portalPosition)
        if (reachedPortal) {
            if (isPortalNowUnlocked) {
                nextStatus = GameStatus.Won
            } else {
                notice = "🔒 Portal locked! Collect all apples first!"
            }
        }

        // Check if head hit spike directly
        if (spikePositions.contains(newHead) && nextStatus !is GameStatus.Won) {
            deathReason = "Ouch! You hit the sharp spikes!"
        }

        // Gravity simulation loop if still playing
        var resolvedSnake: List<Position> = nextSnake
        if (nextStatus is GameStatus.Playing && deathReason == null) {
            var falling = true
            while (falling) {
                // Check if any segment is on a spike during fall
                if (resolvedSnake.any { spikePositions.contains(it) }) {
                    deathReason = "Ouch! You fell into spikes!"
                    falling = false
                    break
                }

                // Check support
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
                    // Fall 1 cell down
                    resolvedSnake = resolvedSnake.map { Position(it.x, it.y + 1) }

                    // Check if head fell into portal while unlocked
                    if (portalPosition != null && resolvedSnake.firstOrNull() == portalPosition && isPortalNowUnlocked) {
                        nextStatus = GameStatus.Won
                        falling = false
                        break
                    }

                    // Check fall out of bounds
                    if (resolvedSnake.any { it.y >= level.height }) {
                        deathReason = "You fell into the infinite abyss!"
                        falling = false
                        break
                    }
                }
            }
        }

        // Check spikes after falling settled
        if (nextStatus is GameStatus.Playing && deathReason == null) {
            if (resolvedSnake.any { spikePositions.contains(it) }) {
                deathReason = "Ouch! You landed on spikes!"
            } else if (portalPosition != null && resolvedSnake.firstOrNull() == portalPosition && isPortalNowUnlocked) {
                nextStatus = GameStatus.Won
            }
        }

        val newMovesCount = currentState.movesCount + 1
        var totalRunScore = initialScore + nextLevelScore
        var earnedTimeBonus = 0

        // Handle victory calculation
        if (nextStatus is GameStatus.Won) {
            val optimalBonus = max(0, (level.optimalMoves * 2 - newMovesCount) * 15)
            // Time bonus based on elapsed seconds: faster completion gives higher bonus!
            earnedTimeBonus = max(50, 1000 - currentState.elapsedSeconds * 15)
            val winBonus = 500 + optimalBonus + earnedTimeBonus
            nextLevelScore += winBonus
            totalRunScore = initialScore + nextLevelScore
        }

        // Handle death calculation with 3 lives system
        var resultingLives = currentState.lives
        if (deathReason != null) {
            resultingLives = currentState.lives - 1
            if (resultingLives > 0) {
                nextStatus = GameStatus.LifeLost(deathReason, resultingLives)
            } else {
                nextStatus = GameStatus.GameOver(deathReason, totalRunScore)
            }
        }

        currentState = GameState(
            snake = resolvedSnake,
            remainingApples = nextApples,
            movesCount = newMovesCount,
            status = nextStatus,
            facingDirection = direction,
            noticeMessage = notice,
            isPortalUnlocked = isPortalNowUnlocked,
            lastEatenPosition = if (appleEatenThisTurn) newHead else null,
            score = totalRunScore,
            highScore = max(highScore, totalRunScore),
            levelScore = nextLevelScore,
            lives = resultingLives,
            elapsedSeconds = currentState.elapsedSeconds,
            timeBonus = earnedTimeBonus
        )

        return when (nextStatus) {
            is GameStatus.Won -> MoveResult.LevelWon(nextLevelScore, totalRunScore, earnedTimeBonus)
            is GameStatus.LifeLost -> MoveResult.LifeLost(resultingLives, deathReason ?: "Lost 1 Life")
            is GameStatus.GameOver -> MoveResult.GameOver(totalRunScore, deathReason ?: "Game Over")
            is GameStatus.Playing -> {
                if (appleEatenThisTurn) MoveResult.AppleEaten(newHead, portalJustOpened, 100)
                else if (reachedPortal && !isPortalNowUnlocked) MoveResult.PortalLockedNotice
                else MoveResult.Moved
            }
        }
    }
}

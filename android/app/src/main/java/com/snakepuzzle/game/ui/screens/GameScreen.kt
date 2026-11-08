package com.snakepuzzle.game.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import com.snakepuzzle.game.audio.SoundManager
import com.snakepuzzle.game.engine.LevelRepository
import com.snakepuzzle.game.engine.SnakePuzzleEngine
import com.snakepuzzle.game.model.Direction
import com.snakepuzzle.game.model.GameStatus
import com.snakepuzzle.game.ui.components.*
import com.snakepuzzle.game.ui.theme.*
import kotlin.math.abs

@Composable
fun GameScreen() {
    val context = LocalContext.current
    val repository = remember { LevelRepository.getInstance(context) }
    val soundManager = remember { SoundManager.getInstance(context) }

    var currentLevelIndex by remember { mutableIntStateOf(0) }
    var currentRunScore by remember { mutableIntStateOf(0) }
    var isNewHighScore by remember { mutableStateOf(false) }

    val currentLevel = repository.levels.getOrNull(currentLevelIndex) ?: LevelRepository.fallback50Levels[0]
    val engine = remember(currentLevel, currentRunScore) {
        SnakePuzzleEngine(
            level = currentLevel,
            initialScore = currentRunScore,
            highScore = repository.highScore
        )
    }

    var showLevelSelect by remember { mutableStateOf(false) }
    var showSkinSelect by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }

    fun handleMove(direction: Direction) {
        val result = engine.move(direction)
        when (result) {
            is SnakePuzzleEngine.MoveResult.LevelWon -> {
                soundManager.playWin()
                isNewHighScore = repository.recordScore(result.totalScore)
                repository.recordWin(
                    levelId = currentLevel.id,
                    moves = engine.currentState.movesCount,
                    targetApples = currentLevel.applePositions.size,
                    optimalMoves = currentLevel.optimalMoves
                )
            }
            is SnakePuzzleEngine.MoveResult.LevelLost -> {
                soundManager.playSpike()
                repository.recordScore(result.finalScore)
            }
            is SnakePuzzleEngine.MoveResult.AppleEaten -> {
                soundManager.playEat()
                if (result.portalJustOpened) {
                    soundManager.playPortalUnlocked()
                }
            }
            is SnakePuzzleEngine.MoveResult.PortalLockedNotice -> {
                soundManager.playLockedNotice()
            }
            is SnakePuzzleEngine.MoveResult.Moved -> {
                soundManager.playMove()
            }
            is SnakePuzzleEngine.MoveResult.Blocked -> {
                // Wall bump feedback
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LandscapeGameHUD(
            level = currentLevel,
            state = engine.currentState,
            canUndo = engine.canUndo,
            isDpadOnLeft = repository.isDpadOnLeft,
            onDirection = { dir -> handleMove(dir) },
            onUndo = {
                soundManager.playClick()
                engine.undo()
            },
            onRestart = {
                soundManager.playClick()
                engine.reset()
            },
            onOpenLevelSelect = {
                soundManager.playClick()
                showLevelSelect = true
            },
            onOpenSkinSelect = {
                soundManager.playClick()
                showSkinSelect = true
            },
            onOpenSettings = {
                soundManager.playClick()
                showSettings = true
            }
        ) {
            // Swipe gesture detector over the board
            var totalDragX = 0f
            var totalDragY = 0f
            var dragTriggered = false

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(engine) {
                        detectDragGestures(
                            onDragStart = {
                                totalDragX = 0f
                                totalDragY = 0f
                                dragTriggered = false
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                totalDragX += dragAmount.x
                                totalDragY += dragAmount.y

                                if (!dragTriggered) {
                                    val threshold = 32f
                                    if (abs(totalDragX) > threshold || abs(totalDragY) > threshold) {
                                        dragTriggered = true
                                        if (abs(totalDragX) > abs(totalDragY)) {
                                            if (totalDragX > 0) handleMove(Direction.RIGHT)
                                            else handleMove(Direction.LEFT)
                                        } else {
                                            if (totalDragY > 0) handleMove(Direction.DOWN)
                                            else handleMove(Direction.UP)
                                        }
                                    }
                                }
                            }
                        )
                    }
            ) {
                GameCanvas(
                    level = currentLevel,
                    state = engine.currentState,
                    skin = repository.selectedSkin
                )
            }
        }

        // Modals & Dialogs
        when (val status = engine.currentState.status) {
            is GameStatus.Won -> {
                val moves = engine.currentState.movesCount
                val stars = when {
                    moves <= currentLevel.optimalMoves + 2 -> 3
                    moves <= currentLevel.optimalMoves + 6 -> 2
                    else -> 1
                }
                VictoryDialog(
                    levelId = currentLevel.id,
                    levelName = currentLevel.name,
                    world = currentLevel.world,
                    moves = moves,
                    optimalMoves = currentLevel.optimalMoves,
                    stars = stars,
                    levelScore = engine.currentState.levelScore,
                    totalScore = engine.currentState.score,
                    highScore = repository.highScore,
                    isNewHighScore = isNewHighScore,
                    hasNextLevel = currentLevelIndex + 1 < repository.levels.size,
                    onNextLevel = {
                        currentRunScore = engine.currentState.score
                        if (currentLevelIndex + 1 < repository.levels.size) {
                            currentLevelIndex++
                        }
                    },
                    onReplay = {
                        engine.reset()
                    },
                    onSelectLevel = {
                        showLevelSelect = true
                    }
                )
            }

            is GameStatus.Lost -> {
                DefeatDialog(
                    reason = status.reason,
                    score = engine.currentState.score,
                    highScore = repository.highScore,
                    canUndo = engine.canUndo,
                    onUndo = {
                        engine.undo()
                    },
                    onRestartFromBeginning = {
                        // "chết thì chơi lại từ đầu" -> Reset to Level 1 and 0 score!
                        currentRunScore = 0
                        currentLevelIndex = 0
                        engine.reset()
                    },
                    onSelectLevel = {
                        showLevelSelect = true
                    }
                )
            }

            is GameStatus.Playing -> {}
        }

        if (showLevelSelect) {
            LevelSelectScreen(
                repository = repository,
                onSelectLevel = { selectedLevel ->
                    val idx = repository.levels.indexOfFirst { it.id == selectedLevel.id }
                    if (idx != -1) {
                        currentLevelIndex = idx
                    }
                    showLevelSelect = false
                },
                onClose = { showLevelSelect = false }
            )
        }

        if (showSkinSelect) {
            SkinSelectorDialog(
                repository = repository,
                onClose = { showSkinSelect = false }
            )
        }

        if (showSettings) {
            SettingsDialog(
                soundManager = soundManager,
                repository = repository,
                onClose = { showSettings = false }
            )
        }
    }
}

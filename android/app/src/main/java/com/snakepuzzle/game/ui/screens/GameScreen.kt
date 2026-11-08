package com.snakepuzzle.game.ui.screens

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
import kotlinx.coroutines.delay
import kotlin.math.abs

@Composable
fun GameScreen() {
    val context = LocalContext.current
    val repository = remember { LevelRepository.getInstance(context) }
    val soundManager = remember { SoundManager.getInstance(context) }

    var currentLevelIndex by remember { mutableIntStateOf(0) }
    var currentRunScore by remember { mutableIntStateOf(0) }
    var currentLives by remember { mutableIntStateOf(3) }
    var isNewHighScore by remember { mutableStateOf(false) }

    fun createEngineFor(index: Int, score: Int, lives: Int): SnakePuzzleEngine {
        val safeIndex = index.coerceIn(0, (repository.levels.size - 1).coerceAtLeast(0))
        val lvl = repository.levels.getOrNull(safeIndex) ?: LevelRepository.fallback50Levels[0]
        return SnakePuzzleEngine(
            level = lvl,
            initialScore = score,
            highScore = repository.highScore,
            initialLives = lives
        )
    }

    var engine by remember {
        mutableStateOf(createEngineFor(currentLevelIndex, currentRunScore, currentLives))
    }

    val currentLevel = engine.level

    fun loadLevel(index: Int, keepScore: Int = currentRunScore, keepLives: Int = currentLives) {
        val safeIndex = index.coerceIn(0, repository.levels.size - 1)
        currentLevelIndex = safeIndex
        currentRunScore = keepScore
        currentLives = keepLives
        engine = createEngineFor(safeIndex, keepScore, keepLives)
    }

    // Ticking Stopwatch Timer for current level
    LaunchedEffect(engine) {
        while (true) {
            delay(1000L)
            engine.tickTimer()
        }
    }

    var showLevelSelect by remember { mutableStateOf(false) }
    var showSkinSelect by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var showScoreboard by remember { mutableStateOf(false) }

    fun handleMove(direction: Direction) {
        val result = engine.move(direction)
        when (result) {
            is SnakePuzzleEngine.MoveResult.LevelWon -> {
                soundManager.playWin()
                currentRunScore = result.totalScore
                isNewHighScore = repository.recordScore(result.totalScore)
                repository.recordWin(
                    levelId = currentLevel.id,
                    moves = engine.currentState.movesCount,
                    targetApples = currentLevel.applePositions.size,
                    optimalMoves = currentLevel.optimalMoves
                )
            }
            is SnakePuzzleEngine.MoveResult.LifeLost -> {
                soundManager.playSpike()
                currentLives = result.remainingLives
            }
            is SnakePuzzleEngine.MoveResult.GameOver -> {
                soundManager.playSpike()
                currentLives = 0
                repository.addScoreToBoard(result.finalScore, currentLevel.id)
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
                engine.reset(keepLives = currentLives)
            },
            onPrevLevel = {
                soundManager.playClick()
                if (currentLevelIndex > 0) {
                    loadLevel(currentLevelIndex - 1)
                }
            },
            onNextLevel = {
                soundManager.playClick()
                if (currentLevelIndex + 1 < repository.levels.size) {
                    loadLevel(currentLevelIndex + 1)
                }
            },
            onOpenScoreboard = {
                soundManager.playClick()
                showScoreboard = true
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
                    elapsedSeconds = engine.currentState.elapsedSeconds,
                    timeBonus = engine.currentState.timeBonus,
                    isNewHighScore = isNewHighScore,
                    hasNextLevel = currentLevelIndex + 1 < repository.levels.size,
                    onNextLevel = {
                        val nextIdx = if (currentLevelIndex + 1 < repository.levels.size) currentLevelIndex + 1 else 0
                        loadLevel(nextIdx, keepScore = engine.currentState.score, keepLives = currentLives)
                    },
                    onReplay = {
                        engine.reset(keepLives = currentLives)
                    },
                    onSelectLevel = {
                        showLevelSelect = true
                    }
                )
            }

            is GameStatus.LifeLost -> {
                LifeLostDialog(
                    reason = status.reason,
                    remainingLives = status.remainingLives,
                    canUndo = engine.canUndo,
                    onUndo = {
                        engine.undo()
                    },
                    onRetryLevel = {
                        loadLevel(currentLevelIndex, keepScore = currentRunScore, keepLives = status.remainingLives)
                    }
                )
            }

            is GameStatus.GameOver -> {
                DefeatDialog(
                    reason = status.reason,
                    score = status.finalScore,
                    highScore = repository.highScore,
                    levelReached = currentLevel.id,
                    onRestartFromBeginning = {
                        // "khi hết 3 tim sẽ thua lưu lại điểm vào bảng điểm và trở về lv1"
                        loadLevel(0, keepScore = 0, keepLives = 3)
                    },
                    onViewScoreboard = {
                        showScoreboard = true
                    }
                )
            }

            is GameStatus.Playing -> {}
        }

        if (showScoreboard) {
            ScoreboardDialog(
                scoreboard = repository.scoreboard,
                onClose = { showScoreboard = false }
            )
        }

        if (showLevelSelect) {
            LevelSelectScreen(
                repository = repository,
                onSelectLevel = { selectedLevel ->
                    val idx = repository.levels.indexOfFirst { it.id == selectedLevel.id }
                    if (idx != -1) {
                        loadLevel(idx)
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

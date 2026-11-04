package com.snakepuzzle.game.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val currentLevel = repository.levels.getOrNull(currentLevelIndex) ?: LevelRepository.fallbackLevels[0]
    val engine = remember(currentLevel) { SnakePuzzleEngine(currentLevel) }

    var showLevelSelect by remember { mutableStateOf(false) }

    fun handleMove(direction: Direction) {
        val prevApplesCount = engine.currentState.remainingApples.size
        val prevHead = engine.currentState.head

        val moved = engine.move(direction)
        if (moved) {
            when (val status = engine.currentState.status) {
                is GameStatus.Won -> {
                    soundManager.playWin()
                    repository.recordWin(
                        levelId = currentLevel.id,
                        moves = engine.currentState.movesCount,
                        targetApples = currentLevel.applePositions.size
                    )
                }
                is GameStatus.Lost -> {
                    soundManager.playSpike()
                }
                is GameStatus.Playing -> {
                    if (engine.currentState.remainingApples.size < prevApplesCount) {
                        soundManager.playEat()
                    } else if (engine.currentState.head.y > prevHead.y + 1) {
                        soundManager.playFall()
                    } else {
                        soundManager.playMove()
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BgTop, BgBottom)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Control Bar
            TopBar(
                movesCount = engine.currentState.movesCount,
                canUndo = engine.canUndo,
                isSoundEnabled = soundManager.isSoundEnabled,
                onOpenLevelSelect = {
                    soundManager.playClick()
                    showLevelSelect = true
                },
                onUndo = {
                    soundManager.playClick()
                    engine.undo()
                },
                onRestart = {
                    soundManager.playClick()
                    engine.reset()
                },
                onToggleSound = {
                    soundManager.isSoundEnabled = !soundManager.isSoundEnabled
                }
            )

            // Level Info Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Level ${currentLevel.id}: ${currentLevel.name}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )

                val remaining = engine.currentState.remainingApples.size
                val total = currentLevel.applePositions.size
                val portalOpen = remaining == 0

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (portalOpen) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (portalOpen) SnakeHead else AppleRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (portalOpen) "Portal Open! Reach the Vortex!" else "Apples Left: $remaining / $total",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (portalOpen) SnakeHead else TextSecondary
                    )
                }
            }

            // Game Board with swipe gestures
            var totalDragX = 0f
            var totalDragY = 0f
            var dragHandled = false

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .pointerInput(engine) {
                        detectDragGestures(
                            onDragStart = {
                                totalDragX = 0f
                                totalDragY = 0f
                                dragHandled = false
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                totalDragX += dragAmount.x
                                totalDragY += dragAmount.y

                                if (!dragHandled) {
                                    val threshold = 35f
                                    if (abs(totalDragX) > threshold || abs(totalDragY) > threshold) {
                                        dragHandled = true
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
                    },
                contentAlignment = Alignment.Center
            ) {
                GameCanvas(
                    level = currentLevel,
                    state = engine.currentState,
                    portalOpen = engine.currentState.remainingApples.isEmpty()
                )
            }

            // Virtual D-Pad
            VirtualDPad(
                onDirection = { dir -> handleMove(dir) },
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }

        // Modals / Dialogs
        when (val status = engine.currentState.status) {
            is GameStatus.Won -> {
                val stars = when {
                    engine.currentState.movesCount <= currentLevel.applePositions.size + 10 -> 3
                    engine.currentState.movesCount <= currentLevel.applePositions.size + 18 -> 2
                    else -> 1
                }
                VictoryDialog(
                    levelName = currentLevel.name,
                    moves = engine.currentState.movesCount,
                    stars = stars,
                    hasNextLevel = currentLevelIndex + 1 < repository.levels.size,
                    onNextLevel = {
                        currentLevelIndex++
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
                    canUndo = engine.canUndo,
                    onUndo = {
                        engine.undo()
                    },
                    onRestart = {
                        engine.reset()
                    },
                    onSelectLevel = {
                        showLevelSelect = true
                    }
                )
            }
            is GameStatus.Playing -> {}
        }

        // Level Select Screen modal
        if (showLevelSelect) {
            LevelSelectScreen(
                repository = repository,
                onSelectLevel = { selected ->
                    val idx = repository.levels.indexOfFirst { it.id == selected.id }
                    if (idx != -1) {
                        currentLevelIndex = idx
                    }
                    showLevelSelect = false
                },
                onClose = { showLevelSelect = false }
            )
        }
    }
}

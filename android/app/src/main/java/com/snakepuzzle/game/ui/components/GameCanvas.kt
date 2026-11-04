package com.snakepuzzle.game.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.snakepuzzle.game.model.Direction
import com.snakepuzzle.game.model.GameState
import com.snakepuzzle.game.model.Level
import com.snakepuzzle.game.model.Position
import com.snakepuzzle.game.ui.theme.*
import kotlin.math.min

@Composable
fun GameCanvas(
    level: Level,
    state: GameState,
    portalOpen: Boolean,
    modifier: Modifier = Modifier
) {
    // Portal rotation animation
    val infiniteTransition = rememberInfiniteTransition(label = "portalRotation")
    val portalAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Apple pulse animation
    val appleScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "applePulse"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val availableWidth = size.width
        val availableHeight = size.height

        val cellWidth = availableWidth / level.width
        val cellHeight = availableHeight / level.height
        val cellSize = min(cellWidth, cellHeight)

        val boardWidth = cellSize * level.width
        val boardHeight = cellSize * level.height
        val offsetX = (availableWidth - boardWidth) / 2f
        val offsetY = (availableHeight - boardHeight) / 2f

        // Board background
        drawRoundRect(
            color = GridBg,
            topLeft = Offset(offsetX, offsetY),
            size = Size(boardWidth, boardHeight),
            cornerRadius = CornerRadius(24f, 24f)
        )

        // Board border
        drawRoundRect(
            color = Color.White.copy(alpha = 0.4f),
            topLeft = Offset(offsetX, offsetY),
            size = Size(boardWidth, boardHeight),
            cornerRadius = CornerRadius(24f, 24f),
            style = Stroke(width = 3f)
        )

        // Grid lines
        for (x in 0..level.width) {
            val px = offsetX + x * cellSize
            drawLine(
                color = GridLine,
                start = Offset(px, offsetY),
                end = Offset(px, offsetY + boardHeight),
                strokeWidth = 1f
            )
        }
        for (y in 0..level.height) {
            val py = offsetY + y * cellSize
            drawLine(
                color = GridLine,
                start = Offset(offsetX, py),
                end = Offset(offsetX + boardWidth, py),
                strokeWidth = 1f
            )
        }

        // 1. Draw Solid Ground & Walls
        for (pos in level.solidPositions) {
            val cellLeft = offsetX + pos.x * cellSize
            val cellTop = offsetY + pos.y * cellSize
            val isTopSurface = !level.solidPositions.contains(Position(pos.x, pos.y - 1))

            // Dirt block
            drawRoundRect(
                color = SolidDirt,
                topLeft = Offset(cellLeft + 1f, cellTop + 1f),
                size = Size(cellSize - 2f, cellSize - 2f),
                cornerRadius = CornerRadius(6f, 6f)
            )

            // Top grass rim if exposed to air
            if (isTopSurface) {
                drawRoundRect(
                    color = SolidGrassTop,
                    topLeft = Offset(cellLeft + 1f, cellTop + 1f),
                    size = Size(cellSize - 2f, cellSize * 0.28f),
                    cornerRadius = CornerRadius(6f, 6f)
                )
            }
        }

        // 2. Draw Spikes
        for (pos in level.spikePositions) {
            drawSpike(offsetX + pos.x * cellSize, offsetY + pos.y * cellSize, cellSize)
        }

        // 3. Draw Portal
        level.portalPosition?.let { portal ->
            drawPortal(
                offsetX + portal.x * cellSize + cellSize / 2f,
                offsetY + portal.y * cellSize + cellSize / 2f,
                cellSize,
                portalOpen,
                portalAngle
            )
        }

        // 4. Draw Apples
        for (apple in state.remainingApples) {
            drawApple(
                offsetX + apple.x * cellSize + cellSize / 2f,
                offsetY + apple.y * cellSize + cellSize / 2f,
                cellSize,
                appleScale
            )
        }

        // 5. Draw Snake
        drawSnake(state.snake, state.facingDirection, offsetX, offsetY, cellSize)
    }
}

private fun DrawScope.drawSpike(x: Float, y: Float, cellSize: Float) {
    val w = cellSize * 0.82f
    val h = cellSize * 0.72f
    val cx = x + cellSize / 2f
    val bottomY = y + cellSize - 2f
    val topY = bottomY - h

    val path = Path().apply {
        moveTo(cx - w / 2f, bottomY)
        lineTo(cx, topY)
        lineTo(cx + w / 2f, bottomY)
        close()
    }
    drawPath(
        path = path,
        brush = Brush.verticalGradient(
            colors = listOf(SpikeTip, SpikeBase),
            startY = topY,
            endY = bottomY
        )
    )
}

private fun DrawScope.drawPortal(cx: Float, cy: Float, cellSize: Float, isOpen: Boolean, angle: Float) {
    val radius = cellSize * 0.42f

    // Outer glow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                if (isOpen) PortalGlow.copy(alpha = 0.8f) else Color.Gray.copy(alpha = 0.3f),
                Color.Transparent
            ),
            center = Offset(cx, cy),
            radius = radius * 1.5f
        ),
        radius = radius * 1.5f,
        center = Offset(cx, cy)
    )

    // Swirling rings
    rotate(angle, pivot = Offset(cx, cy)) {
        drawCircle(
            brush = Brush.sweepGradient(
                colors = listOf(PortalOuter, PortalGlow, PortalInner, PortalOuter),
                center = Offset(cx, cy)
            ),
            radius = radius,
            center = Offset(cx, cy),
            style = Stroke(width = cellSize * 0.16f)
        )
    }

    // Inner black hole
    drawCircle(
        color = PortalInner,
        radius = radius * 0.65f,
        center = Offset(cx, cy)
    )
}

private fun DrawScope.drawApple(cx: Float, cy: Float, cellSize: Float, scale: Float) {
    val r = cellSize * 0.32f * scale

    // Stem
    drawRoundRect(
        color = AppleStem,
        topLeft = Offset(cx - cellSize * 0.04f, cy - r - cellSize * 0.14f),
        size = Size(cellSize * 0.08f, cellSize * 0.18f),
        cornerRadius = CornerRadius(2f, 2f)
    )

    // Leaf
    val leafPath = Path().apply {
        moveTo(cx, cy - r - cellSize * 0.04f)
        quadraticTo(cx + cellSize * 0.16f, cy - r - cellSize * 0.14f, cx + cellSize * 0.2f, cy - r - cellSize * 0.06f)
        quadraticTo(cx + cellSize * 0.10f, cy - r - cellSize * 0.02f, cx, cy - r - cellSize * 0.04f)
        close()
    }
    drawPath(path = leafPath, color = AppleLeaf)

    // Fruit body
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFF7373), AppleRed),
            center = Offset(cx - r * 0.3f, cy - r * 0.3f),
            radius = r * 1.2f
        ),
        radius = r,
        center = Offset(cx, cy)
    )
}

private fun DrawScope.drawSnake(
    snake: List<Position>,
    facing: Direction,
    offsetX: Float,
    offsetY: Float,
    cellSize: Float
) {
    for (i in snake.indices.reversed()) {
        val pos = snake[i]
        val isHead = (i == 0)
        val isTail = (i == snake.size - 1)
        val segSize = cellSize * if (isHead) 0.88f else if (isTail) 0.72f else 0.82f

        val cx = offsetX + pos.x * cellSize + cellSize / 2f
        val cy = offsetY + pos.y * cellSize + cellSize / 2f

        val segColor = when {
            isHead -> SnakeHead
            isTail -> SnakeTail
            else -> SnakeBody
        }

        // Draw body segment
        drawRoundRect(
            color = segColor,
            topLeft = Offset(cx - segSize / 2f, cy - segSize / 2f),
            size = Size(segSize, segSize),
            cornerRadius = CornerRadius(segSize * 0.42f, segSize * 0.42f)
        )

        if (isHead) {
            // Draw eyes & face
            val eyeSize = segSize * 0.28f
            val pupilSize = eyeSize * 0.52f
            val eyeSpacing = segSize * 0.22f
            val eyeVertical = -segSize * 0.08f

            val (pupilDx, pupilDy) = when (facing) {
                Direction.UP -> Pair(0f, -eyeSize * 0.22f)
                Direction.DOWN -> Pair(0f, eyeSize * 0.22f)
                Direction.LEFT -> Pair(-eyeSize * 0.22f, 0f)
                Direction.RIGHT -> Pair(eyeSize * 0.22f, 0f)
            }

            // Left Eye
            val leftEyeCenter = Offset(cx - eyeSpacing, cy + eyeVertical)
            drawCircle(color = SnakeEye, radius = eyeSize / 2f, center = leftEyeCenter)
            drawCircle(color = SnakePupil, radius = pupilSize / 2f, center = leftEyeCenter + Offset(pupilDx, pupilDy))

            // Right Eye
            val rightEyeCenter = Offset(cx + eyeSpacing, cy + eyeVertical)
            drawCircle(color = SnakeEye, radius = eyeSize / 2f, center = rightEyeCenter)
            drawCircle(color = SnakePupil, radius = pupilSize / 2f, center = rightEyeCenter + Offset(pupilDx, pupilDy))

            // Cheeks
            val cheekRadius = eyeSize * 0.32f
            drawCircle(color = SnakeCheek, radius = cheekRadius, center = Offset(cx - eyeSpacing * 1.35f, cy + eyeVertical + eyeSize * 0.75f))
            drawCircle(color = SnakeCheek, radius = cheekRadius, center = Offset(cx + eyeSpacing * 1.35f, cy + eyeVertical + eyeSize * 0.75f))
        } else {
            // Body accent stripe
            drawCircle(
                color = SnakeStripe.copy(alpha = 0.45f),
                radius = segSize * 0.18f,
                center = Offset(cx, cy)
            )
        }
    }
}

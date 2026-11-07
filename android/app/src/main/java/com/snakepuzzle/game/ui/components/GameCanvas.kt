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
import com.snakepuzzle.game.model.*
import com.snakepuzzle.game.ui.theme.*
import kotlin.math.min
import kotlin.math.sin

@Composable
fun GameCanvas(
    level: Level,
    state: GameState,
    skin: SnakeSkin,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "canvasAnimations")

    // Portal continuous swirl rotation
    val portalAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "portalRotation"
    )

    // Apple juicy bounce pulse
    val applePulse by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "applePulse"
    )

    // Snake tongue flick animation
    val tongueFlick by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tongueFlick"
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

        // Board outer frame & background
        drawRoundRect(
            color = Color.Black.copy(alpha = 0.18f),
            topLeft = Offset(offsetX - 4f, offsetY - 4f),
            size = Size(boardWidth + 8f, boardHeight + 8f),
            cornerRadius = CornerRadius(18f, 18f)
        )

        drawRoundRect(
            color = Color(0x33000000),
            topLeft = Offset(offsetX, offsetY),
            size = Size(boardWidth, boardHeight),
            cornerRadius = CornerRadius(14f, 14f)
        )

        // Subtle Grid Lines
        for (x in 0..level.width) {
            val px = offsetX + x * cellSize
            drawLine(
                color = Color.White.copy(alpha = 0.12f),
                start = Offset(px, offsetY),
                end = Offset(px, offsetY + boardHeight),
                strokeWidth = 1f
            )
        }
        for (y in 0..level.height) {
            val py = offsetY + y * cellSize
            drawLine(
                color = Color.White.copy(alpha = 0.12f),
                start = Offset(offsetX, py),
                end = Offset(offsetX + boardWidth, py),
                strokeWidth = 1f
            )
        }

        // 1. Draw Solid Terrain Blocks based on World theme
        val (baseDirt, topTrim) = when (level.world) {
            "Crystal Caverns" -> Pair(CavernStone, CavernCrystal)
            "Sunken Ruins" -> Pair(RuinsStone, RuinsMoss)
            "Sky Citadel" -> Pair(SkyStone, SkyGold)
            "Magma Core" -> Pair(MagmaObsidian, MagmaLava)
            else -> Pair(MeadowDirt, MeadowGrass)
        }

        for (pos in level.solidPositions) {
            val cellLeft = offsetX + pos.x * cellSize
            val cellTop = offsetY + pos.y * cellSize
            val isTopSurface = !level.solidPositions.contains(Position(pos.x, pos.y - 1))

            // Main block body
            drawRoundRect(
                color = baseDirt,
                topLeft = Offset(cellLeft + 1f, cellTop + 1f),
                size = Size(cellSize - 2f, cellSize - 2f),
                cornerRadius = CornerRadius(6f, 6f)
            )

            // 3D block top highlight / trim
            if (isTopSurface) {
                drawRoundRect(
                    color = topTrim,
                    topLeft = Offset(cellLeft + 1f, cellTop + 1f),
                    size = Size(cellSize - 2f, cellSize * 0.28f),
                    cornerRadius = CornerRadius(5f, 5f)
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
                state.isPortalUnlocked,
                portalAngle
            )
        }

        // 4. Draw Apples
        for (apple in state.remainingApples) {
            drawApple(
                offsetX + apple.x * cellSize + cellSize / 2f,
                offsetY + apple.y * cellSize + cellSize / 2f,
                cellSize,
                applePulse
            )
        }

        // 5. Draw 2.5D Animated Cute Snake
        drawSnake(
            snake = state.snake,
            facing = state.facingDirection,
            skin = skin,
            offsetX = offsetX,
            offsetY = offsetY,
            cellSize = cellSize,
            tongueFlick = tongueFlick
        )
    }
}

private fun DrawScope.drawSpike(x: Float, y: Float, cellSize: Float) {
    val w = cellSize * 0.82f
    val h = cellSize * 0.75f
    val cx = x + cellSize / 2f
    val bottomY = y + cellSize - 1f
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
            colors = listOf(SpikePoint, SpikeBody),
            startY = topY,
            endY = bottomY
        )
    )
}

private fun DrawScope.drawPortal(cx: Float, cy: Float, cellSize: Float, isUnlocked: Boolean, angle: Float) {
    val radius = cellSize * 0.44f

    // Outer vortex aura
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                if (isUnlocked) PortalVortexGlow.copy(alpha = 0.85f) else Color.Gray.copy(alpha = 0.35f),
                Color.Transparent
            ),
            center = Offset(cx, cy),
            radius = radius * 1.6f
        ),
        radius = radius * 1.6f,
        center = Offset(cx, cy)
    )

    // Swirling rings
    rotate(if (isUnlocked) angle else 0f, pivot = Offset(cx, cy)) {
        drawCircle(
            brush = Brush.sweepGradient(
                colors = if (isUnlocked) {
                    listOf(PortalVortexOuter, PortalVortexGlow, PortalVortexInner, PortalVortexOuter)
                } else {
                    listOf(Color.DarkGray, Color.Gray, Color.Black, Color.DarkGray)
                },
                center = Offset(cx, cy)
            ),
            radius = radius,
            center = Offset(cx, cy),
            style = Stroke(width = cellSize * 0.16f)
        )
    }

    // Inner vortex hole
    drawCircle(
        color = PortalVortexInner,
        radius = radius * 0.65f,
        center = Offset(cx, cy)
    )

    // If locked, draw lock indicator
    if (!isUnlocked) {
        val lockW = cellSize * 0.22f
        val lockH = cellSize * 0.26f
        drawRoundRect(
            color = Color.White.copy(alpha = 0.9f),
            topLeft = Offset(cx - lockW / 2f, cy - lockH * 0.2f),
            size = Size(lockW, lockH * 0.7f),
            cornerRadius = CornerRadius(3f, 3f)
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.9f),
            radius = lockW * 0.35f,
            center = Offset(cx, cy - lockH * 0.2f),
            style = Stroke(width = 3f)
        )
    }
}

private fun DrawScope.drawApple(cx: Float, cy: Float, cellSize: Float, pulse: Float) {
    val r = cellSize * 0.33f * pulse

    // Wooden Stem
    drawRoundRect(
        color = AppleStem,
        topLeft = Offset(cx - cellSize * 0.035f, cy - r - cellSize * 0.15f),
        size = Size(cellSize * 0.07f, cellSize * 0.18f),
        cornerRadius = CornerRadius(2f, 2f)
    )

    // Green Leaf
    val leafPath = Path().apply {
        moveTo(cx, cy - r - cellSize * 0.05f)
        quadraticBezierTo(cx + cellSize * 0.16f, cy - r - cellSize * 0.16f, cx + cellSize * 0.22f, cy - r - cellSize * 0.07f)
        quadraticBezierTo(cx + cellSize * 0.12f, cy - r - cellSize * 0.02f, cx, cy - r - cellSize * 0.05f)
        close()
    }
    drawPath(path = leafPath, color = AppleLeaf)

    // Fruit Body with rich radial shading
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(AppleGleam, AppleRed, Color(0xFF9E0D0B)),
            center = Offset(cx - r * 0.35f, cy - r * 0.35f),
            radius = r * 1.3f
        ),
        radius = r,
        center = Offset(cx, cy)
    )

    // Specular shine dot
    drawCircle(
        color = Color.White.copy(alpha = 0.6f),
        radius = r * 0.22f,
        center = Offset(cx - r * 0.38f, cy - r * 0.38f)
    )
}

private fun DrawScope.drawSnake(
    snake: List<Position>,
    facing: Direction,
    skin: SnakeSkin,
    offsetX: Float,
    offsetY: Float,
    cellSize: Float,
    tongueFlick: Float
) {
    // Draw body segments from tail to head
    for (i in snake.indices.reversed()) {
        val pos = snake[i]
        val isHead = (i == 0)
        val isTail = (i == snake.size - 1)
        val segSize = cellSize * if (isHead) 0.90f else if (isTail) 0.70f else 0.82f

        val cx = offsetX + pos.x * cellSize + cellSize / 2f
        val cy = offsetY + pos.y * cellSize + cellSize / 2f

        val segColor = when {
            isHead -> skin.headColor
            isTail -> skin.tailColor
            else -> skin.bodyColor
        }

        // Draw shadow under segment
        drawCircle(
            color = Color.Black.copy(alpha = 0.22f),
            radius = segSize / 2f,
            center = Offset(cx, cy + 2f)
        )

        // Draw rounded 2.5D segment
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(skin.accentColor, segColor, segColor.copy(alpha = 0.85f)),
                center = Offset(cx - segSize * 0.2f, cy - segSize * 0.2f),
                radius = segSize * 0.65f
            ),
            radius = segSize / 2f,
            center = Offset(cx, cy)
        )

        if (isHead) {
            // Little flicking tongue
            if (tongueFlick > 0.4f) {
                val tongueLen = cellSize * 0.26f * tongueFlick
                val (tDx, tDy) = when (facing) {
                    Direction.UP -> Pair(0f, -segSize / 2f - tongueLen)
                    Direction.DOWN -> Pair(0f, segSize / 2f + tongueLen)
                    Direction.LEFT -> Pair(-segSize / 2f - tongueLen, 0f)
                    Direction.RIGHT -> Pair(segSize / 2f + tongueLen, 0f)
                }
                drawLine(
                    color = Color(0xFFFF2A4D),
                    start = Offset(cx, cy),
                    end = Offset(cx + tDx, cy + tDy),
                    strokeWidth = 3f
                )
            }

            // Big expressive cartoon eyes
            val eyeSize = segSize * 0.32f
            val pupilSize = eyeSize * 0.52f
            val eyeSpacing = segSize * 0.24f
            val eyeVertical = -segSize * 0.08f

            val (pupilDx, pupilDy) = when (facing) {
                Direction.UP -> Pair(0f, -eyeSize * 0.22f)
                Direction.DOWN -> Pair(0f, eyeSize * 0.22f)
                Direction.LEFT -> Pair(-eyeSize * 0.22f, 0f)
                Direction.RIGHT -> Pair(eyeSize * 0.22f, 0f)
            }

            // Left Eye
            val leftEyeCenter = Offset(cx - eyeSpacing, cy + eyeVertical)
            drawCircle(color = skin.eyeColor, radius = eyeSize / 2f, center = leftEyeCenter)
            drawCircle(color = skin.pupilColor, radius = pupilSize / 2f, center = leftEyeCenter + Offset(pupilDx, pupilDy))
            drawCircle(color = Color.White, radius = pupilSize * 0.35f, center = leftEyeCenter + Offset(pupilDx - 1f, pupilDy - 1f))

            // Right Eye
            val rightEyeCenter = Offset(cx + eyeSpacing, cy + eyeVertical)
            drawCircle(color = skin.eyeColor, radius = eyeSize / 2f, center = rightEyeCenter)
            drawCircle(color = skin.pupilColor, radius = pupilSize / 2f, center = rightEyeCenter + Offset(pupilDx, pupilDy))
            drawCircle(color = Color.White, radius = pupilSize * 0.35f, center = rightEyeCenter + Offset(pupilDx - 1f, pupilDy - 1f))

            // Cheerful blush cheeks
            val cheekR = eyeSize * 0.32f
            drawCircle(color = skin.cheekColor, radius = cheekR, center = Offset(cx - eyeSpacing * 1.35f, cy + eyeVertical + eyeSize * 0.72f))
            drawCircle(color = skin.cheekColor, radius = cheekR, center = Offset(cx + eyeSpacing * 1.35f, cy + eyeVertical + eyeSize * 0.72f))
        } else {
            // Dorsal scale accent
            drawCircle(
                color = skin.accentColor.copy(alpha = 0.55f),
                radius = segSize * 0.18f,
                center = Offset(cx, cy)
            )
        }
    }
}

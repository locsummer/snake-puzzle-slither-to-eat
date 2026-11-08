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
import kotlin.math.*

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
            animation = tween(durationMillis = 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "portalRotation"
    )

    // Apple juicy bounce pulse
    val applePulse by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "applePulse"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val availableWidth = size.width
        val availableHeight = size.height

        // 1. Draw Authentic Sunburst Background Sky (Exact Match to Screenshot)
        drawSunburstSky(availableWidth, availableHeight)

        // 2. Compute board dimensions and offset
        val cellWidth = availableWidth / level.width
        val cellHeight = availableHeight / level.height
        val cellSize = min(cellWidth, cellHeight)

        val boardWidth = cellSize * level.width
        val boardHeight = cellSize * level.height
        val offsetX = (availableWidth - boardWidth) / 2f
        val offsetY = (availableHeight - boardHeight) / 2f

        // 3. Draw Connected Brown Dirt Platforms with Organic Wavy Edges & Bold Outlines
        drawConnectedTerrain(
            solidPositions = level.solidPositions,
            offsetX = offsetX,
            offsetY = offsetY,
            cellSize = cellSize
        )

        // 4. Draw Spikes
        for (pos in level.spikePositions) {
            drawSpike(offsetX + pos.x * cellSize, offsetY + pos.y * cellSize, cellSize)
        }

        // 5. Draw Portal (Black Spiral Vortex as shown in screenshot)
        level.portalPosition?.let { portal ->
            drawBlackSpiralPortal(
                cx = offsetX + portal.x * cellSize + cellSize / 2f,
                cy = offsetY + portal.y * cellSize + cellSize / 2f,
                cellSize = cellSize,
                isUnlocked = state.isPortalUnlocked,
                rotation = portalAngle
            )
        }

        // 6. Draw Apples (Glossy red cartoon apples with stem, leaf & shine)
        for (apple in state.remainingApples) {
            drawCartoonApple(
                cx = offsetX + apple.x * cellSize + cellSize / 2f,
                cy = offsetY + apple.y * cellSize + cellSize / 2f,
                cellSize = cellSize,
                pulse = applePulse
            )
        }

        // 7. Draw The Apple Worm Snake (Googly eyes, purple lips, lime throat, green body)
        drawAppleWorm(
            snake = state.snake,
            facing = state.facingDirection,
            skin = skin,
            isDead = state.isDead,
            offsetX = offsetX,
            offsetY = offsetY,
            cellSize = cellSize
        )
    }
}

/**
 * Draws the comic sunburst sky rays, soft horizon clouds, and rolling cartoon hills.
 */
private fun DrawScope.drawSunburstSky(width: Float, height: Float) {
    // Solid base sky blue
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(WormSkySunburstLight, WormSkySunburstDeep),
            startY = 0f,
            endY = height
        )
    )

    // Sunburst ray fan originating from upper center
    val originX = width / 2f
    val originY = -height * 0.1f
    val rayCount = 20
    val maxDist = hypot(width, height) * 1.5f

    for (i in 0 until rayCount step 2) {
        val angle1 = (i * 2 * PI / rayCount).toFloat()
        val angle2 = ((i + 1) * 2 * PI / rayCount).toFloat()

        val x1 = originX + cos(angle1) * maxDist
        val y1 = originY + sin(angle1) * maxDist
        val x2 = originX + cos(angle2) * maxDist
        val y2 = originY + sin(angle2) * maxDist

        val rayPath = Path().apply {
            moveTo(originX, originY)
            lineTo(x1, y1)
            lineTo(x2, y2)
            close()
        }
        drawPath(path = rayPath, color = Color.White.copy(alpha = 0.14f))
    }

    // Soft clouds along the lower horizon
    val cloudBaseY = height * 0.82f
    drawRoundRect(
        color = WormCloud.copy(alpha = 0.65f),
        topLeft = Offset(-width * 0.1f, cloudBaseY - 25f),
        size = Size(width * 1.2f, 70f),
        cornerRadius = CornerRadius(35f, 35f)
    )
    drawCircle(
        color = WormCloud.copy(alpha = 0.55f),
        radius = 70f,
        center = Offset(width * 0.25f, cloudBaseY - 15f)
    )
    drawCircle(
        color = WormCloud.copy(alpha = 0.55f),
        radius = 85f,
        center = Offset(width * 0.55f, cloudBaseY - 20f)
    )
    drawCircle(
        color = WormCloud.copy(alpha = 0.55f),
        radius = 65f,
        center = Offset(width * 0.82f, cloudBaseY - 10f)
    )

    // Rolling green hills at the bottom
    val hillPathBack = Path().apply {
        moveTo(0f, height)
        lineTo(0f, height * 0.86f)
        cubicTo(width * 0.25f, height * 0.80f, width * 0.45f, height * 0.89f, width * 0.70f, height * 0.84f)
        cubicTo(width * 0.85f, height * 0.81f, width * 0.95f, height * 0.85f, width, height * 0.83f)
        lineTo(width, height)
        close()
    }
    drawPath(path = hillPathBack, color = WormHillBack)

    val hillPathFront = Path().apply {
        moveTo(0f, height)
        lineTo(0f, height * 0.89f)
        cubicTo(width * 0.2f, height * 0.85f, width * 0.4f, height * 0.92f, width * 0.65f, height * 0.88f)
        cubicTo(width * 0.8f, height * 0.85f, width * 0.92f, height * 0.90f, width, height * 0.87f)
        lineTo(width, height)
        close()
    }
    drawPath(path = hillPathFront, color = WormHillFront)
}

/**
 * Draws connected brown dirt platforms with bold outer cartoon borders and textured top edges.
 */
private fun DrawScope.drawConnectedTerrain(
    solidPositions: Set<Position>,
    offsetX: Float,
    offsetY: Float,
    cellSize: Float
) {
    val outlineWidth = 4f

    // 1. Fill all solid cells with warm dirt brown
    for (pos in solidPositions) {
        val cellX = offsetX + pos.x * cellSize
        val cellY = offsetY + pos.y * cellSize

        drawRect(
            color = WormDirt,
            topLeft = Offset(cellX, cellY),
            size = Size(cellSize, cellSize)
        )

        // Top surface organic dirt edge & highlight if exposed
        val hasTopNeighbor = solidPositions.contains(Position(pos.x, pos.y - 1))
        if (!hasTopNeighbor) {
            val trimHeight = cellSize * 0.16f
            // Lighter dirt highlight band
            drawRect(
                color = WormDirtTopEdge,
                topLeft = Offset(cellX, cellY),
                size = Size(cellSize, trimHeight)
            )

            // Wavy textured earth contour line
            val wavyPath = Path().apply {
                moveTo(cellX, cellY + trimHeight)
                quadraticBezierTo(cellX + cellSize * 0.25f, cellY + trimHeight - 2f, cellX + cellSize * 0.5f, cellY + trimHeight)
                quadraticBezierTo(cellX + cellSize * 0.75f, cellY + trimHeight + 2f, cellX + cellSize, cellY + trimHeight)
            }
            drawPath(
                path = wavyPath,
                color = WormDirtWavyLine,
                style = Stroke(width = 2.5f)
            )
        }
    }

    // 2. Draw outer bold cartoon outlines only along exposed edges
    for (pos in solidPositions) {
        val cellX = offsetX + pos.x * cellSize
        val cellY = offsetY + pos.y * cellSize

        val hasTop = solidPositions.contains(Position(pos.x, pos.y - 1))
        val hasBottom = solidPositions.contains(Position(pos.x, pos.y + 1))
        val hasLeft = solidPositions.contains(Position(pos.x - 1, pos.y))
        val hasRight = solidPositions.contains(Position(pos.x + 1, pos.y))

        // Top boundary outline
        if (!hasTop) {
            drawLine(
                color = WormOutline,
                start = Offset(cellX - outlineWidth / 2f, cellY),
                end = Offset(cellX + cellSize + outlineWidth / 2f, cellY),
                strokeWidth = outlineWidth
            )
        }
        // Bottom boundary outline
        if (!hasBottom) {
            drawLine(
                color = WormOutline,
                start = Offset(cellX - outlineWidth / 2f, cellY + cellSize),
                end = Offset(cellX + cellSize + outlineWidth / 2f, cellY + cellSize),
                strokeWidth = outlineWidth
            )
        }
        // Left boundary outline
        if (!hasLeft) {
            drawLine(
                color = WormOutline,
                start = Offset(cellX, cellY - outlineWidth / 2f),
                end = Offset(cellX, cellY + cellSize + outlineWidth / 2f),
                strokeWidth = outlineWidth
            )
        }
        // Right boundary outline
        if (!hasRight) {
            drawLine(
                color = WormOutline,
                start = Offset(cellX + cellSize, cellY - outlineWidth / 2f),
                end = Offset(cellX + cellSize, cellY + cellSize + outlineWidth / 2f),
                strokeWidth = outlineWidth
            )
        }
    }
}

/**
 * Draws the Black Spiral Vortex Exit Portal (Identical to the screenshot's black snail swirl).
 */
private fun DrawScope.drawBlackSpiralPortal(
    cx: Float,
    cy: Float,
    cellSize: Float,
    isUnlocked: Boolean,
    rotation: Float
) {
    val radius = cellSize * 0.44f

    // Outer dark circular border
    drawCircle(
        color = WormOutline,
        radius = radius + 2f,
        center = Offset(cx, cy),
        style = Stroke(width = 3.5f)
    )

    // Base dark vortex background
    drawCircle(
        color = WormPortalBlack,
        radius = radius,
        center = Offset(cx, cy)
    )

    // Spiral Archimedean swirl lines
    val spiralAngle = if (isUnlocked) rotation else 0f
    rotate(spiralAngle, pivot = Offset(cx, cy)) {
        val spiralPath = Path()
        val numPoints = 80
        val maxTurns = 3.2f

        for (i in 0..numPoints) {
            val t = i.toFloat() / numPoints
            val angle = t * 2 * PI.toFloat() * maxTurns
            val r = radius * (1f - t) * 0.95f
            val px = cx + cos(angle) * r
            val py = cy + sin(angle) * r
            if (i == 0) spiralPath.moveTo(px, py) else spiralPath.lineTo(px, py)
        }

        drawPath(
            path = spiralPath,
            color = if (isUnlocked) Color(0xFF6B7280) else Color(0xFF383838),
            style = Stroke(width = cellSize * 0.08f)
        )
    }

    // Inner dark black hole center
    drawCircle(
        color = WormPortalCore,
        radius = radius * 0.28f,
        center = Offset(cx, cy)
    )

    // If locked, draw padlock icon
    if (!isUnlocked) {
        val lockW = cellSize * 0.20f
        val lockH = cellSize * 0.24f
        drawRoundRect(
            color = Color.White.copy(alpha = 0.85f),
            topLeft = Offset(cx - lockW / 2f, cy - lockH * 0.15f),
            size = Size(lockW, lockH * 0.65f),
            cornerRadius = CornerRadius(3f, 3f)
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.85f),
            radius = lockW * 0.32f,
            center = Offset(cx, cy - lockH * 0.18f),
            style = Stroke(width = 2.5f)
        )
    }
}

/**
 * Draws the glossy cartoon red apple with curved shine, brown stem, green leaf, and bold outline.
 */
private fun DrawScope.drawCartoonApple(
    cx: Float,
    cy: Float,
    cellSize: Float,
    pulse: Float
) {
    val r = cellSize * 0.36f * pulse

    // Wooden Stem
    val stemPath = Path().apply {
        moveTo(cx, cy - r + 3f)
        quadraticBezierTo(cx - cellSize * 0.08f, cy - r - cellSize * 0.16f, cx - cellSize * 0.04f, cy - r - cellSize * 0.20f)
        lineTo(cx + cellSize * 0.03f, cy - r - cellSize * 0.20f)
        quadraticBezierTo(cx - cellSize * 0.01f, cy - r - cellSize * 0.14f, cx + cellSize * 0.03f, cy - r + 3f)
        close()
    }
    drawPath(path = stemPath, color = AppleStem)
    drawPath(path = stemPath, color = Color.Black, style = Stroke(width = 1.5f))

    // Green Leaf pointing to the right
    val leafPath = Path().apply {
        moveTo(cx, cy - r - cellSize * 0.06f)
        quadraticBezierTo(cx + cellSize * 0.18f, cy - r - cellSize * 0.18f, cx + cellSize * 0.26f, cy - r - cellSize * 0.08f)
        quadraticBezierTo(cx + cellSize * 0.14f, cy - r - cellSize * 0.02f, cx, cy - r - cellSize * 0.06f)
        close()
    }
    drawPath(path = leafPath, color = AppleLeaf)
    drawPath(path = leafPath, color = Color.Black, style = Stroke(width = 1.5f))

    // Red Apple Fruit Body with bold cartoon outline
    drawCircle(
        color = Color.Black,
        radius = r + 2.5f,
        center = Offset(cx, cy)
    )

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(AppleGleam, AppleRed, Color(0xFF9E0B0B)),
            center = Offset(cx - r * 0.3f, cy - r * 0.3f),
            radius = r * 1.35f
        ),
        radius = r,
        center = Offset(cx, cy)
    )

    // Curved crescent specular shine reflection on the top-left
    val shinePath = Path().apply {
        moveTo(cx - r * 0.65f, cy - r * 0.25f)
        quadraticBezierTo(cx - r * 0.35f, cy - r * 0.70f, cx, cy - r * 0.65f)
        quadraticBezierTo(cx - r * 0.30f, cy - r * 0.55f, cx - r * 0.55f, cy - r * 0.18f)
        close()
    }
    drawPath(path = shinePath, color = Color.White.copy(alpha = 0.58f))
}

/**
 * Draws sharp cartoon hazard spikes.
 */
private fun DrawScope.drawSpike(x: Float, y: Float, cellSize: Float) {
    val w = cellSize * 0.85f
    val h = cellSize * 0.78f
    val cx = x + cellSize / 2f
    val bottomY = y + cellSize
    val topY = bottomY - h

    val spikePath = Path().apply {
        moveTo(cx - w / 2f, bottomY)
        lineTo(cx, topY)
        lineTo(cx + w / 2f, bottomY)
        close()
    }

    // Bold black outline
    drawPath(path = spikePath, color = WormOutline, style = Stroke(width = 3.5f))
    // Metal gradient fill
    drawPath(
        path = spikePath,
        brush = Brush.verticalGradient(
            colors = listOf(SpikePoint, SpikeBody),
            startY = topY,
            endY = bottomY
        )
    )
}

/**
 * Draws the authentic Apple Worm character:
 * - Rounded green capsule body with bold 3.5px black cartoon outline
 * - Lime-green underbelly / throat patch under the head
 * - Large googly cartoon eyes resting on top of the head
 * - Iconic, plump, protruding PURPLE / MAGENTA LIPS
 * - Comical X X eyes upon death
 */
private fun DrawScope.drawAppleWorm(
    snake: List<Position>,
    facing: Direction,
    skin: SnakeSkin,
    isDead: Boolean,
    offsetX: Float,
    offsetY: Float,
    cellSize: Float
) {
    if (snake.isEmpty()) return

    val outlineWidth = 3.5f
    val bodyRadius = cellSize * 0.40f

    // 1. First pass: Draw bold black outline behind the entire snake body
    for (i in snake.indices) {
        val pos = snake[i]
        val cx = offsetX + pos.x * cellSize + cellSize / 2f
        val cy = offsetY + pos.y * cellSize + cellSize / 2f

        drawCircle(
            color = Color.Black,
            radius = bodyRadius + outlineWidth,
            center = Offset(cx, cy)
        )

        // Connect with previous segment to form continuous sausage
        if (i > 0) {
            val prev = snake[i - 1]
            val prevCx = offsetX + prev.x * cellSize + cellSize / 2f
            val prevCy = offsetY + prev.y * cellSize + cellSize / 2f

            drawLine(
                color = Color.Black,
                start = Offset(prevCx, prevCy),
                end = Offset(cx, cy),
                strokeWidth = (bodyRadius + outlineWidth) * 2f
            )
        }
    }

    // 2. Second pass: Draw vibrant green body segments
    for (i in snake.indices.reversed()) {
        val pos = snake[i]
        val isHead = (i == 0)
        val cx = offsetX + pos.x * cellSize + cellSize / 2f
        val cy = offsetY + pos.y * cellSize + cellSize / 2f

        val segColor = if (isHead) skin.headColor else skin.bodyColor

        // Connect with previous segment
        if (i > 0) {
            val prev = snake[i - 1]
            val prevCx = offsetX + prev.x * cellSize + cellSize / 2f
            val prevCy = offsetY + prev.y * cellSize + cellSize / 2f

            drawLine(
                color = skin.bodyColor,
                start = Offset(prevCx, prevCy),
                end = Offset(cx, cy),
                strokeWidth = bodyRadius * 2f
            )
        }

        // Segment circle
        drawCircle(
            color = segColor,
            radius = bodyRadius,
            center = Offset(cx, cy)
        )

        // 3. Head specifics: Lime throat patch, googly eyes, and PURPLE LIPS!
        if (isHead) {
            // Lime-green throat patch on the bottom/underbelly of the head
            val throatPath = Path().apply {
                when (facing) {
                    Direction.RIGHT -> {
                        moveTo(cx - bodyRadius * 0.6f, cy + bodyRadius * 0.1f)
                        quadraticBezierTo(cx, cy + bodyRadius * 0.3f, cx + bodyRadius * 0.8f, cy + bodyRadius * 0.2f)
                        lineTo(cx + bodyRadius * 0.8f, cy + bodyRadius)
                        lineTo(cx - bodyRadius * 0.6f, cy + bodyRadius)
                        close()
                    }
                    Direction.LEFT -> {
                        moveTo(cx + bodyRadius * 0.6f, cy + bodyRadius * 0.1f)
                        quadraticBezierTo(cx, cy + bodyRadius * 0.3f, cx - bodyRadius * 0.8f, cy + bodyRadius * 0.2f)
                        lineTo(cx - bodyRadius * 0.8f, cy + bodyRadius)
                        lineTo(cx + bodyRadius * 0.6f, cy + bodyRadius)
                        close()
                    }
                    Direction.UP -> {
                        moveTo(cx - bodyRadius * 0.8f, cy + bodyRadius * 0.3f)
                        lineTo(cx + bodyRadius * 0.8f, cy + bodyRadius * 0.3f)
                        lineTo(cx + bodyRadius * 0.6f, cy + bodyRadius)
                        lineTo(cx - bodyRadius * 0.6f, cy + bodyRadius)
                        close()
                    }
                    Direction.DOWN -> {
                        moveTo(cx - bodyRadius * 0.8f, cy - bodyRadius * 0.3f)
                        lineTo(cx + bodyRadius * 0.8f, cy - bodyRadius * 0.3f)
                        lineTo(cx + bodyRadius * 0.6f, cy - bodyRadius)
                        lineTo(cx - bodyRadius * 0.6f, cy - bodyRadius)
                        close()
                    }
                }
            }
            drawPath(path = throatPath, color = skin.throatColor)

            // THE ICONIC PURPLE / MAGENTA LIPS (Apple Worm)
            drawPurpleLips(
                cx = cx,
                cy = cy,
                bodyRadius = bodyRadius,
                facing = facing,
                lipsColor = skin.lipsColor
            )

            // GOOGLY CARTOON EYES ON TOP OF THE HEAD
            drawGooglyEyes(
                cx = cx,
                cy = cy,
                bodyRadius = bodyRadius,
                facing = facing,
                isDead = isDead
            )
        }
    }
}

/**
 * Draws the thick, protruding PURPLE / MAGENTA lips (Apple Worm iconic feature).
 */
private fun DrawScope.drawPurpleLips(
    cx: Float,
    cy: Float,
    bodyRadius: Float,
    facing: Direction,
    lipsColor: Color
) {
    val lipLength = bodyRadius * 0.72f
    val lipWidth = bodyRadius * 0.48f

    // Center of lips positioned in front of head
    val (lipCx, lipCy) = when (facing) {
        Direction.LEFT -> Pair(cx - bodyRadius - lipLength * 0.25f, cy + bodyRadius * 0.12f)
        Direction.RIGHT -> Pair(cx + bodyRadius + lipLength * 0.25f, cy + bodyRadius * 0.12f)
        Direction.UP -> Pair(cx, cy - bodyRadius - lipLength * 0.25f)
        Direction.DOWN -> Pair(cx, cy + bodyRadius + lipLength * 0.25f)
    }

    // Outer bold cartoon outline for lips
    drawRoundRect(
        color = Color.Black,
        topLeft = Offset(lipCx - lipWidth / 2f - 2.5f, lipCy - lipLength / 2f - 2.5f),
        size = Size(lipWidth + 5f, lipLength + 5f),
        cornerRadius = CornerRadius(lipWidth / 2f, lipWidth / 2f)
    )

    // Magenta / Purple Lip Body
    drawRoundRect(
        color = lipsColor,
        topLeft = Offset(lipCx - lipWidth / 2f, lipCy - lipLength / 2f),
        size = Size(lipWidth, lipLength),
        cornerRadius = CornerRadius(lipWidth / 2f, lipWidth / 2f)
    )

    // Inner mouth cavity / crease
    val (creaseX1, creaseY1, creaseX2, creaseY2) = when (facing) {
        Direction.LEFT, Direction.RIGHT -> listOf(lipCx - lipWidth * 0.35f, lipCy, lipCx + lipWidth * 0.35f, lipCy)
        Direction.UP, Direction.DOWN -> listOf(lipCx, lipCy - lipLength * 0.35f, lipCx, lipCy + lipLength * 0.35f)
    }
    drawLine(
        color = WormLipsDark,
        start = Offset(creaseX1, creaseY1),
        end = Offset(creaseX2, creaseY2),
        strokeWidth = 3f
    )

    // Upper lip shiny reflection
    drawCircle(
        color = WormLipsHighlight.copy(alpha = 0.5f),
        radius = lipWidth * 0.20f,
        center = Offset(lipCx - lipWidth * 0.12f, lipCy - lipLength * 0.20f)
    )
}

/**
 * Draws large googly cartoon eyes on top of the head with looking pupils or X X upon death.
 */
private fun DrawScope.drawGooglyEyes(
    cx: Float,
    cy: Float,
    bodyRadius: Float,
    facing: Direction,
    isDead: Boolean
) {
    val eyeRadius = bodyRadius * 0.38f
    val pupilRadius = eyeRadius * 0.50f

    // Eyes are perched on top of the head
    val eyeY = cy - bodyRadius * 0.65f
    val eyeSpacing = bodyRadius * 0.34f

    val leftEyeCenter = Offset(cx - eyeSpacing, eyeY)
    val rightEyeCenter = Offset(cx + eyeSpacing, eyeY)

    for (eyeCenter in listOf(leftEyeCenter, rightEyeCenter)) {
        // Black eye outline
        drawCircle(
            color = Color.Black,
            radius = eyeRadius + 2f,
            center = eyeCenter
        )
        // White sclera
        drawCircle(
            color = Color.White,
            radius = eyeRadius,
            center = eyeCenter
        )

        if (isDead) {
            // Comical X eye when dead
            val xSize = eyeRadius * 0.60f
            drawLine(
                color = Color.Black,
                start = Offset(eyeCenter.x - xSize, eyeCenter.y - xSize),
                end = Offset(eyeCenter.x + xSize, eyeCenter.y + xSize),
                strokeWidth = 2.5f
            )
            drawLine(
                color = Color.Black,
                start = Offset(eyeCenter.x + xSize, eyeCenter.y - xSize),
                end = Offset(eyeCenter.x - xSize, eyeCenter.y + xSize),
                strokeWidth = 2.5f
            )
        } else {
            // Looking pupil
            val (pDx, pDy) = when (facing) {
                Direction.UP -> Pair(0f, -eyeRadius * 0.35f)
                Direction.DOWN -> Pair(0f, eyeRadius * 0.35f)
                Direction.LEFT -> Pair(-eyeRadius * 0.40f, 0f)
                Direction.RIGHT -> Pair(eyeRadius * 0.40f, 0f)
            }
            val pupilCenter = Offset(eyeCenter.x + pDx, eyeCenter.y + pDy)

            // Black pupil
            drawCircle(
                color = WormEyePupil,
                radius = pupilRadius,
                center = pupilCenter
            )
            // Tiny white specular glint
            drawCircle(
                color = Color.White,
                radius = pupilRadius * 0.35f,
                center = Offset(pupilCenter.x - pupilRadius * 0.25f, pupilCenter.y - pupilRadius * 0.25f)
            )
        }
    }
}

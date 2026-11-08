package com.snakepuzzle.game.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.snakepuzzle.game.model.Direction
import com.snakepuzzle.game.model.GameState
import com.snakepuzzle.game.model.Level
import com.snakepuzzle.game.ui.theme.*

@Composable
fun LandscapeGameHUD(
    level: Level,
    state: GameState,
    canUndo: Boolean,
    isDpadOnLeft: Boolean,
    onDirection: (Direction) -> Unit,
    onUndo: () -> Unit,
    onRestart: () -> Unit,
    onOpenLevelSelect: () -> Unit,
    onOpenSkinSelect: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        // 1. Center Game Board Canvas
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = if (isDpadOnLeft) 130.dp else 20.dp,
                    end = if (!isDpadOnLeft) 130.dp else 20.dp,
                    top = 58.dp,
                    bottom = 14.dp
                ),
            contentAlignment = Alignment.Center
        ) {
            content()
        }

        // 2. Top-Left: Glossy Blue Restart Button (Exact match to screenshot)
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            GlossyBubbleButton(
                icon = Icons.Default.Refresh,
                contentDescription = "Restart Level",
                enabled = true,
                onClick = onRestart
            )

            // Current Level Tag
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.90f),
                shadowElevation = 3.dp,
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF2072D6))
            ) {
                Text(
                    text = "LV ${level.id}",
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = Color(0xFF1B4F9B),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        // 3. Top-Center: Score, Best Score & Apple Counter Pill
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Main Score & Best Pill
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White.copy(alpha = 0.95f),
                shadowElevation = 4.dp,
                border = androidx.compose.foundation.BorderStroke(2.5.dp, Color(0xFF2072D6))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Current Score
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "⭐", fontSize = 14.sp)
                        Text(
                            text = "SCORE: ${state.score}",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = Color(0xFF1A365D)
                        )
                    }

                    // Divider
                    Box(
                        modifier = Modifier
                            .width(1.5.dp)
                            .height(16.dp)
                            .background(Color(0xFFCBD5E1))
                    )

                    // Best / High Score
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "👑", fontSize = 14.sp)
                        Text(
                            text = "BEST: ${state.highScore}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFFB45309)
                        )
                    }

                    // Divider
                    Box(
                        modifier = Modifier
                            .width(1.5.dp)
                            .height(16.dp)
                            .background(Color(0xFFCBD5E1))
                    )

                    // Apples remaining
                    val remaining = state.remainingApples.size
                    val total = level.applePositions.size
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "🍎", fontSize = 14.sp)
                        Text(
                            text = "${total - remaining}/$total",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = if (remaining == 0) Color(0xFF16A34A) else Color(0xFFDC2626)
                        )
                    }
                }
            }

            // Notice Banner (if any)
            AnimatedVisibility(
                visible = state.noticeMessage != null,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()
            ) {
                state.noticeMessage?.let { msg ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (state.isPortalUnlocked) Color(0xFF16A34A) else Color(0xFFEA580C),
                        shadowElevation = 4.dp
                    ) {
                        Text(
                            text = msg,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // 4. Top-Right: Glossy Blue Action Buttons (Undo, Levels, Skins, Settings)
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 16.dp, top = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Undo Button
            GlossyBubbleButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Undo Move",
                enabled = canUndo,
                onClick = onUndo
            )

            // Skins Button
            GlossyBubbleButton(
                icon = Icons.Default.Palette,
                contentDescription = "Change Skin",
                enabled = true,
                onClick = onOpenSkinSelect
            )

            // Levels Button
            GlossyBubbleButton(
                icon = Icons.Default.GridOn,
                contentDescription = "Level Select",
                enabled = true,
                onClick = onOpenLevelSelect
            )

            // Settings Button
            GlossyBubbleButton(
                icon = Icons.Default.Settings,
                contentDescription = "Settings",
                enabled = true,
                onClick = onOpenSettings
            )
        }

        // 5. Thumb D-Pad (Left or Right side depending on user preference)
        Box(
            modifier = Modifier
                .align(if (isDpadOnLeft) Alignment.BottomStart else Alignment.BottomEnd)
                .padding(
                    start = if (isDpadOnLeft) 18.dp else 0.dp,
                    end = if (!isDpadOnLeft) 18.dp else 0.dp,
                    bottom = 14.dp
                )
        ) {
            VirtualDPad(onDirection = onDirection)
        }
    }
}

/**
 * Glossy sky-blue circular button with thick white border matching the reference screenshot.
 */
@Composable
fun GlossyBubbleButton(
    icon: ImageVector,
    contentDescription: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gradientBrush = Brush.verticalGradient(
        colors = if (enabled) {
            listOf(Color(0xFF56B4F7), Color(0xFF1E6FD6))
        } else {
            listOf(Color(0xFF94A3B8), Color(0xFF64748B))
        }
    )

    Box(
        modifier = modifier
            .size(46.dp)
            .shadow(4.dp, CircleShape)
            .border(2.8.dp, Color.White, CircleShape)
            .clip(CircleShape)
            .background(gradientBrush)
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // Specular highlight crescent on top
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(14.dp)
                .align(Alignment.TopCenter)
                .padding(top = 2.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White.copy(alpha = 0.35f))
        )

        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

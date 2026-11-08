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
    onPrevLevel: () -> Unit,
    onNextLevel: () -> Unit,
    onOpenScoreboard: () -> Unit,
    onOpenLevelSelect: () -> Unit,
    onOpenSkinSelect: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val minutes = state.elapsedSeconds / 60
    val seconds = state.elapsedSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

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

        // 2. TOP HUD BAR - Unified responsive row (No overlapping on any screen size!)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // LEFT: Quick Action (Restart, Undo) & Prominent Level Navigator with "NEXT ▶"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Restart button
                GlossyBubbleButton(
                    icon = Icons.Default.Refresh,
                    contentDescription = "Restart Level",
                    enabled = true,
                    onClick = onRestart
                )

                // Undo button
                GlossyBubbleButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Undo Move",
                    enabled = canUndo,
                    onClick = onUndo
                )

                // Level Navigator Pill: [ ◀ ] LV X [ NEXT ▶ ]
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White.copy(alpha = 0.95f),
                    shadowElevation = 4.dp,
                    border = androidx.compose.foundation.BorderStroke(2.2.dp, Color(0xFF2072D6))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Previous Level Arrow
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (level.id > 1) Color(0xFFE2E8F0) else Color(0xFFF1F5F9))
                                .clickable(enabled = level.id > 1) { onPrevLevel() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "◀",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = if (level.id > 1) Color(0xFF1E293B) else Color(0xFF94A3B8)
                            )
                        }

                        // Current Level Label
                        Text(
                            text = "LV ${level.id}",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = Color(0xFF1B4F9B),
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )

                        // DEDICATED PROMINENT "NEXT ▶" BUTTON (Can never be missed!)
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (level.id < 50) Color(0xFF16A34A) else Color(0xFF94A3B8),
                            shadowElevation = 2.dp,
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White),
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable(enabled = level.id < 50) { onNextLevel() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "NEXT",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "▶",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // CENTER: 3 Hearts, Stopwatch, Score & Apples Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.95f),
                    shadowElevation = 4.dp,
                    border = androidx.compose.foundation.BorderStroke(2.2.dp, Color(0xFF2072D6))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 3 Hearts
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            for (i in 1..3) {
                                Text(
                                    text = if (i <= state.lives) "❤️" else "🖤",
                                    fontSize = 14.sp
                                )
                            }
                        }

                        // Divider
                        Box(modifier = Modifier.width(1.5.dp).height(14.dp).background(Color(0xFFCBD5E1)))

                        // Stopwatch
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(text = "⏱️", fontSize = 12.sp)
                            Text(
                                text = timeFormatted,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                color = Color(0xFF0F172A)
                            )
                        }

                        // Divider
                        Box(modifier = Modifier.width(1.5.dp).height(14.dp).background(Color(0xFFCBD5E1)))

                        // Current Score
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(text = "⭐", fontSize = 12.sp)
                            Text(
                                text = "${state.score}",
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                color = Color(0xFF1E3A8A)
                            )
                        }

                        // Divider
                        Box(modifier = Modifier.width(1.5.dp).height(14.dp).background(Color(0xFFCBD5E1)))

                        // Apples Remaining
                        val remaining = state.remainingApples.size
                        val total = level.applePositions.size
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(text = "🍎", fontSize = 12.sp)
                            Text(
                                text = "${total - remaining}/$total",
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                color = if (remaining == 0) Color(0xFF16A34A) else Color(0xFFDC2626)
                            )
                        }
                    }
                }
            }

            // RIGHT: Scoreboard, Levels, Skins, Settings
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Scoreboard (Bảng Điểm)
                GlossyBubbleButton(
                    icon = Icons.Default.EmojiEvents,
                    contentDescription = "Scoreboard",
                    enabled = true,
                    onClick = onOpenScoreboard
                )

                // Levels Button
                GlossyBubbleButton(
                    icon = Icons.Default.GridOn,
                    contentDescription = "Level Select",
                    enabled = true,
                    onClick = onOpenLevelSelect
                )

                // Skins Button
                GlossyBubbleButton(
                    icon = Icons.Default.Palette,
                    contentDescription = "Change Skin",
                    enabled = true,
                    onClick = onOpenSkinSelect
                )

                // Settings Button
                GlossyBubbleButton(
                    icon = Icons.Default.Settings,
                    contentDescription = "Settings",
                    enabled = true,
                    onClick = onOpenSettings
                )
            }
        }

        // 3. Notice Message Banner (Centered below top bar)
        AnimatedVisibility(
            visible = state.noticeMessage != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 54.dp)
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

        // 5. Thumb D-Pad
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
            .size(44.dp)
            .shadow(4.dp, CircleShape)
            .border(2.6.dp, Color.White, CircleShape)
            .clip(CircleShape)
            .background(gradientBrush)
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // Specular highlight crescent on top
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(13.dp)
                .align(Alignment.TopCenter)
                .padding(top = 2.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White.copy(alpha = 0.35f))
        )

        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(22.dp)
        )
    }
}

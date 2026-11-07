package com.snakepuzzle.game.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
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
                    start = if (isDpadOnLeft) 140.dp else 16.dp,
                    end = if (!isDpadOnLeft) 140.dp else 80.dp,
                    top = 54.dp,
                    bottom = 12.dp
                ),
            contentAlignment = Alignment.Center
        ) {
            content()
        }

        // 2. Top Info Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Level & World info badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White.copy(alpha = 0.92f),
                shadowElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "LV ${level.id}",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "• ${level.name} (${level.world})",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }

            // Notice Banner (e.g. "PORTAL OPEN!" or "Collect all apples!")
            AnimatedVisibility(
                visible = state.noticeMessage != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                state.noticeMessage?.let { msg ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (state.isPortalUnlocked) Color(0xFF2ECC71) else Color(0xFFE67E22),
                        shadowElevation = 4.dp
                    ) {
                        Text(
                            text = msg,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Apples counter & Moves counter
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // Apples badge
                val remaining = state.remainingApples.size
                val total = level.applePositions.size
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.92f),
                    shadowElevation = 3.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "🍎", fontSize = 14.sp)
                        Text(
                            text = "${total - remaining}/$total",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (remaining == 0) Color(0xFF27AE60) else TextPrimary
                        )
                    }
                }

                // Steps / Moves badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.92f),
                    shadowElevation = 3.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsWalk,
                            contentDescription = "Steps",
                            tint = TextPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${state.movesCount}",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                    }
                }
            }
        }

        // 3. Thumb D-Pad (Left or Right side depending on user preference)
        Box(
            modifier = Modifier
                .align(if (isDpadOnLeft) Alignment.BottomStart else Alignment.BottomEnd)
                .padding(
                    start = if (isDpadOnLeft) 24.dp else 0.dp,
                    end = if (!isDpadOnLeft) 24.dp else 0.dp,
                    bottom = 16.dp
                )
        ) {
            VirtualDPad(onDirection = onDirection)
        }

        // 4. Action Buttons Panel (Undo, Restart, Skins, Settings, Levels)
        Column(
            modifier = Modifier
                .align(if (isDpadOnLeft) Alignment.CenterEnd else Alignment.CenterStart)
                .padding(
                    end = if (isDpadOnLeft) 16.dp else 0.dp,
                    start = if (!isDpadOnLeft) 16.dp else 0.dp
                ),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Undo button
            ActionButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                label = "Undo",
                enabled = canUndo,
                onClick = onUndo
            )

            // Restart button
            ActionButton(
                icon = Icons.Default.Refresh,
                label = "Reset",
                enabled = true,
                onClick = onRestart
            )

            // Skins button
            ActionButton(
                icon = Icons.Default.Palette,
                label = "Skins",
                enabled = true,
                onClick = onOpenSkinSelect
            )

            // Levels button
            ActionButton(
                icon = Icons.Default.GridOn,
                label = "Levels",
                enabled = true,
                onClick = onOpenLevelSelect
            )

            // Settings button
            ActionButton(
                icon = Icons.Default.Settings,
                label = "Config",
                enabled = true,
                onClick = onOpenSettings
            )
        }
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    label: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = CircleShape,
        color = if (enabled) Color.White.copy(alpha = 0.95f) else Color.White.copy(alpha = 0.45f),
        shadowElevation = if (enabled) 3.dp else 0.dp,
        modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .clickable(enabled = enabled) { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (enabled) TextPrimary else TextSecondary.copy(alpha = 0.4f),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

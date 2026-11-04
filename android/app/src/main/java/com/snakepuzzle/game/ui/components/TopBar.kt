package com.snakepuzzle.game.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.snakepuzzle.game.ui.theme.TextPrimary
import com.snakepuzzle.game.ui.theme.TextSecondary

@Composable
fun TopBar(
    movesCount: Int,
    canUndo: Boolean,
    isSoundEnabled: Boolean,
    onOpenLevelSelect: () -> Unit,
    onUndo: () -> Unit,
    onRestart: () -> Unit,
    onToggleSound: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Levels button
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            modifier = Modifier
                .shadow(4.dp, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .clickable { onOpenLevelSelect() }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.GridOn,
                    contentDescription = "Levels",
                    tint = TextPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Levels",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
            }
        }

        // Moves chip
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            modifier = Modifier.shadow(4.dp, RoundedCornerShape(12.dp))
        ) {
            Text(
                text = "Moves: $movesCount",
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }

        // Control actions
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Undo
            CircleActionButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDesc = "Undo",
                enabled = canUndo,
                onClick = onUndo
            )

            // Restart
            CircleActionButton(
                icon = Icons.Default.Refresh,
                contentDesc = "Restart",
                enabled = true,
                onClick = onRestart
            )

            // Sound
            CircleActionButton(
                icon = if (isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                contentDesc = "Sound",
                enabled = true,
                tint = if (isSoundEnabled) TextPrimary else Color.Red,
                onClick = onToggleSound
            )
        }
    }
}

@Composable
private fun CircleActionButton(
    icon: ImageVector,
    contentDesc: String,
    enabled: Boolean,
    tint: Color = TextPrimary,
    onClick: () -> Unit
) {
    Surface(
        shape = CircleShape,
        color = Color.White,
        modifier = Modifier
            .size(38.dp)
            .shadow(4.dp, CircleShape)
            .clip(CircleShape)
            .clickable(enabled = enabled) { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDesc,
                tint = if (enabled) tint else TextSecondary.copy(alpha = 0.35f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

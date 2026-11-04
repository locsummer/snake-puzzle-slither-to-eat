package com.snakepuzzle.game.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.snakepuzzle.game.model.Direction
import com.snakepuzzle.game.ui.theme.TextPrimary

@Composable
fun VirtualDPad(
    onDirection: (Direction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // UP
        DPadButton(icon = Icons.Default.KeyboardArrowUp, contentDesc = "Up") {
            onDirection(Direction.UP)
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // LEFT
            DPadButton(icon = Icons.Default.KeyboardArrowLeft, contentDesc = "Left") {
                onDirection(Direction.LEFT)
            }

            // Center circle decoration
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.4f))
            )

            // RIGHT
            DPadButton(icon = Icons.Default.KeyboardArrowRight, contentDesc = "Right") {
                onDirection(Direction.RIGHT)
            }
        }

        // DOWN
        DPadButton(icon = Icons.Default.KeyboardArrowDown, contentDesc = "Down") {
            onDirection(Direction.DOWN)
        }
    }
}

@Composable
private fun DPadButton(
    icon: ImageVector,
    contentDesc: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(62.dp)
            .shadow(6.dp, CircleShape)
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = androidx.compose.material.ripple.rememberRipple(bounded = true),
                onClick = onClick
            ),
        color = Color.White,
        shape = CircleShape
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDesc,
                tint = TextPrimary,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

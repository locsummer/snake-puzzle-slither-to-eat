package com.snakepuzzle.game.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.snakepuzzle.game.audio.SoundManager
import com.snakepuzzle.game.engine.LevelRepository
import com.snakepuzzle.game.ui.theme.TextPrimary
import com.snakepuzzle.game.ui.theme.TextSecondary

@Composable
fun SettingsDialog(
    soundManager: SoundManager,
    repository: LevelRepository,
    onClose: () -> Unit
) {
    Dialog(onDismissRequest = onClose) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier
                .width(380.dp)
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "⚙️ Game Settings",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )

                // 1. Music (BGM)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Background Music", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                        Text("Ambient Chiptune Melody", fontSize = 12.sp, color = TextSecondary)
                    }
                    Switch(
                        checked = soundManager.isMusicEnabled,
                        onCheckedChange = { soundManager.toggleMusic() }
                    )
                }

                // 2. Sound Effects (SFX)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Sound Effects", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                        Text("Slither, munch, victory chimes", fontSize = 12.sp, color = TextSecondary)
                    }
                    Switch(
                        checked = soundManager.isSoundEnabled,
                        onCheckedChange = { soundManager.toggleSfx() }
                    )
                }

                // 3. Haptics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Haptic Feedback", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                        Text("Tactile vibration on moves", fontSize = 12.sp, color = TextSecondary)
                    }
                    Switch(
                        checked = soundManager.isHapticsEnabled,
                        onCheckedChange = { soundManager.toggleHaptics() }
                    )
                }

                // 4. D-Pad Hand (Left/Right side)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("D-Pad Position", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                        Text(if (repository.isDpadOnLeft) "Left Hand Side" else "Right Hand Side", fontSize = 12.sp, color = TextSecondary)
                    }
                    Button(
                        onClick = { repository.setDpadSide(!repository.isDpadOnLeft) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3498DB))
                    ) {
                        Text(if (repository.isDpadOnLeft) "Move Right" else "Move Left", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = onClose,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

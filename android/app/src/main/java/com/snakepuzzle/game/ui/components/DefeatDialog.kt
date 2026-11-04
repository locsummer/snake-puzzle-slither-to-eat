package com.snakepuzzle.game.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.snakepuzzle.game.ui.theme.SpikeTip
import com.snakepuzzle.game.ui.theme.TextPrimary
import com.snakepuzzle.game.ui.theme.TextSecondary

@Composable
fun DefeatDialog(
    reason: String,
    canUndo: Boolean,
    onUndo: () -> Unit,
    onRestart: () -> Unit,
    onSelectLevel: () -> Unit
) {
    Dialog(onDismissRequest = {}) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "💥 OOPS!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = SpikeTip
                )

                Text(
                    text = reason,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                if (canUndo) {
                    Button(
                        onClick = onUndo,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2673E0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("Undo Move", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = onRestart,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0F2F5)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Text("Try Again", fontWeight = FontWeight.Bold, color = TextPrimary)
                }

                TextButton(onClick = onSelectLevel) {
                    Text("Choose Level", fontWeight = FontWeight.SemiBold, color = TextSecondary)
                }
            }
        }
    }
}

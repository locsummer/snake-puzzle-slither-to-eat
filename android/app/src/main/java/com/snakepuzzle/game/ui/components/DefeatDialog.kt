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
import com.snakepuzzle.game.ui.theme.SpikePoint
import com.snakepuzzle.game.ui.theme.TextSecondary

@Composable
fun DefeatDialog(
    reason: String,
    score: Int,
    highScore: Int,
    levelReached: Int,
    onRestartFromBeginning: () -> Unit,
    onViewScoreboard: () -> Unit
) {
    Dialog(onDismissRequest = {}) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 10.dp,
            modifier = Modifier
                .width(420.dp)
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "💀 HẾT 3 TIM - GAME OVER!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = SpikePoint
                )

                Text(
                    text = "$reason\nBạn đã dùng hết cả 3 tim!",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                // Score Card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFEF2F2),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "TỔNG ĐIỂM: $score PTS",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF991B1B)
                        )
                        Text(
                            text = "Màn chơi dừng lại: LV $levelReached",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF475569)
                        )
                        Text(
                            text = "✅ Đã lưu kết quả vào Bảng Điểm Kỷ Lục!",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF15803D)
                        )
                    }
                }

                // Action Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onRestartFromBeginning,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("🔄 CHƠI LẠI TỪ LEVEL 1 (HỒI 3 TIM)", fontSize = 14.sp, fontWeight = FontWeight.Black)
                    }

                    OutlinedButton(
                        onClick = onViewScoreboard,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        Text("🏆 Xem Bảng Điểm Kỷ Lục", fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A))
                    }
                }
            }
        }
    }
}

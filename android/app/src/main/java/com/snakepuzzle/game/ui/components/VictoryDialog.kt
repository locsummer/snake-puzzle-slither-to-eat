package com.snakepuzzle.game.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.snakepuzzle.game.ui.theme.*

@Composable
fun VictoryDialog(
    levelId: Int,
    levelName: String,
    world: String,
    moves: Int,
    optimalMoves: Int,
    stars: Int,
    levelScore: Int,
    totalScore: Int,
    highScore: Int,
    elapsedSeconds: Int,
    timeBonus: Int,
    isNewHighScore: Boolean,
    hasNextLevel: Boolean,
    onNextLevel: () -> Unit,
    onReplay: () -> Unit,
    onSelectLevel: () -> Unit
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "🎉 HOÀN THÀNH MÀN CHƠI!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF16A34A)
                )

                Text(
                    text = "Màn $levelId: $levelName ($world)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )

                // 3 Stars rating
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 1..3) {
                        Icon(
                            imageVector = if (i <= stars) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = "Star $i",
                            tint = if (i <= stars) StarActive else StarInactive,
                            modifier = Modifier.size(42.dp)
                        )
                    }
                }

                // Score Card with Time Bonus
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFF0FDF4),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "⏱️ Thời gian: ${elapsedSeconds}s  •  Thưởng tốc độ: +$timeBonus PTS",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF15803D)
                        )
                        Text(
                            text = "ĐIỂM MÀN NÀY: +$levelScore PTS",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF166534)
                        )
                        Text(
                            text = "TỔNG ĐIỂM TÍCH LŨY: $totalScore PTS",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF14532D)
                        )
                        if (isNewHighScore) {
                            Text(
                                text = "🔥 KỶ LỤC MỚI (NEW RECORD)! 🔥",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFB45309)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Next Level Button (Guaranteed to work and loads next level!)
                    Button(
                        onClick = onNextLevel,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(48.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (hasNextLevel) "Màn Tiếp Theo" else "Chơi Lại",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = onReplay,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text("Chơi Lại", fontWeight = FontWeight.Bold, color = TextPrimary)
                    }

                    OutlinedButton(
                        onClick = onSelectLevel,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text("Màn Chơi", fontWeight = FontWeight.Bold, color = TextSecondary)
                    }
                }
            }
        }
    }
}

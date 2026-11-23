package com.snakepuzzle.game.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.window.DialogProperties
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
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = Color.White,
            shadowElevation = 12.dp,
            modifier = Modifier
                .width(540.dp)
                .wrapContentHeight()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 1. Top Header: Title & Stars in one compact row!
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "🎉 HOÀN THÀNH MÀN CHƠI!",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF16A34A)
                        )
                        Text(
                            text = "Màn $levelId: $levelName ($world)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                    }

                    // 3 Stars rating
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in 1..3) {
                            Icon(
                                imageVector = if (i <= stars) Icons.Filled.Star else Icons.Outlined.Star,
                                contentDescription = "Star $i",
                                tint = if (i <= stars) StarActive else StarInactive,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                // 2. Score Summary Pill (Single horizontal row with maxLines=1)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF0FDF4),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 7.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⏱️ ${elapsedSeconds}s (+${timeBonus} PTS)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF15803D),
                            maxLines = 1
                        )
                        Text(
                            text = "MÀN: +$levelScore PTS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF166534),
                            maxLines = 1
                        )
                        Text(
                            text = "TỔNG: $totalScore PTS",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF14532D),
                            maxLines = 1
                        )
                    }
                }

                if (isNewHighScore) {
                    Text(
                        text = "🔥 KỶ LỤC MỚI (NEW RECORD)! 🔥",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFB45309)
                    )
                }

                // 3. ACTION BUTTONS ROW (ALWAYS 100% VISIBLE & PROMINENT!)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // NEXT LEVEL BUTTON: HUGE, GLOWING GREEN, UNMISSABLE!
                    Button(
                        onClick = onNextLevel,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
                        modifier = Modifier
                            .weight(1.8f)
                            .height(48.dp)
                    ) {
                        Text(
                            text = if (hasNextLevel) "NEXT LEVEL ▶" else "CHƠI LẠI",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }

                    OutlinedButton(
                        onClick = onReplay,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text("Chơi Lại", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
                    }

                    OutlinedButton(
                        onClick = onSelectLevel,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text("Chọn Màn", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }
        }
    }
}

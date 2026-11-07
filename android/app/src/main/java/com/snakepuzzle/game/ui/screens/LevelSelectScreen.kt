package com.snakepuzzle.game.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.snakepuzzle.game.engine.LevelRepository
import com.snakepuzzle.game.model.Level
import com.snakepuzzle.game.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelSelectScreen(
    repository: LevelRepository,
    onSelectLevel: (Level) -> Unit,
    onClose: () -> Unit
) {
    val worlds = listOf(
        "Green Meadows",
        "Crystal Caverns",
        "Sunken Ruins",
        "Sky Citadel",
        "Magma Core"
    )

    var selectedWorldIndex by remember { mutableIntStateOf(0) }
    val currentWorldName = worlds[selectedWorldIndex]
    val levelsInWorld = repository.levels.filter { it.world == currentWorldName }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "🗺️ Choose Level (50 Levels)",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 18.sp
                    )
                },
                actions = {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(MeadowSkyTop, MeadowSkyBottom)
                    )
                )
        ) {
            // World Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedWorldIndex,
                containerColor = Color.White,
                edgePadding = 16.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                worlds.forEachIndexed { index, worldName ->
                    Tab(
                        selected = selectedWorldIndex == index,
                        onClick = { selectedWorldIndex = index },
                        text = {
                            Text(
                                "World ${index + 1}: $worldName",
                                fontWeight = if (selectedWorldIndex == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    )
                }
            }

            // Levels Grid
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 100.dp),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(levelsInWorld) { level ->
                    val unlocked = repository.isLevelUnlocked(level.id)
                    val stars = repository.getStars(level.id)
                    val best = repository.getBestMoves(level.id)

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (unlocked) Color.White else Color.White.copy(alpha = 0.5f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp)
                            .shadow(if (unlocked) 3.dp else 0.dp, RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(enabled = unlocked) {
                                onSelectLevel(level)
                            }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            if (unlocked) {
                                Text(
                                    text = "${level.id}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary
                                )
                                Text(
                                    text = level.name,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    for (s in 1..3) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = if (s <= stars) StarActive else StarInactive,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                }
                                if (best > 0) {
                                    Text(
                                        text = "$best moves",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Gray
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Locked",
                                    tint = Color.Gray.copy(alpha = 0.5f),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "LV ${level.id}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Gray.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

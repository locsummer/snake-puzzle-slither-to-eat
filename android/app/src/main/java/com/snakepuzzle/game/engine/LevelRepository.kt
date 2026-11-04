package com.snakepuzzle.game.engine

import android.content.Context
import android.content.SharedPreferences
import com.snakepuzzle.game.model.Level
import org.json.JSONArray

class LevelRepository private constructor(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("SnakePuzzlePrefs", Context.MODE_PRIVATE)

    var levels: List<Level> = emptyList()
        private set

    var unlockedLevelMax: Int = 1
        private set

    init {
        unlockedLevelMax = prefs.getInt(KEY_UNLOCKED_MAX, 1).coerceAtLeast(1)
        levels = loadLevelsFromAssets(context)
    }

    private fun loadLevelsFromAssets(context: Context): List<Level> {
        return try {
            val jsonString = context.assets.open("levels.json").bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(jsonString)
            val result = mutableListOf<Level>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val id = obj.getInt("id")
                val name = obj.getString("name")
                val width = obj.getInt("width")
                val height = obj.getInt("height")

                val gridArray = obj.getJSONArray("grid")
                val grid = mutableListOf<String>()
                for (g in 0 until gridArray.length()) {
                    grid.add(gridArray.getString(g))
                }

                val snakeArray = obj.getJSONArray("snake")
                val snake = mutableListOf<List<Int>>()
                for (s in 0 until snakeArray.length()) {
                    val pos = snakeArray.getJSONArray(s)
                    snake.add(listOf(pos.getInt(0), pos.getInt(1)))
                }

                val targetApples = obj.getInt("targetApples")
                result.add(Level(id, name, width, height, grid, snake, targetApples))
            }
            result
        } catch (e: Exception) {
            e.printStackTrace()
            fallbackLevels
        }
    }

    fun isLevelUnlocked(levelId: Int): Boolean = levelId <= unlockedLevelMax

    fun getStars(levelId: Int): Int = prefs.getInt("${KEY_STARS_PREFIX}$levelId", 0)

    fun getBestMoves(levelId: Int): Int = prefs.getInt("${KEY_BEST_MOVES_PREFIX}$levelId", 0)

    fun recordWin(levelId: Int, moves: Int, targetApples: Int) {
        val currentStars = getStars(levelId)
        val newStars = when {
            moves <= targetApples + 10 -> 3
            moves <= targetApples + 18 -> 2
            else -> 1
        }
        if (newStars > currentStars) {
            prefs.edit().putInt("${KEY_STARS_PREFIX}$levelId", newStars).apply()
        }

        val existingBest = getBestMoves(levelId)
        if (existingBest == 0 || moves < existingBest) {
            prefs.edit().putInt("${KEY_BEST_MOVES_PREFIX}$levelId", moves).apply()
        }

        if (levelId >= unlockedLevelMax) {
            unlockedLevelMax = (levelId + 1).coerceAtMost(levels.size)
            prefs.edit().putInt(KEY_UNLOCKED_MAX, unlockedLevelMax).apply()
        }
    }

    companion object {
        private const val KEY_UNLOCKED_MAX = "unlocked_level_max"
        private const val KEY_STARS_PREFIX = "stars_"
        private const val KEY_BEST_MOVES_PREFIX = "best_moves_"

        @Volatile
        private var instance: LevelRepository? = null

        fun getInstance(context: Context): LevelRepository {
            return instance ?: synchronized(this) {
                instance ?: LevelRepository(context.applicationContext).also { instance = it }
            }
        }

        val fallbackLevels = listOf(
            Level(
                id = 1,
                name = "First Bite",
                width = 8,
                height = 5,
                grid = listOf(
                    "........",
                    "........",
                    ".A....O.",
                    "SSSSSSSS",
                    "SSSSSSSS"
                ),
                snake = listOf(listOf(3, 2), listOf(4, 2)),
                targetApples = 1
            ),
            Level(
                id = 2,
                name = "Across the Gap",
                width = 9,
                height = 5,
                grid = listOf(
                    ".........",
                    ".........",
                    "..A...O..",
                    "SSSS.SSSS",
                    "SSSS.SSSS"
                ),
                snake = listOf(listOf(1, 2), listOf(0, 2)),
                targetApples = 1
            ),
            Level(
                id = 3,
                name = "Safe Fall",
                width = 8,
                height = 6,
                grid = listOf(
                    "........",
                    "SS......",
                    "SS..A...",
                    "SS......",
                    "SS....OS",
                    "SSSSSSSS"
                ),
                snake = listOf(listOf(1, 0), listOf(0, 0)),
                targetApples = 1
            ),
            Level(
                id = 4,
                name = "Spike Crossing",
                width = 9,
                height = 6,
                grid = listOf(
                    ".........",
                    ".........",
                    "..A...O..",
                    "SSSS.SSSS",
                    "SSSSXSSSS",
                    "SSSSSSSSSS"
                ),
                snake = listOf(listOf(1, 2), listOf(0, 2)),
                targetApples = 1
            ),
            Level(
                id = 5,
                name = "Double Growth",
                width = 10,
                height = 6,
                grid = listOf(
                    "..........",
                    "..........",
                    ".A..A..O..",
                    "SSS..SSSSS",
                    "SSSXXSSSSS",
                    "SSSSSSSSSS"
                ),
                snake = listOf(listOf(1, 2), listOf(0, 2)),
                targetApples = 2
            ),
            Level(
                id = 6,
                name = "Terraces",
                width = 9,
                height = 6,
                grid = listOf(
                    ".......OS",
                    ".....SSSS",
                    "...A.SSSS",
                    ".SSSSSSSS",
                    "SSSSSSSSS",
                    "SSSSSSSSS"
                ),
                snake = listOf(listOf(1, 3), listOf(0, 3)),
                targetApples = 1
            ),
            Level(
                id = 7,
                name = "The High Portal",
                width = 9,
                height = 7,
                grid = listOf(
                    ".......O.",
                    "......SS.",
                    "....A.SS.",
                    "..A.S.SS.",
                    "SSS.S.SS.",
                    "SSS...SS.",
                    "SSSSSSSSS"
                ),
                snake = listOf(listOf(1, 3), listOf(0, 3)),
                targetApples = 2
            ),
            Level(
                id = 8,
                name = "The S-Bend",
                width = 9,
                height = 6,
                grid = listOf(
                    "SSSSSSSSS",
                    "S....A..S",
                    "S.SSSSS.S",
                    "S.O.....S",
                    "SSSSSSSSS",
                    "SSSSSSSSS"
                ),
                snake = listOf(listOf(2, 1), listOf(1, 1)),
                targetApples = 1
            ),
            Level(
                id = 9,
                name = "The Pit",
                width = 9,
                height = 6,
                grid = listOf(
                    ".........",
                    "SS.....SS",
                    "SS..A..SS",
                    "SS.....SS",
                    "SS...O.SS",
                    "SSSSSSSSS"
                ),
                snake = listOf(listOf(1, 0), listOf(0, 0)),
                targetApples = 1
            ),
            Level(
                id = 10,
                name = "Pillar Steps",
                width = 10,
                height = 6,
                grid = listOf(
                    "..........",
                    "..........",
                    ".A..A..O..",
                    "SS..S..SSS",
                    "SSXXSXXSSS",
                    "SSSSSSSSSS"
                ),
                snake = listOf(listOf(0, 2), listOf(0, 3)),
                targetApples = 2
            ),
            Level(
                id = 11,
                name = "The Bridge",
                width = 11,
                height = 6,
                grid = listOf(
                    "...........",
                    "..A....A...",
                    "SSS......OS",
                    "SSS.SS...SS",
                    "SSSXXXXXXSS",
                    "SSSSSSSSSSS"
                ),
                snake = listOf(listOf(1, 2), listOf(0, 2)),
                targetApples = 2
            ),
            Level(
                id = 12,
                name = "Two Level Pit",
                width = 9,
                height = 7,
                grid = listOf(
                    ".........",
                    "SS.....SS",
                    "SS..A..SS",
                    "SS.SSS.SS",
                    "SS..A..SS",
                    "SS...O.SS",
                    "SSSSSSSSS"
                ),
                snake = listOf(listOf(1, 0), listOf(0, 0)),
                targetApples = 2
            ),
            Level(
                id = 13,
                name = "Spike Overpass",
                width = 11,
                height = 6,
                grid = listOf(
                    "...........",
                    "....A...O..",
                    "SSSS..SSSSS",
                    "SSSS..SSSSS",
                    "SSSSXXSSSSS",
                    "SSSSSSSSSSS"
                ),
                snake = listOf(listOf(1, 1), listOf(0, 1)),
                targetApples = 1
            ),
            Level(
                id = 14,
                name = "Zig Zag",
                width = 10,
                height = 7,
                grid = listOf(
                    "..........",
                    ".A...SS..O",
                    "SSSS.SS.SS",
                    "SSSS....SS",
                    "SSSS.A..SS",
                    "SSSSSSSSSS",
                    "SSSSSSSSSS"
                ),
                snake = listOf(listOf(1, 0), listOf(0, 0)),
                targetApples = 2
            ),
            Level(
                id = 15,
                name = "Grandmaster",
                width = 11,
                height = 8,
                grid = listOf(
                    "...........",
                    ".A......A..",
                    "SSS.SSS.SSS",
                    "S...S.S...S",
                    "S.O.S.S...S",
                    "SSS.S.S.SSS",
                    "SSS...S.SSS",
                    "SSSSSSSSSSS"
                ),
                snake = listOf(listOf(1, 1), listOf(0, 1)),
                targetApples = 2
            )
        )
    }
}

package com.snakepuzzle.game.engine

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.snakepuzzle.game.model.Level
import com.snakepuzzle.game.model.SnakeSkin
import org.json.JSONArray

class LevelRepository private constructor(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("SnakePuzzlePrefs_v2", Context.MODE_PRIVATE)

    var levels: List<Level> = emptyList()
        private set

    var unlockedLevelMax by mutableStateOf(1)
        private set

    var selectedSkin by mutableStateOf(SnakeSkin.EMERALD)
        private set

    var isDpadOnLeft by mutableStateOf(true)
        private set

    var highScore by mutableStateOf(0)
        private set

    init {
        unlockedLevelMax = prefs.getInt(KEY_UNLOCKED_MAX, 1).coerceAtLeast(1)
        val skinId = prefs.getString(KEY_SELECTED_SKIN, SnakeSkin.EMERALD.id) ?: SnakeSkin.EMERALD.id
        selectedSkin = SnakeSkin.fromId(skinId)
        isDpadOnLeft = prefs.getBoolean(KEY_DPAD_LEFT, true)
        highScore = prefs.getInt(KEY_HIGH_SCORE, 0)
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
                val world = obj.optString("world", "Green Meadows")
                val width = obj.getInt("width")
                val height = obj.getInt("height")
                val optimalMoves = obj.optInt("optimalMoves", 10)

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
                result.add(Level(id, name, world, width, height, grid, snake, targetApples, optimalMoves))
            }
            if (result.isNotEmpty()) result else fallback50Levels
        } catch (e: Exception) {
            e.printStackTrace()
            fallback50Levels
        }
    }

    fun isLevelUnlocked(levelId: Int): Boolean = levelId <= unlockedLevelMax

    fun getStars(levelId: Int): Int = prefs.getInt("${KEY_STARS_PREFIX}$levelId", 0)

    fun getBestMoves(levelId: Int): Int = prefs.getInt("${KEY_BEST_MOVES_PREFIX}$levelId", 0)

    fun recordWin(levelId: Int, moves: Int, targetApples: Int, optimalMoves: Int) {
        val currentStars = getStars(levelId)
        val newStars = when {
            moves <= optimalMoves + 2 -> 3
            moves <= optimalMoves + 6 -> 2
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

    fun setSkin(skin: SnakeSkin) {
        selectedSkin = skin
        prefs.edit().putString(KEY_SELECTED_SKIN, skin.id).apply()
    }

    fun setDpadSide(onLeft: Boolean) {
        isDpadOnLeft = onLeft
        prefs.edit().putBoolean(KEY_DPAD_LEFT, onLeft).apply()
    }

    fun recordScore(score: Int): Boolean {
        if (score > highScore) {
            highScore = score
            prefs.edit().putInt(KEY_HIGH_SCORE, score).apply()
            return true
        }
        return false
    }

    companion object {
        private const val KEY_UNLOCKED_MAX = "unlocked_level_max"
        private const val KEY_STARS_PREFIX = "stars_"
        private const val KEY_BEST_MOVES_PREFIX = "best_moves_"
        private const val KEY_SELECTED_SKIN = "selected_skin"
        private const val KEY_DPAD_LEFT = "dpad_left"
        private const val KEY_HIGH_SCORE = "high_score"

        @Volatile
        private var instance: LevelRepository? = null

        fun getInstance(context: Context): LevelRepository {
            return instance ?: synchronized(this) {
                instance ?: LevelRepository(context.applicationContext).also { instance = it }
            }
        }

        val fallback50Levels = listOf(
            Level(1, "First Bite", "Green Meadows", 8, 5, listOf("........","........",".A....O.","SSSSSSSS","SSSSSSSS"), listOf(listOf(3, 2), listOf(4, 2)), 1, 8),
            Level(2, "Reaching Out", "Green Meadows", 8, 5, listOf("........","........","..A...O.","SSSSSSSS","SSSSSSSS"), listOf(listOf(1, 2), listOf(0, 2)), 1, 5)
        )
    }
}

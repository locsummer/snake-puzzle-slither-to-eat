package com.snakepuzzle.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.snakepuzzle.game.ui.screens.GameScreen
import com.snakepuzzle.game.ui.theme.SnakePuzzleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SnakePuzzleTheme {
                GameScreen()
            }
        }
    }
}

package com.snakepuzzle.game.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = MeadowGrass,
    secondary = PortalVortexOuter,
    tertiary = AppleRed,
    background = MeadowSkyTop,
    surface = Color.White
)

@Composable
fun SnakePuzzleTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}

package com.snakepuzzle.game.model

import androidx.compose.ui.graphics.Color

enum class SnakeSkin(
    val id: String,
    val displayName: String,
    val headColor: Color,
    val bodyColor: Color,
    val tailColor: Color,
    val accentColor: Color,
    val bellyColor: Color,
    val eyeColor: Color,
    val pupilColor: Color,
    val cheekColor: Color
) {
    EMERALD(
        id = "emerald",
        displayName = "Emerald Viper",
        headColor = Color(0xFF2ECC71),
        bodyColor = Color(0xFF27AE60),
        tailColor = Color(0xFF1E8449),
        accentColor = Color(0xFF58D68D),
        bellyColor = Color(0xFFF9E79F),
        eyeColor = Color.White,
        pupilColor = Color(0xFF1A1A1E),
        cheekColor = Color(0x73FF69B4)
    ),
    GOLDEN_DRAGON(
        id = "golden",
        displayName = "Golden Dragon",
        headColor = Color(0xFFFFD700),
        bodyColor = Color(0xFFF39C12),
        tailColor = Color(0xFFD68910),
        accentColor = Color(0xFFFFF275),
        bellyColor = Color(0xFFFEF9E7),
        eyeColor = Color.White,
        pupilColor = Color(0xFF78281F),
        cheekColor = Color(0x66FF8C00)
    ),
    CYBER_NEON(
        id = "cyber",
        displayName = "Cyber Neon",
        headColor = Color(0xFF00F5FF),
        bodyColor = Color(0xFF00C5CD),
        tailColor = Color(0xFF009ACD),
        accentColor = Color(0xFFE000FF),
        bellyColor = Color(0xFFD4F8FF),
        eyeColor = Color.White,
        pupilColor = Color(0xFF1B0033),
        cheekColor = Color(0x73E000FF)
    ),
    CANDY_WORM(
        id = "candy",
        displayName = "Strawberry Candy",
        headColor = Color(0xFFFF69B4),
        bodyColor = Color(0xFFFF1493),
        tailColor = Color(0xFFC71585),
        accentColor = Color(0xFFFFB6C1),
        bellyColor = Color(0xFFFFF0F5),
        eyeColor = Color.White,
        pupilColor = Color(0xFF4A0023),
        cheekColor = Color(0x66FF1493)
    ),
    SHADOW_PHANTOM(
        id = "shadow",
        displayName = "Shadow Phantom",
        headColor = Color(0xFF34495E),
        bodyColor = Color(0xFF2C3E50),
        tailColor = Color(0xFF1A252F),
        accentColor = Color(0xFF9B59B6),
        bellyColor = Color(0xFFBDC3C7),
        eyeColor = Color(0xFFFF4136),
        pupilColor = Color(0xFF111111),
        cheekColor = Color(0x4D9B59B6)
    );

    companion object {
        fun fromId(id: String): SnakeSkin = values().firstOrNull { it.id == id } ?: EMERALD
    }
}

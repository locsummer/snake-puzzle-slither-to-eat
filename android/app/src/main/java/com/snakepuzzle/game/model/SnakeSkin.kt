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
    val throatColor: Color,
    val lipsColor: Color,
    val eyeColor: Color,
    val pupilColor: Color,
    val cheekColor: Color
) {
    EMERALD(
        id = "emerald",
        displayName = "Apple Worm (Classic)",
        headColor = Color(0xFF16A324),
        bodyColor = Color(0xFF149921),
        tailColor = Color(0xFF0F7A19),
        accentColor = Color(0xFF38C747),
        bellyColor = Color(0xFF92D41E),
        throatColor = Color(0xFF92D41E),
        lipsColor = Color(0xFFB3148B),
        eyeColor = Color.White,
        pupilColor = Color(0xFF111111),
        cheekColor = Color(0x66FF69B4)
    ),
    GOLDEN_DRAGON(
        id = "golden",
        displayName = "Golden Dragon",
        headColor = Color(0xFFFFD700),
        bodyColor = Color(0xFFF39C12),
        tailColor = Color(0xFFD68910),
        accentColor = Color(0xFFFFF275),
        bellyColor = Color(0xFFFEF9E7),
        throatColor = Color(0xFFFFF275),
        lipsColor = Color(0xFFC0392B),
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
        throatColor = Color(0xFF00F5FF),
        lipsColor = Color(0xFFE000FF),
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
        throatColor = Color(0xFFFFB6C1),
        lipsColor = Color(0xFF880E4F),
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
        throatColor = Color(0xFF9B59B6),
        lipsColor = Color(0xFF8E44AD),
        eyeColor = Color(0xFFFF4136),
        pupilColor = Color(0xFF111111),
        cheekColor = Color(0x4D9B59B6)
    );

    companion object {
        fun fromId(id: String): SnakeSkin = values().firstOrNull { it.id == id } ?: EMERALD
    }
}

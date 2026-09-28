package com.timworksports.crestedpass.ui.theme

import androidx.compose.ui.graphics.Color

val Black = Color(0xFF000000)
val White = Color(0xFFFFFFFF)
val DeepGreen = Color(0xFF0E3D2A)
val DeepGreenBright = Color(0xFF1F8A4C)
val Ink = White

val Navy = White
val Gold = DeepGreenBright
val AlertRed = Color(0xFFC8102E)
val Cream = White
val Border = Color(0xFF2A2A2A)
val Muted = Color(0xFF9A9A9A)
val SurfaceWhite = White
val StampGreen = DeepGreenBright
val LockedGray = Color(0xFF8A8A8A)

val CanvasDark = Black
val CardDark = Color(0xFF141414)
val InkDark = White
val MutedDark = Color(0xFF9A9A9A)
val BorderDark = Color(0xFF2A2A2A)

val CardLight = Color(0xFFF5F5F5)
val InkLight = Color(0xFF111111)
val MutedLight = Color(0xFF666666)
val BorderLight = Color(0xFFE4E4E4)

fun avatarColor(key: String): Color {
    val palette = listOf(
        Navy,
        Gold,
        AlertRed,
        StampGreen,
        Color(0xFF3D5A80),
        Color(0xFF6B3FA0),
        Color(0xFF8B5E3C)
    )
    return palette[kotlin.math.abs(key.hashCode()) % palette.size]
}

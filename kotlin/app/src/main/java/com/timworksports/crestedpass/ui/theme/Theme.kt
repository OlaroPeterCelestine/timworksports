package com.timworksports.crestedpass.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = DeepGreenBright,
    onPrimary = White,
    primaryContainer = Color(0xFFEEEEEE),
    onPrimaryContainer = InkLight,
    secondary = DeepGreenBright,
    onSecondary = White,
    secondaryContainer = Color(0xFFEEEEEE),
    onSecondaryContainer = InkLight,
    background = White,
    surfaceTint = Color.Transparent,
    onBackground = InkLight,
    surface = CardLight,
    onSurface = InkLight,
    surfaceVariant = Color(0xFFEEEEEE),
    onSurfaceVariant = MutedLight,
    error = AlertRed,
    onError = White,
    outline = BorderLight
)

private val DarkColors = darkColorScheme(
    primary = DeepGreenBright,
    onPrimary = White,
    primaryContainer = Color(0xFF1C1C1C),
    onPrimaryContainer = White,
    secondary = DeepGreenBright,
    onSecondary = White,
    secondaryContainer = Color(0xFF1C1C1C),
    onSecondaryContainer = White,
    background = Black,
    surfaceTint = Color.Transparent,
    onBackground = White,
    surface = CardDark,
    onSurface = White,
    surfaceVariant = Color(0xFF1C1C1C),
    onSurfaceVariant = MutedDark,
    error = AlertRed,
    onError = White,
    outline = BorderDark
)

enum class ThemeMode { System, Light, Dark }

val ThemeMode.label: String
    get() = when (this) {
        ThemeMode.System -> "System"
        ThemeMode.Light -> "Light"
        ThemeMode.Dark -> "Dark"
    }

fun ThemeMode.next(): ThemeMode = when (this) {
    ThemeMode.System -> ThemeMode.Light
    ThemeMode.Light -> ThemeMode.Dark
    ThemeMode.Dark -> ThemeMode.System
}

@Composable
fun CrestedPassTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colors.background.toArgb()
            window.navigationBarColor = colors.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }
    MaterialTheme(
        colorScheme = colors,
        typography = CrestedTypography,
        content = content
    )
}

package com.example.laptrinhmang.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Primary DAU Red/Burgundy theme colors
val DauRedPrimary = Color(0xFF8B0000)
val DauRedSecondary = Color(0xFFB22222)
val DauGoldAccent = Color(0xFFDAA520)
val BackgroundLight = Color(0xFFF8F9FA)
val SurfaceLight = Color(0xFFFFFFFF)

private val LightColorScheme = lightColorScheme(
    primary = DauRedPrimary,
    secondary = DauRedSecondary,
    tertiary = DauGoldAccent,
    background = BackgroundLight,
    surface = SurfaceLight,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFB4AB),
    secondary = Color(0xFFE5B9B5),
    tertiary = DauGoldAccent,
    background = Color(0xFF1C1B1F),
    surface = Color(0xFF2B292F)
)

@Composable
fun DauTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

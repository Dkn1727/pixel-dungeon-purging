package com.watabou.pixeldungeon.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = DungeonGold,
    onPrimary = DungeonDark,
    primaryContainer = DungeonSurfaceLight,
    onPrimaryContainer = DungeonGold,
    secondary = DungeonHealthRed,
    onSecondary = DungeonDark,
    secondaryContainer = DungeonHealthDark,
    onSecondaryContainer = DungeonParchment,
    tertiary = DungeonManaBlue,
    onTertiary = DungeonDark,
    background = DungeonDark,
    onBackground = DungeonText,
    surface = DungeonSurface,
    onSurface = DungeonText,
    surfaceVariant = DungeonSurfaceLight,
    onSurfaceVariant = DungeonParchmentDark,
    outline = DungeonBorder
)

@Composable
fun PixelDungeonTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = DungeonTypography,
        content = content
    )
}

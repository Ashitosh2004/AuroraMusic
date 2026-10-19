package com.zephyr.musicplayer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun AuroraTheme(
    themeMode: ThemeMode,
    accentColor: Color,
    content: @Composable () -> Unit
) {
    val resolved = when (themeMode) {
        ThemeMode.SYSTEM -> if (isSystemInDarkTheme()) ThemeMode.DARK else ThemeMode.LIGHT
        else -> themeMode
    }

    val colorScheme = when (resolved) {
        ThemeMode.LIGHT -> lightColorScheme(
            primary = accentColor,
            onPrimary = Color.White,
            primaryContainer = DaylightSurfaceTint,
            onPrimaryContainer = DaylightPrimary,
            secondary = accentColor.copy(alpha = 0.7f),
            onSecondary = Color.White,
            background = DaylightBackground,
            onBackground = DaylightOnSurface,
            surface = DaylightSurface,
            onSurface = DaylightOnSurface,
            surfaceVariant = DaylightSurfaceVariant,
            onSurfaceVariant = DaylightOnSurface.copy(alpha = 0.7f),
            surfaceTint = DaylightSurfaceTint,
            outline = DaylightOnSurface.copy(alpha = 0.25f)
        )
        ThemeMode.DARK -> darkColorScheme(
            primary = accentColor,
            onPrimary = Color.White,
            primaryContainer = MidnightSurfaceTint,
            onPrimaryContainer = MidnightPrimary,
            secondary = accentColor.copy(alpha = 0.7f),
            onSecondary = Color.White,
            background = MidnightBackground,
            onBackground = MidnightOnSurface,
            surface = MidnightSurface,
            onSurface = MidnightOnSurface,
            surfaceVariant = MidnightSurfaceVariant,
            onSurfaceVariant = MidnightOnSurface.copy(alpha = 0.7f),
            surfaceTint = MidnightSurfaceTint,
            outline = MidnightOnSurface.copy(alpha = 0.25f)
        )
        ThemeMode.AMOLED -> darkColorScheme(
            primary = accentColor,
            onPrimary = Color.White,
            primaryContainer = AmoledSurfaceTint,
            onPrimaryContainer = AmoledPrimary,
            secondary = accentColor.copy(alpha = 0.7f),
            onSecondary = Color.White,
            background = AmoledBackground,
            onBackground = AmoledOnSurface,
            surface = AmoledSurface,
            onSurface = AmoledOnSurface,
            surfaceVariant = AmoledSurfaceVariant,
            onSurfaceVariant = AmoledOnSurface.copy(alpha = 0.7f),
            surfaceTint = AmoledSurfaceTint,
            outline = AmoledOnSurface.copy(alpha = 0.2f)
        )
        ThemeMode.SEPIA -> lightColorScheme(
            primary = accentColor,
            onPrimary = Color.White,
            primaryContainer = SepiaSurfaceTint,
            onPrimaryContainer = SepiaPrimary,
            secondary = accentColor.copy(alpha = 0.7f),
            onSecondary = Color.White,
            background = SepiaBackground,
            onBackground = SepiaOnSurface,
            surface = SepiaSurface,
            onSurface = SepiaOnSurface,
            surfaceVariant = SepiaSurfaceVariant,
            onSurfaceVariant = SepiaOnSurface.copy(alpha = 0.7f),
            surfaceTint = SepiaSurfaceTint,
            outline = SepiaOnSurface.copy(alpha = 0.25f)
        )
        ThemeMode.ROSE -> lightColorScheme(
            primary = accentColor,
            onPrimary = Color.White,
            primaryContainer = RoseSurfaceTint,
            onPrimaryContainer = RosePrimary,
            secondary = accentColor.copy(alpha = 0.7f),
            onSecondary = Color.White,
            background = RoseBackground,
            onBackground = RoseOnSurface,
            surface = RoseSurface,
            onSurface = RoseOnSurface,
            surfaceVariant = RoseSurfaceVariant,
            onSurfaceVariant = RoseOnSurface.copy(alpha = 0.7f),
            surfaceTint = RoseSurfaceTint,
            outline = RoseOnSurface.copy(alpha = 0.25f)
        )
        ThemeMode.FOREST -> darkColorScheme(
            primary = accentColor,
            onPrimary = Color.White,
            primaryContainer = ForestSurfaceTint,
            onPrimaryContainer = ForestPrimary,
            secondary = accentColor.copy(alpha = 0.7f),
            onSecondary = Color.White,
            background = ForestBackground,
            onBackground = ForestOnSurface,
            surface = ForestSurface,
            onSurface = ForestOnSurface,
            surfaceVariant = ForestSurfaceVariant,
            onSurfaceVariant = ForestOnSurface.copy(alpha = 0.7f),
            surfaceTint = ForestSurfaceTint,
            outline = ForestOnSurface.copy(alpha = 0.25f)
        )
        ThemeMode.SYSTEM -> error("should not reach here")
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AuroraTypography,
        content = content
    )
}

private val AuroraTypography = Typography(
    displayLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 36.sp, letterSpacing = (-0.5).sp, lineHeight = 42.sp),
    displayMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 28.sp, letterSpacing = (-0.25).sp, lineHeight = 36.sp),
    headlineLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 26.sp, letterSpacing = (-0.25).sp, lineHeight = 32.sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 22.sp, lineHeight = 28.sp),
    headlineSmall = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 18.sp, lineHeight = 24.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 26.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp, letterSpacing = 0.1.sp, lineHeight = 22.sp),
    titleSmall = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp, letterSpacing = 0.1.sp, lineHeight = 20.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 22.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp, letterSpacing = 0.1.sp, lineHeight = 20.sp),
    labelMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 12.sp, letterSpacing = 0.5.sp, lineHeight = 16.sp),
    labelSmall = TextStyle(fontWeight = FontWeight.Medium, fontSize = 11.sp, letterSpacing = 0.5.sp, lineHeight = 16.sp)
)

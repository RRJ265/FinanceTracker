package com.sample.financetracker.ui.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Values come from the Vanguard Precision design tokens. Indigo #5146E5 is primaryContainer.
val VanguardColorScheme = lightColorScheme(
    primary = Color(0xFF3725CD),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF5146E5),
    onPrimaryContainer = Color(0xFFDBD8FF),
    inversePrimary = Color(0xFFC3C0FF),
    secondary = Color(0xFF594CC3),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFF9186FF),
    onSecondaryContainer = Color(0xFF270C92),
    tertiary = Color(0xFF005434),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFF006F46),
    onTertiaryContainer = Color(0xFF79F3B2),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF93000A),
    background = Color(0xFFFAF8FF),
    onBackground = Color(0xFF171B29),
    surface = Color(0xFFFAF8FF),
    onSurface = Color(0xFF171B29),
    surfaceVariant = Color(0xFFDEE2F5),
    onSurfaceVariant = Color(0xFF464555),
    surfaceTint = Color(0xFF4F43E3),
    inverseSurface = Color(0xFF2C303E),
    inverseOnSurface = Color(0xFFEEF0FF),
    outline = Color(0xFF777587),
    outlineVariant = Color(0xFFC7C4D8),
    surfaceDim = Color(0xFFD6D9EC),
    surfaceBright = Color(0xFFFAF8FF),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF2F3FF),
    surfaceContainer = Color(0xFFEAEDFF),
    surfaceContainerHigh = Color(0xFFE4E7FA),
    surfaceContainerHighest = Color(0xFFDEE2F5),
)

object Fixed {
    val primaryFixed = Color(0xFFE3DFFF)
    val primaryFixedDim = Color(0xFFC3C0FF)
    val onPrimaryFixed = Color(0xFF100069)
    val secondaryFixed = Color(0xFFE4DFFF)
    val tertiaryFixed = Color(0xFF80FAB9)
    val onTertiaryFixed = Color(0xFF002111)
}

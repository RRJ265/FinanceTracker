package com.sample.financetracker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

// Vanguard Precision is a light-only design system.
@Composable
fun FinanceTrackerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = VanguardColorScheme,
        typography = VanguardTypography,
        shapes = VanguardShapes,
        content = content,
    )
}

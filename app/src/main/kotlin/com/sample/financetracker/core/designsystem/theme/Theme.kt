package com.sample.financetracker.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun FinanceTrackerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = VanguardColorScheme,
        typography = VanguardTypography,
        shapes = VanguardShapes,
        content = content,
    )
}

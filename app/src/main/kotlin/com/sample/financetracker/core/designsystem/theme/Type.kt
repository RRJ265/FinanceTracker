package com.sample.financetracker.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.sample.financetracker.R

@OptIn(ExperimentalTextApi::class)
private fun interWeight(weight: FontWeight) = Font(
    resId = R.font.inter_variable,
    weight = weight,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)),
)

val Inter = FontFamily(
    interWeight(FontWeight.Normal),
    interWeight(FontWeight.Medium),
    interWeight(FontWeight.SemiBold),
    interWeight(FontWeight.Bold),
)

private const val TabularFigures = "tnum"

private fun style(
    size: Int,
    weight: FontWeight,
    lineHeight: Int,
    letterSpacing: Float = 0f,
) = TextStyle(
    fontFamily = Inter,
    fontSize = size.sp,
    fontWeight = weight,
    lineHeight = lineHeight.sp,
    letterSpacing = letterSpacing.em,
    fontFeatureSettings = TabularFigures,
)

private val Base = Typography()

val VanguardTypography = Typography(
    displayLarge = style(40, FontWeight.Bold, 48, -0.02f),
    displayMedium = Base.displayMedium.copy(fontFamily = Inter),
    displaySmall = Base.displaySmall.copy(fontFamily = Inter),
    headlineLarge = style(28, FontWeight.SemiBold, 36, -0.01f),
    headlineMedium = style(22, FontWeight.SemiBold, 28),
    headlineSmall = style(18, FontWeight.SemiBold, 24),
    titleLarge = Base.titleLarge.copy(fontFamily = Inter),
    titleMedium = style(16, FontWeight.SemiBold, 24),
    titleSmall = Base.titleSmall.copy(fontFamily = Inter),
    bodyLarge = style(16, FontWeight.Normal, 24),
    bodyMedium = style(14, FontWeight.Normal, 20),
    bodySmall = style(12, FontWeight.Normal, 16),
    labelLarge = style(14, FontWeight.SemiBold, 20, 0.01f),
    labelMedium = style(12, FontWeight.Medium, 16, 0.02f),
    labelSmall = style(10, FontWeight.SemiBold, 14, 0.04f),
)

val DisplayLargeMobile = style(32, FontWeight.Bold, 40, -0.02f)

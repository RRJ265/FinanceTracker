package com.sample.financetracker.core.designsystem.component

import com.sample.financetracker.core.designsystem.theme.Fixed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.sample.financetracker.domain.model.Transaction
import com.sample.financetracker.domain.model.TransactionType

fun categoryIcon(category: String): ImageVector = when (category) {
    "Food" -> Icons.Outlined.Restaurant
    "Shopping" -> Icons.Outlined.ShoppingCart
    "Travel" -> Icons.Outlined.DirectionsCar
    "Bills" -> Icons.Outlined.Receipt
    "Rent" -> Icons.Outlined.Home
    "Health" -> Icons.Outlined.LocalHospital
    "Salary", "Business" -> Icons.Outlined.Work
    "Investments" -> Icons.Outlined.TrendingUp
    "Gift" -> Icons.Outlined.CardGiftcard
    else -> Icons.Outlined.Category
}

fun badgeColors(transaction: Transaction, scheme: ColorScheme): Pair<Color, Color> = when {
    transaction.type == TransactionType.INCOME -> Fixed.tertiaryFixed to Fixed.onTertiaryFixed
    transaction.category == "Food" -> scheme.errorContainer.copy(alpha = 0.4f) to scheme.error
    transaction.category == "Shopping" -> Fixed.secondaryFixed to scheme.primary
    else -> scheme.surfaceContainer to scheme.secondary
}

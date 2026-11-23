package com.sample.financetracker.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

enum class Destination(val route: String, val label: String, val icon: ImageVector) {
    Home("home", "Home", Icons.Outlined.Dashboard),
    Ledger("ledger", "Ledger", Icons.AutoMirrored.Outlined.ReceiptLong),
    Budget("budget", "Budget", Icons.Outlined.PieChart),
    Profile("profile", "Profile", Icons.Outlined.Person),
}

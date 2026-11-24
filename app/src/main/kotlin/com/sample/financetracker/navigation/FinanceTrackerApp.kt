package com.sample.financetracker.navigation

import com.sample.financetracker.core.designsystem.theme.Fixed
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sample.financetracker.feature.budget.BudgetScreen
import com.sample.financetracker.feature.home.HomeScreen
import com.sample.financetracker.feature.ledger.LedgerScreen
import com.sample.financetracker.feature.profile.ProfileScreen

@Composable
fun FinanceTrackerApp() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val navigateTo = { destination: Destination ->
        navController.navigate(destination.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Column {
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainer)
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                    tonalElevation = 0.dp,
                ) {
                    Destination.entries.forEach { destination ->
                        NavigationBarItem(
                            selected = currentRoute == destination.route,
                            onClick = { navigateTo(destination) },
                            icon = { Icon(destination.icon, contentDescription = destination.label) },
                            label = { Text(destination.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Fixed.onPrimaryFixed,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = Fixed.primaryFixed,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destination.Home.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Destination.Home.route) {
                HomeScreen(
                    onViewAll = { navigateTo(Destination.Ledger) },
                    onOpenAnalytics = { navigateTo(Destination.Budget) },
                )
            }
            composable(Destination.Ledger.route) { LedgerScreen(snackbarHostState) }
            composable(Destination.Budget.route) { BudgetScreen() }
            composable(Destination.Profile.route) { ProfileScreen() }
        }
    }
}

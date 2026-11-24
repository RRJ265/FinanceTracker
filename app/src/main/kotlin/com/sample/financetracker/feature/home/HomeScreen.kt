package com.sample.financetracker.feature.home

import com.sample.financetracker.core.designsystem.component.badgeColors
import com.sample.financetracker.core.designsystem.component.categoryIcon
import com.sample.financetracker.core.designsystem.theme.Fixed
import com.sample.financetracker.core.designsystem.component.FinanceTopBar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.CallReceived
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.sample.financetracker.domain.model.Transaction
import com.sample.financetracker.domain.model.TransactionType
import com.sample.financetracker.core.designsystem.component.DonutChart
import com.sample.financetracker.core.designsystem.component.ErrorView
import com.sample.financetracker.core.designsystem.component.LoadingView
import com.sample.financetracker.feature.transaction.AddTransactionSheet
import com.sample.financetracker.core.designsystem.component.DonutSegment
import com.sample.financetracker.core.designsystem.component.FinanceCard
import com.sample.financetracker.core.designsystem.component.FinanceFab
import com.sample.financetracker.core.designsystem.component.Sparkline
import com.sample.financetracker.core.designsystem.theme.DisplayLargeMobile
import com.sample.financetracker.core.util.formatIndianDigits
import com.sample.financetracker.core.util.formatRecentDate
import com.sample.financetracker.core.util.formatRupeesAbbreviated
import com.sample.financetracker.core.util.formatRupeesTrimmed
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    onViewAll: () -> Unit,
    onOpenAnalytics: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var addType by remember { mutableStateOf<TransactionType?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            FinanceTopBar("Home Dashboard", state.userName)
            when {
                state.isLoading -> LoadingView()
                state.error != null -> ErrorView(state.error.orEmpty(), viewModel::retry)
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 112.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    item { GreetingRow(state, viewModel::selectMonth) }
                    item { BalanceHero(state) }
                    item {
                        QuickActions(
                            onSend = { addType = TransactionType.EXPENSE },
                            onRequest = { addType = TransactionType.INCOME },
                            onAddNote = { addType = TransactionType.EXPENSE },
                            onAnalytics = onOpenAnalytics,
                        )
                    }
                    item { IncomeExpenseRow(state) }
                    item { MonthlyBudgetCard(state) }
                    item { CategorySpendingCard(state) }
                    item { RecentTransactions(state.recent, onViewAll) }
                }
            }
        }

        FinanceFab(
            onClick = { addType = TransactionType.EXPENSE },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
        )
    }

    addType?.let { type ->
        AddTransactionSheet(initialType = type, onDismiss = { addType = null })
    }
}

@Composable
private fun GreetingRow(state: HomeUiState, onMonthSelected: (YearMonth) -> Unit) {
    val scheme = MaterialTheme.colorScheme
    var expanded by remember { mutableStateOf(false) }
    val monthFormat = remember { DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault()) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "${state.greeting}, ${state.userName} \uD83D\uDC4B",
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                "Here is your monthly summary",
                style = MaterialTheme.typography.bodyMedium,
                color = scheme.outline,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.width(8.dp))
        Box {
            Surface(
                onClick = { expanded = true },
                shape = CircleShape,
                color = scheme.surfaceContainerLow,
                shadowElevation = 1.dp,
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(Icons.Filled.CalendarToday, null, tint = scheme.primary, modifier = Modifier.size(18.dp))
                    Text(state.month.format(monthFormat), style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium))
                    Icon(Icons.Filled.ExpandMore, null, tint = scheme.outline, modifier = Modifier.size(18.dp))
                }
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                state.monthOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.format(monthFormat)) },
                        onClick = {
                            onMonthSelected(option)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun BalanceHero(state: HomeUiState) {
    val scheme = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(20.dp)
    var hidden by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = shape,
                ambientColor = scheme.primaryContainer.copy(alpha = 0.38f),
                spotColor = scheme.primaryContainer.copy(alpha = 0.38f),
            )
            .clip(shape)
            .background(Brush.linearGradient(listOf(scheme.primaryContainer, scheme.primary, scheme.secondary)))
            .drawBehind {
                val bubble = 88.dp.toPx()
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(Color.White.copy(alpha = 0.14f), Color.Transparent),
                        center = Offset(size.width + 40.dp.toPx() - bubble, size.height + 40.dp.toPx() - bubble),
                        radius = bubble * 1.4f,
                    ),
                    radius = bubble * 1.4f,
                    center = Offset(size.width + 40.dp.toPx() - bubble, size.height + 40.dp.toPx() - bubble),
                )
                val glow = 56.dp.toPx()
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(Fixed.primaryFixedDim.copy(alpha = 0.28f), Color.Transparent),
                        center = Offset(size.width - 16.dp.toPx() - glow, 16.dp.toPx() + glow),
                        radius = glow * 1.3f,
                    ),
                    radius = glow * 1.3f,
                    center = Offset(size.width - 16.dp.toPx() - glow, 16.dp.toPx() + glow),
                )
            }
            .padding(24.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "TOTAL NET BALANCE",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                        color = scheme.onPrimaryContainer,
                    )
                    Icon(
                        imageVector = if (hidden) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = if (hidden) "Show balance" else "Hide balance",
                        tint = scheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp).clip(CircleShape).clickable { hidden = !hidden },
                    )
                }
                Text(
                    "Personal AC",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 2.dp),
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    if (state.balancePaise < 0) "-₹" else "₹",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Normal),
                    color = scheme.onPrimaryContainer,
                    modifier = Modifier.alignByBaseline(),
                )
                Text(
                    if (hidden) "••••••" else formatIndianDigits(state.balancePaise),
                    style = DisplayLargeMobile,
                    color = Color.White,
                    modifier = Modifier.alignByBaseline(),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                val change = state.balanceChangePercent
                if (change != null) {
                    val positive = change >= 0
                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (positive) Fixed.tertiaryFixed else scheme.errorContainer)
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        val tint = if (positive) Fixed.onTertiaryFixed else scheme.onErrorContainer
                        Icon(
                            if (positive) Icons.Filled.TrendingUp else Icons.Filled.TrendingDown,
                            null,
                            tint = tint,
                            modifier = Modifier.size(15.dp),
                        )
                        Text(
                            "${signedPercent(change)} vs last month",
                            style = MaterialTheme.typography.labelSmall,
                            color = tint,
                        )
                    }
                } else {
                    Spacer(Modifier.width(1.dp))
                }
                Sparkline(color = Fixed.tertiaryFixed)
            }
        }
    }
}

@Composable
private fun QuickActions(onSend: () -> Unit, onRequest: () -> Unit, onAddNote: () -> Unit, onAnalytics: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        QuickAction("Send", Icons.AutoMirrored.Outlined.Send, onSend, Modifier.weight(1f))
        QuickAction("Request", Icons.AutoMirrored.Outlined.CallReceived, onRequest, Modifier.weight(1f))
        QuickAction("Add Note", Icons.Outlined.EditNote, onAddNote, Modifier.weight(1f))
        QuickAction("Analytics", Icons.Outlined.QueryStats, onAnalytics, Modifier.weight(1f))
    }
}

@Composable
private fun QuickAction(label: String, icon: ImageVector, onClick: () -> Unit, modifier: Modifier) {
    val scheme = MaterialTheme.colorScheme
    Column(
        modifier = modifier.clip(MaterialTheme.shapes.large).clickable(onClick = onClick).padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .shadow(1.dp, MaterialTheme.shapes.large)
                .clip(MaterialTheme.shapes.large)
                .background(scheme.surfaceContainerHigh),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = scheme.primary, modifier = Modifier.size(24.dp))
        }
        Text(label, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium))
    }
}

@Composable
private fun IncomeExpenseRow(state: HomeUiState) {
    val scheme = MaterialTheme.colorScheme
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        SummaryCard(
            title = "Income",
            value = formatRupeesTrimmed(state.incomePaise),
            valueColor = scheme.tertiary,
            caption = changeCaption(state.incomeChangePercent),
            badgeBackground = Fixed.tertiaryFixed,
            badgeIcon = Icons.Filled.ArrowDownward,
            badgeTint = Fixed.onTertiaryFixed,
            modifier = Modifier.weight(1f),
        )
        SummaryCard(
            title = "Expenses",
            value = formatRupeesTrimmed(state.expensePaise),
            valueColor = scheme.error,
            caption = changeCaption(state.expenseChangePercent),
            badgeBackground = scheme.errorContainer,
            badgeIcon = Icons.Filled.ArrowUpward,
            badgeTint = scheme.onErrorContainer,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun SummaryCard(
    title: String,
    value: String,
    valueColor: Color,
    caption: String,
    badgeBackground: Color,
    badgeIcon: ImageVector,
    badgeTint: Color,
    modifier: Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    FinanceCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(title, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium), color = scheme.outline)
            Box(
                modifier = Modifier.size(28.dp).clip(CircleShape).background(badgeBackground),
                contentAlignment = Alignment.Center,
            ) {
                Icon(badgeIcon, null, tint = badgeTint, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = valueColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(caption, style = MaterialTheme.typography.labelSmall, color = scheme.outline)
    }
}

@Composable
private fun MonthlyBudgetCard(state: HomeUiState) {
    val scheme = MaterialTheme.colorScheme
    val used = state.budgetUsedFraction
    val overBudget = used > 1f
    FinanceCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Outlined.AccountBalanceWallet, null, tint = scheme.primary, modifier = Modifier.size(20.dp))
                Text("Monthly Budget", style = MaterialTheme.typography.titleMedium)
            }
            Text(
                "${(used * 100).roundToInt()}% used",
                style = MaterialTheme.typography.labelSmall,
                color = if (overBudget) scheme.error else scheme.primary,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (overBudget) scheme.errorContainer else scheme.surfaceContainer)
                    .padding(horizontal = 8.dp, vertical = 2.dp),
            )
        }
        Spacer(Modifier.height(12.dp))
        Box(
            modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape).background(scheme.surfaceContainerHigh),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(used.coerceIn(0f, 1f))
                    .height(10.dp)
                    .clip(CircleShape)
                    .background(if (overBudget) scheme.error else scheme.primaryContainer),
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            BudgetFigure("Spent", formatRupeesTrimmed(state.expensePaise))
            BudgetFigure("Limit", formatRupeesTrimmed(state.budgetLimitPaise))
        }
    }
}

@Composable
private fun BudgetFigure(label: String, value: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
        Text(value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
    }
}

@Composable
private fun CategorySpendingCard(state: HomeUiState) {
    val scheme = MaterialTheme.colorScheme
    val palette = listOf(scheme.primary, scheme.secondaryContainer, scheme.tertiaryContainer, scheme.outlineVariant)
    val monthName = remember(state.month) {
        state.month.format(DateTimeFormatter.ofPattern("MMMM", Locale.getDefault()))
    }

    FinanceCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text("Category Spending", style = MaterialTheme.typography.titleMedium)
                Text("$monthName Distribution", style = MaterialTheme.typography.labelSmall, color = scheme.outline)
            }
            Box(modifier = Modifier.size(32.dp), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.Tune, contentDescription = "Filter", tint = scheme.outline, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
        ) {
            DonutChart(
                segments = state.categories.mapIndexed { index, share ->
                    DonutSegment(share.fraction, palette[index.coerceAtMost(palette.lastIndex)])
                },
                trackColor = scheme.surfaceContainer,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("TOTAL", style = MaterialTheme.typography.labelSmall, color = scheme.outline)
                    Text(
                        formatRupeesAbbreviated(state.expensePaise),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    )
                }
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (state.categories.isEmpty()) {
                    Text("No spending this month", style = MaterialTheme.typography.bodySmall, color = scheme.outline)
                }
                state.categories.forEachIndexed { index, share ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Box(
                                modifier = Modifier.size(10.dp).clip(CircleShape)
                                    .background(palette[index.coerceAtMost(palette.lastIndex)]),
                            )
                            Text(share.name, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        Text(
                            "${(share.fraction * 100).roundToInt()}%",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentTransactions(recent: List<Transaction>, onViewAll: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Recent Transactions", style = MaterialTheme.typography.headlineSmall)
                Box(
                    modifier = Modifier.size(20.dp).clip(CircleShape).background(scheme.surfaceContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        recent.size.toString(),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = scheme.primary,
                    )
                }
            }
            Row(
                modifier = Modifier.clip(MaterialTheme.shapes.small).clickable(onClick = onViewAll),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("View All", style = MaterialTheme.typography.labelLarge, color = scheme.primary)
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = scheme.primary, modifier = Modifier.size(16.dp))
            }
        }
        if (recent.isEmpty()) {
            Text(
                "No transactions yet. Tap + to add one.",
                style = MaterialTheme.typography.bodyMedium,
                color = scheme.outline,
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            )
        }
        recent.forEach { RecentTransactionRow(it) }
    }
}

@Composable
private fun RecentTransactionRow(transaction: Transaction) {
    val scheme = MaterialTheme.colorScheme
    val isIncome = transaction.type == TransactionType.INCOME
    val (badgeBackground, badgeTint) = badgeColors(transaction, scheme)
    val date = remember(transaction.dateMillis) { formatRecentDate(transaction.dateMillis) }

    FinanceCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(44.dp).clip(MaterialTheme.shapes.large).background(badgeBackground),
                contentAlignment = Alignment.Center,
            ) {
                Icon(categoryIcon(transaction.category), null, tint = badgeTint, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    transaction.note.ifBlank { transaction.category },
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    if (transaction.note.isBlank()) date else "${transaction.category} • $date",
                    style = MaterialTheme.typography.labelMedium,
                    color = scheme.outline,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                (if (isIncome) "+" else "-") + formatRupeesTrimmed(transaction.amountPaise),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = if (isIncome) scheme.tertiary else scheme.error,
                modifier = Modifier.padding(start = 8.dp),
            )
        }
    }
}

private fun signedPercent(value: Double): String =
    (if (value >= 0) "+" else "-") + String.format(Locale.US, "%.1f", abs(value)) + "%"

private fun changeCaption(change: Double?): String =
    if (change == null) "No prior month data" else "${signedPercent(change)} vs last mo"

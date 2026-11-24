package com.sample.financetracker.feature.ledger

import com.sample.financetracker.core.designsystem.component.badgeColors
import com.sample.financetracker.core.designsystem.component.categoryIcon
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.CallReceived
import androidx.compose.material.icons.automirrored.outlined.TrendingDown
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.automirrored.outlined.CallMade
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.sample.financetracker.domain.model.Transaction
import com.sample.financetracker.domain.model.TransactionType
import com.sample.financetracker.core.designsystem.component.ErrorView
import com.sample.financetracker.core.designsystem.component.FinanceCard
import com.sample.financetracker.core.designsystem.component.LoadingView
import com.sample.financetracker.core.ui.CollectMessages
import com.sample.financetracker.feature.transaction.AddTransactionSheet
import com.sample.financetracker.core.designsystem.component.FinanceFab
import com.sample.financetracker.core.designsystem.component.FinanceTopBar
import com.sample.financetracker.core.designsystem.theme.Fixed
import com.sample.financetracker.core.util.formatRupeesAbbreviated
import com.sample.financetracker.core.util.formatRupeesTrimmed
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun LedgerScreen(snackbarHostState: SnackbarHostState, viewModel: LedgerViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showSheet by remember { mutableStateOf(false) }
    CollectMessages(viewModel.messages, snackbarHostState)
    val monthName = remember(state.query.month) {
        state.query.month.format(DateTimeFormatter.ofPattern("MMMM", Locale.getDefault()))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            state.isLoading -> LoadingView()
            state.error != null -> ErrorView(state.error.orEmpty(), viewModel::retry)
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    item { FinanceTopBar("Transactions", state.userName) }
                    item {
                        Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            SearchBar(state.query.search, viewModel::setSearch)
                            FilterChips(state, viewModel::setFilter)
                            DropdownRow(state, viewModel::setMonth, viewModel::setCategory)
                            SummaryBanner(state)
                        }
                    }
                    items(state.groups, key = { it.date.toEpochDay() }) { group ->
                        DayGroupSection(group, onDelete = viewModel::delete)
                    }
                    item { EndOfList(state.groups.isEmpty(), monthName) }
                }
            }
        }

        FinanceFab(
            onClick = { showSheet = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
        )
    }

    if (showSheet) {
        AddTransactionSheet(onDismiss = { showSheet = false })
    }
}

@Composable
private fun SearchBar(value: String, onChange: (String) -> Unit) {
    val scheme = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = CircleShape,
        color = scheme.surface,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, scheme.surfaceContainer),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Outlined.Search, contentDescription = null, tint = scheme.outline)
            Spacer(Modifier.width(12.dp))
            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty()) {
                    Text(
                        "Search transactions, merchant…",
                        style = MaterialTheme.typography.bodyLarge,
                        color = scheme.outline,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onChange,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = scheme.onSurface),
                    cursorBrush = SolidColor(scheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (value.isNotEmpty()) {
                IconButton(onClick = { onChange("") }) {
                    Icon(Icons.Outlined.Close, contentDescription = "Clear search", tint = scheme.outline)
                }
            } else {
                Icon(Icons.Outlined.Tune, contentDescription = null, tint = scheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun FilterChips(state: LedgerUiState, onSelect: (LedgerFilter) -> Unit) {
    val scheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TypeChip(
            label = "All",
            icon = Icons.AutoMirrored.Outlined.Sort,
            amount = null,
            amountColor = Color.Unspecified,
            selected = state.query.filter == LedgerFilter.All,
            onClick = { onSelect(LedgerFilter.All) },
        )
        TypeChip(
            label = "Income",
            icon = Icons.AutoMirrored.Outlined.CallReceived,
            amount = "+" + formatRupeesAbbreviated(state.monthIncomePaise),
            amountColor = scheme.tertiary,
            selected = state.query.filter == LedgerFilter.Income,
            onClick = { onSelect(LedgerFilter.Income) },
        )
        TypeChip(
            label = "Expense",
            icon = Icons.AutoMirrored.Outlined.CallMade,
            amount = "-" + formatRupeesAbbreviated(state.monthExpensePaise),
            amountColor = scheme.error,
            selected = state.query.filter == LedgerFilter.Expense,
            onClick = { onSelect(LedgerFilter.Expense) },
        )
    }
}

@Composable
private fun TypeChip(
    label: String,
    icon: ImageVector,
    amount: String?,
    amountColor: Color,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = if (selected) scheme.primaryContainer else scheme.surfaceContainerLow,
        contentColor = if (selected) scheme.onPrimary else scheme.onSurface,
        border = if (selected) null else BorderStroke(1.dp, scheme.surfaceContainer),
        modifier = Modifier.height(40.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Text(label, style = MaterialTheme.typography.labelLarge)
            if (amount != null) {
                Text(
                    amount,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (selected) Fixed.tertiaryFixed else amountColor,
                )
            }
        }
    }
}

@Composable
private fun DropdownRow(
    state: LedgerUiState,
    onMonth: (YearMonth) -> Unit,
    onCategory: (String?) -> Unit,
) {
    val monthFormat = remember { DateTimeFormatter.ofPattern("MMM yyyy", Locale.getDefault()) }
    val thisMonth = remember { YearMonth.now() }
    val monthLabel = state.query.month.let {
        if (it == thisMonth) "This Month (${it.format(monthFormat)})" else it.format(monthFormat)
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        DropdownPill(monthLabel, Icons.Outlined.CalendarToday) { dismiss ->
            state.monthOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.format(monthFormat)) },
                    onClick = {
                        onMonth(option)
                        dismiss()
                    },
                )
            }
        }
        DropdownPill(state.query.category ?: "All Categories", null) { dismiss ->
            DropdownMenuItem(
                text = { Text("All Categories") },
                onClick = {
                    onCategory(null)
                    dismiss()
                },
            )
            state.categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category) },
                    onClick = {
                        onCategory(category)
                        dismiss()
                    },
                )
            }
        }
    }
}

@Composable
private fun DropdownPill(
    label: String,
    leadingIcon: ImageVector?,
    menu: @Composable (dismiss: () -> Unit) -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    var expanded by remember { mutableStateOf(false) }
    Box {
        Surface(
            onClick = { expanded = true },
            shape = MaterialTheme.shapes.medium,
            color = scheme.surface,
            shadowElevation = 1.dp,
            border = BorderStroke(1.dp, scheme.surfaceContainer),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (leadingIcon != null) {
                    Icon(leadingIcon, contentDescription = null, tint = scheme.primaryContainer, modifier = Modifier.size(18.dp))
                }
                Text(label, style = MaterialTheme.typography.labelLarge, maxLines = 1)
                Icon(Icons.Filled.ExpandMore, contentDescription = null, tint = scheme.outline, modifier = Modifier.size(18.dp))
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            menu { expanded = false }
        }
    }
}

@Composable
private fun SummaryBanner(state: LedgerUiState) {
    val scheme = MaterialTheme.colorScheme
    val income = state.query.filter == LedgerFilter.Income
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = Fixed.primaryFixed.copy(alpha = 0.55f),
        border = BorderStroke(1.dp, Fixed.primaryFixedDim.copy(alpha = 0.5f)),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(MaterialTheme.shapes.medium).background(Fixed.primaryFixedDim.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.AutoMirrored.Outlined.ShowChart, contentDescription = null, tint = scheme.primary)
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    (if (income) "TOTAL RECEIVED" else "TOTAL SPENT") + "  •  ${state.count} transaction" + if (state.count == 1) "" else "s",
                    style = MaterialTheme.typography.labelSmall,
                    color = scheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    formatRupeesTrimmed(state.totalPaise),
                    style = MaterialTheme.typography.headlineMedium,
                    maxLines = 1,
                )
            }
            state.changePercent?.let { ChangePill(it, goodWhenUp = income) }
        }
    }
}

@Composable
private fun ChangePill(percent: Double, goodWhenUp: Boolean) {
    val scheme = MaterialTheme.colorScheme
    val up = percent >= 0
    val good = up == goodWhenUp
    Surface(shape = CircleShape, color = scheme.surface) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(
                if (up) Icons.AutoMirrored.Outlined.TrendingUp else Icons.AutoMirrored.Outlined.TrendingDown,
                contentDescription = null,
                tint = if (good) scheme.tertiary else scheme.error,
                modifier = Modifier.size(16.dp),
            )
            Text(
                "${abs(percent).roundToInt()}% ${if (up) "more" else "less"}",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = if (good) scheme.tertiary else scheme.error,
            )
        }
    }
}

@Composable
private fun DayGroupSection(group: DayGroup, onDelete: (Transaction) -> Unit) {
    val scheme = MaterialTheme.colorScheme
    Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(dayHeading(group.date), style = MaterialTheme.typography.labelLarge, color = scheme.onSurfaceVariant)
            Text(
                "${group.items.size} item" + if (group.items.size == 1) "" else "s",
                style = MaterialTheme.typography.labelMedium,
                color = scheme.outline,
            )
        }
        FinanceCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(0.dp)) {
            group.items.forEachIndexed { index, transaction ->
                if (index > 0) {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = scheme.surfaceContainer)
                }
                key(transaction.id) { TransactionRow(transaction, onDelete = { onDelete(transaction) }) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransactionRow(transaction: Transaction, onDelete: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else {
                false
            }
        },
    )
    val isIncome = transaction.type == TransactionType.INCOME
    val (badgeBackground, badgeTint) = badgeColors(transaction, scheme)
    val time = remember(transaction.dateMillis) {
        Instant.ofEpochMilli(transaction.dateMillis).atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault()))
    }

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier.fillMaxSize().background(scheme.errorContainer).padding(end = 24.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = scheme.onErrorContainer)
            }
        },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().background(scheme.surface).padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(MaterialTheme.shapes.large).background(badgeBackground),
                contentAlignment = Alignment.Center,
            ) {
                Icon(categoryIcon(transaction.category), null, tint = badgeTint, modifier = Modifier.size(24.dp))
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
                    if (transaction.note.isBlank()) time else "${transaction.category} • $time",
                    style = MaterialTheme.typography.labelMedium,
                    color = scheme.outline,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(start = 8.dp)) {
                Text(
                    (if (isIncome) "+" else "-") + formatRupeesTrimmed(transaction.amountPaise),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isIncome) scheme.tertiary else scheme.error,
                )
                Text(
                    if (isIncome) "Credit" else "Debit",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isIncome) scheme.tertiary else scheme.outline,
                )
            }
        }
    }
}

@Composable
private fun EndOfList(empty: Boolean, monthName: String) {
    val scheme = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .clip(MaterialTheme.shapes.large)
            .background(scheme.surfaceContainerLow)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.size(48.dp).clip(CircleShape).background(scheme.surfaceContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = scheme.outline)
        }
        Spacer(Modifier.height(12.dp))
        Text(
            if (empty) "No transactions found" else "You're completely up to date!",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
        Text(
            if (empty) "Nothing matches these filters for $monthName. Tap + to add one." else "No earlier transactions recorded for $monthName. Enjoy financial peace of mind.",
            style = MaterialTheme.typography.bodyMedium,
            color = scheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

private fun dayHeading(date: LocalDate, today: LocalDate = LocalDate.now()): String {
    val locale = Locale.getDefault()
    val short = date.format(DateTimeFormatter.ofPattern("d MMM", locale))
    return when {
        date == today -> "TODAY, $short"
        date == today.minusDays(1) -> "YESTERDAY, $short"
        date.year == today.year -> short
        else -> date.format(DateTimeFormatter.ofPattern("d MMM yyyy", locale))
    }.uppercase(locale)
}

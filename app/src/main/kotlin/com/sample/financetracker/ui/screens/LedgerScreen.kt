package com.sample.financetracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sample.financetracker.data.Transaction
import com.sample.financetracker.data.TransactionType
import com.sample.financetracker.ui.components.FinanceCard
import com.sample.financetracker.ui.components.FinanceChip
import com.sample.financetracker.ui.components.FinanceFab
import com.sample.financetracker.util.formatRupees
import com.sample.financetracker.util.formatRupeesCompact
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LedgerScreen(viewModel: LedgerViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showSheet by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().statusBarsPadding(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Text("Ledger", style = MaterialTheme.typography.headlineLarge)
                Box(modifier = Modifier.padding(top = 16.dp))
                SummaryCard(state)
            }
            item {
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    LedgerFilter.entries.forEach { filter ->
                        FinanceChip(filter.label, state.filter == filter, { viewModel.setFilter(filter) })
                    }
                }
            }
            if (state.transactions.isEmpty()) {
                item {
                    Text(
                        text = "No transactions yet. Tap + to add one.",
                        modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            items(state.transactions, key = { it.id }) { transaction ->
                TransactionRow(transaction, onDelete = { viewModel.delete(transaction) })
            }
        }

        FinanceFab(
            onClick = { showSheet = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
        )
    }

    if (showSheet) {
        AddTransactionSheet(
            onDismiss = { showSheet = false },
            onSave = {
                viewModel.add(it)
                showSheet = false
            },
        )
    }
}

@Composable
private fun SummaryCard(state: LedgerUiState) {
    FinanceCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            "Balance",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            formatRupees(state.balancePaise),
            style = MaterialTheme.typography.headlineLarge,
            color = if (state.balancePaise < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
        )
        Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
            SummaryFigure("Income", formatRupeesCompact(state.incomePaise), MaterialTheme.colorScheme.tertiary, Modifier.weight(1f))
            SummaryFigure("Expense", formatRupeesCompact(state.expensePaise), MaterialTheme.colorScheme.error, Modifier.weight(1f))
        }
    }
}

@Composable
private fun SummaryFigure(label: String, value: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier) {
    Column(modifier) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.titleMedium, color = color)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransactionRow(transaction: Transaction, onDelete: () -> Unit) {
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
    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.large)
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(end = 24.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.onErrorContainer)
            }
        },
    ) {
        FinanceCard(modifier = Modifier.fillMaxWidth()) {
            val isIncome = transaction.type == TransactionType.INCOME
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(transaction.category, style = MaterialTheme.typography.titleMedium)
                    val subtitle = listOf(formatDate(transaction.dateMillis), transaction.note)
                        .filter { it.isNotBlank() }
                        .joinToString(" · ")
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(
                    text = (if (isIncome) "+" else "-") + formatRupees(transaction.amountPaise),
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isIncome) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

private fun formatDate(millis: Long): String =
    SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(millis))

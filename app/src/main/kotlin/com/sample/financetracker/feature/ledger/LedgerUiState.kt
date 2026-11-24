package com.sample.financetracker.feature.ledger

import com.sample.financetracker.core.util.percentChange
import com.sample.financetracker.domain.model.MonthTransactions
import com.sample.financetracker.domain.model.Transaction
import com.sample.financetracker.domain.model.TransactionType
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

enum class LedgerFilter(val label: String, val type: TransactionType?) {
    All("All", null),
    Income("Income", TransactionType.INCOME),
    Expense("Expense", TransactionType.EXPENSE),
}

data class LedgerQuery(
    val filter: LedgerFilter = LedgerFilter.All,
    val month: YearMonth = YearMonth.now(),
    val category: String? = null,
    val search: String = "",
)

data class DayGroup(val date: LocalDate, val items: List<Transaction>)

data class LedgerUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val userName: String = "",
    val query: LedgerQuery = LedgerQuery(),
    val monthOptions: List<YearMonth> = emptyList(),
    val categories: List<String> = emptyList(),
    val monthIncomePaise: Long = 0,
    val monthExpensePaise: Long = 0,
    val totalPaise: Long = 0,
    val count: Int = 0,
    val changePercent: Double? = null,
    val groups: List<DayGroup> = emptyList(),
)

private const val MONTH_OPTION_COUNT = 12

fun buildLedgerUiState(
    data: MonthTransactions,
    query: LedgerQuery,
    userName: String,
    zone: ZoneId = ZoneId.systemDefault(),
    currentMonth: YearMonth = YearMonth.now(zone),
): LedgerUiState {
    fun Transaction.date() = Instant.ofEpochMilli(dateMillis).atZone(zone).toLocalDate()
    fun Transaction.inScope() =
        (query.filter.type == null || type == query.filter.type) &&
            (query.category == null || category == query.category)

    val needle = query.search.trim()
    val scoped = data.current.filter { it.inScope() }
    val visible = if (needle.isEmpty()) {
        scoped
    } else {
        scoped.filter { it.category.contains(needle, ignoreCase = true) || it.note.contains(needle, ignoreCase = true) }
    }
    val summed = if (query.filter == LedgerFilter.Income) TransactionType.INCOME else TransactionType.EXPENSE
    fun List<Transaction>.total() = filter { it.type == summed }.sumOf { it.amountPaise }
    val previousTotal = data.previous.filter { it.inScope() }.total()

    return LedgerUiState(
        isLoading = false,
        userName = userName,
        query = query,
        monthOptions = (0 until MONTH_OPTION_COUNT).map { currentMonth.minusMonths(it.toLong()) },
        categories = data.current.map { it.category }.distinct().sorted(),
        monthIncomePaise = data.current.filter { it.type == TransactionType.INCOME }.sumOf { it.amountPaise },
        monthExpensePaise = data.current.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amountPaise },
        totalPaise = visible.total(),
        count = visible.size,
        changePercent = percentChange(visible.total(), previousTotal),
        groups = visible.groupBy { it.date() }
            .toSortedMap(compareByDescending { it })
            .map { (date, items) -> DayGroup(date, items) },
    )
}

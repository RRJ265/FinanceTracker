package com.sample.financetracker.feature.home

import com.sample.financetracker.core.util.percentChange
import com.sample.financetracker.domain.model.HomeSummary
import com.sample.financetracker.domain.model.Transaction
import com.sample.financetracker.domain.model.TransactionType
import java.time.YearMonth
import java.time.ZonedDateTime

data class CategoryShare(val name: String, val paise: Long, val fraction: Float)

data class HomeUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val greeting: String = "Hello",
    val userName: String = "",
    val month: YearMonth = YearMonth.now(),
    val monthOptions: List<YearMonth> = emptyList(),
    val balancePaise: Long = 0,
    val balanceChangePercent: Double? = null,
    val incomePaise: Long = 0,
    val incomeChangePercent: Double? = null,
    val expensePaise: Long = 0,
    val expenseChangePercent: Double? = null,
    val budgetLimitPaise: Long = 0,
    val categories: List<CategoryShare> = emptyList(),
    val recent: List<Transaction> = emptyList(),
) {
    val budgetUsedFraction: Float
        get() = if (budgetLimitPaise <= 0) 0f else (expensePaise.toDouble() / budgetLimitPaise).toFloat()
}

private const val MAX_CATEGORY_SLICES = 3
private const val MONTH_OPTION_COUNT = 12

fun buildHomeUiState(summary: HomeSummary, now: ZonedDateTime = ZonedDateTime.now()): HomeUiState {
    fun List<Transaction>.total(type: TransactionType) = filter { it.type == type }.sumOf { it.amountPaise }

    val thisMonth = summary.thisMonth
    val income = thisMonth.total(TransactionType.INCOME)
    val expense = thisMonth.total(TransactionType.EXPENSE)

    val byCategory = thisMonth
        .filter { it.type == TransactionType.EXPENSE }
        .groupBy { it.category }
        .mapValues { (_, items) -> items.sumOf { it.amountPaise } }
        .entries.sortedByDescending { it.value }
    val slices = byCategory.take(MAX_CATEGORY_SLICES).map { it.key to it.value }.toMutableList()
    val others = byCategory.drop(MAX_CATEGORY_SLICES).sumOf { it.value }
    if (others > 0) slices += "Others" to others

    return HomeUiState(
        isLoading = false,
        greeting = when (now.hour) {
            in 0..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            else -> "Good evening"
        },
        userName = summary.userName,
        month = summary.month,
        monthOptions = (0 until MONTH_OPTION_COUNT).map { YearMonth.from(now).minusMonths(it.toLong()) },
        balancePaise = summary.balancePaise,
        balanceChangePercent = percentChange(summary.balancePaise, summary.previousBalancePaise),
        incomePaise = income,
        incomeChangePercent = percentChange(income, summary.lastMonth.total(TransactionType.INCOME)),
        expensePaise = expense,
        expenseChangePercent = percentChange(expense, summary.lastMonth.total(TransactionType.EXPENSE)),
        budgetLimitPaise = summary.budgetLimitPaise,
        categories = slices.map { (name, paise) -> CategoryShare(name, paise, paise.toFloat() / expense) },
        recent = summary.recent,
    )
}

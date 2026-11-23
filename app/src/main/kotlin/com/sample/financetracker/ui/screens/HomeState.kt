package com.sample.financetracker.ui.screens

import com.sample.financetracker.data.Transaction
import com.sample.financetracker.data.TransactionType
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZonedDateTime

data class CategoryShare(val name: String, val paise: Long, val fraction: Float)

data class HomeUiState(
    val greeting: String = "Hello",
    val userName: String = DEFAULT_USER_NAME,
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

const val DEFAULT_USER_NAME = "Priya"
private const val MAX_CATEGORY_SLICES = 3
private const val RECENT_COUNT = 4
private const val MONTH_OPTION_COUNT = 12

fun buildHomeState(
    transactions: List<Transaction>,
    month: YearMonth,
    budgetLimitPaise: Long,
    now: ZonedDateTime = ZonedDateTime.now(),
    zone: ZoneId = ZoneId.systemDefault(),
    userName: String = DEFAULT_USER_NAME,
): HomeUiState {
    fun Transaction.yearMonth() = YearMonth.from(Instant.ofEpochMilli(dateMillis).atZone(zone))
    fun Transaction.signed() = if (type == TransactionType.INCOME) amountPaise else -amountPaise
    fun List<Transaction>.total(type: TransactionType) = filter { it.type == type }.sumOf { it.amountPaise }

    val previous = month.minusMonths(1)
    val thisMonth = transactions.filter { it.yearMonth() == month }
    val lastMonth = transactions.filter { it.yearMonth() == previous }

    val balance = transactions.filter { it.yearMonth() <= month }.sumOf { it.signed() }
    val previousBalance = transactions.filter { it.yearMonth() <= previous }.sumOf { it.signed() }

    val expenseByCategory = thisMonth
        .filter { it.type == TransactionType.EXPENSE }
        .groupBy { it.category }
        .mapValues { (_, items) -> items.sumOf { it.amountPaise } }
        .entries.sortedByDescending { it.value }
    val totalExpense = thisMonth.total(TransactionType.EXPENSE)
    val slices = expenseByCategory.take(MAX_CATEGORY_SLICES).map { it.key to it.value }.toMutableList()
    val others = expenseByCategory.drop(MAX_CATEGORY_SLICES).sumOf { it.value }
    if (others > 0) slices += "Others" to others

    return HomeUiState(
        greeting = when (now.hour) {
            in 0..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            else -> "Good evening"
        },
        userName = userName,
        month = month,
        monthOptions = (0 until MONTH_OPTION_COUNT).map { YearMonth.from(now).minusMonths(it.toLong()) },
        balancePaise = balance,
        balanceChangePercent = percentChange(balance, previousBalance),
        incomePaise = thisMonth.total(TransactionType.INCOME),
        incomeChangePercent = percentChange(thisMonth.total(TransactionType.INCOME), lastMonth.total(TransactionType.INCOME)),
        expensePaise = totalExpense,
        expenseChangePercent = percentChange(totalExpense, lastMonth.total(TransactionType.EXPENSE)),
        budgetLimitPaise = budgetLimitPaise,
        categories = slices.map { (name, paise) -> CategoryShare(name, paise, paise.toFloat() / totalExpense) },
        recent = transactions.sortedWith(compareByDescending<Transaction> { it.dateMillis }.thenByDescending { it.id })
            .take(RECENT_COUNT),
    )
}

/** Percentage change from [previous] to [current], or null when there is no positive baseline. */
fun percentChange(current: Long, previous: Long): Double? =
    if (previous <= 0) null else (current - previous) * 100.0 / previous

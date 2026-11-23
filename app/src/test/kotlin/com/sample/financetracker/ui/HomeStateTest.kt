package com.sample.financetracker.ui

import com.sample.financetracker.data.Transaction
import com.sample.financetracker.data.TransactionType
import com.sample.financetracker.ui.screens.buildHomeState
import com.sample.financetracker.ui.screens.percentChange
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneOffset
import java.time.ZonedDateTime

class HomeStateTest {
    private val zone = ZoneOffset.UTC
    private val now = ZonedDateTime.of(2024, 10, 24, 20, 0, 0, 0, zone)
    private val october = YearMonth.of(2024, 10)

    private var nextId = 1L

    private fun tx(type: TransactionType, rupees: Long, category: String, date: LocalDate) = Transaction(
        id = nextId++,
        type = type,
        amountPaise = rupees * 100,
        category = category,
        note = "",
        dateMillis = date.atStartOfDay(zone).toInstant().toEpochMilli(),
    )

    private val data = listOf(
        tx(TransactionType.INCOME, 100_000, "Salary", LocalDate.of(2024, 9, 1)),
        tx(TransactionType.EXPENSE, 20_000, "Rent", LocalDate.of(2024, 9, 5)),
        tx(TransactionType.INCOME, 120_000, "Salary", LocalDate.of(2024, 10, 1)),
        tx(TransactionType.EXPENSE, 13_000, "Food", LocalDate.of(2024, 10, 3)),
        tx(TransactionType.EXPENSE, 8_000, "Shopping", LocalDate.of(2024, 10, 8)),
        tx(TransactionType.EXPENSE, 7_000, "Bills", LocalDate.of(2024, 10, 9)),
        tx(TransactionType.EXPENSE, 4_000, "Travel", LocalDate.of(2024, 10, 12)),
        tx(TransactionType.EXPENSE, 2_000, "Health", LocalDate.of(2024, 10, 14)),
    )

    private val state = buildHomeState(data, october, budgetLimitPaise = 5_000_000, now = now, zone = zone)

    @Test
    fun totalsForSelectedMonth() {
        assertEquals(120_000_00L, state.incomePaise)
        assertEquals(34_000_00L, state.expensePaise)
        assertEquals(0.68f, state.budgetUsedFraction, 0.001f)
    }

    @Test
    fun balanceIsCumulativeAndComparedWithPreviousMonthEnd() {
        assertEquals(166_000_00L, state.balancePaise)
        // 80,000 at end of September -> 166,000 now
        assertEquals(107.5, state.balanceChangePercent!!, 0.001)
    }

    @Test
    fun changeVsLastMonthForIncomeAndExpenses() {
        assertEquals(20.0, state.incomeChangePercent!!, 0.001)
        assertEquals(70.0, state.expenseChangePercent!!, 0.001)
    }

    @Test
    fun groupsTopThreeCategoriesAndOthers() {
        assertEquals(listOf("Food", "Shopping", "Bills", "Others"), state.categories.map { it.name })
        assertEquals(6_000_00L, state.categories.last().paise)
        assertEquals(1.0f, state.categories.sumOf { it.fraction.toDouble() }.toFloat(), 0.001f)
    }

    @Test
    fun recentIsNewestFirstAndLimitedToFour() {
        assertEquals(listOf("Health", "Travel", "Bills", "Shopping"), state.recent.map { it.category })
    }

    @Test
    fun offersTwelveMonthsAndGreetsByHour() {
        assertEquals(12, state.monthOptions.size)
        assertEquals(october, state.monthOptions.first())
        assertEquals("Good evening", state.greeting)
    }

    @Test
    fun percentChangeNeedsPositiveBaseline() {
        assertNull(percentChange(100, 0))
        assertNull(percentChange(100, -5))
        assertEquals(-50.0, percentChange(50, 100)!!, 0.0001)
    }
}

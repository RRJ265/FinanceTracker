package com.sample.financetracker.feature.ledger

import com.sample.financetracker.domain.model.MonthTransactions
import com.sample.financetracker.domain.model.Transaction
import com.sample.financetracker.domain.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneOffset

class LedgerUiStateTest {
    private val zone = ZoneOffset.UTC
    private val october = YearMonth.of(2024, 10)
    private var nextId = 1L

    private fun tx(type: TransactionType, rupees: Long, category: String, date: LocalDate, note: String = "") =
        Transaction(
            id = nextId++,
            type = type,
            amountPaise = rupees * 100,
            category = category,
            note = note,
            dateMillis = date.atStartOfDay(zone).toInstant().toEpochMilli(),
        )

    private val data = MonthTransactions(
        month = october,
        previous = listOf(tx(TransactionType.EXPENSE, 1_000, "Food", LocalDate.of(2024, 9, 10))),
        current = listOf(
            tx(TransactionType.EXPENSE, 400, "Food", LocalDate.of(2024, 10, 24), "Starbucks"),
            tx(TransactionType.EXPENSE, 600, "Food", LocalDate.of(2024, 10, 24), "Swiggy"),
            tx(TransactionType.INCOME, 35_000, "Salary", LocalDate.of(2024, 10, 23), "Wire"),
            tx(TransactionType.EXPENSE, 400, "Travel", LocalDate.of(2024, 10, 20)),
        ),
    )

    private fun build(query: LedgerQuery) = buildLedgerUiState(data, query, "Priya", zone, october)

    @Test
    fun groupsByDayNewestFirstAndSumsSpend() {
        val state = build(LedgerQuery(month = october))
        assertEquals(listOf(24, 23, 20), state.groups.map { it.date.dayOfMonth })
        assertEquals(1_400_00L, state.totalPaise)
        assertEquals(4, state.count)
        assertEquals(35_000_00L, state.monthIncomePaise)
        assertEquals(40.0, state.changePercent!!, 0.001)
    }

    @Test
    fun filtersByTypeCategoryAndSearch() {
        assertEquals(1, build(LedgerQuery(LedgerFilter.Income, october)).count)
        assertEquals(2, build(LedgerQuery(month = october, category = "Food")).count)
        assertEquals(1, build(LedgerQuery(month = october, search = "swig")).count)
        assertEquals(listOf("Food", "Salary", "Travel"), build(LedgerQuery(month = october)).categories)
    }

    @Test
    fun changeIsNullWithoutPreviousSpend() {
        assertNull(build(LedgerQuery(LedgerFilter.Income, october)).changePercent)
    }
}

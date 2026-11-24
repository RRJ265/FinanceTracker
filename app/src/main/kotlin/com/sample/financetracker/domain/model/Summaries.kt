package com.sample.financetracker.domain.model

import java.time.YearMonth

data class HomeSummary(
    val month: YearMonth,
    val userName: String,
    val budgetLimitPaise: Long,
    val thisMonth: List<Transaction>,
    val lastMonth: List<Transaction>,
    val balancePaise: Long,
    val previousBalancePaise: Long,
    val recent: List<Transaction>,
)

data class MonthTransactions(
    val month: YearMonth,
    val current: List<Transaction>,
    val previous: List<Transaction>,
)

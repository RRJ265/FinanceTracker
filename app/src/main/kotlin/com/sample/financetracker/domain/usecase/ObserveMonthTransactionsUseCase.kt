package com.sample.financetracker.domain.usecase

import com.sample.financetracker.core.util.endMillis
import com.sample.financetracker.core.util.startMillis
import com.sample.financetracker.domain.model.MonthTransactions
import com.sample.financetracker.domain.repository.TransactionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import java.time.YearMonth
import javax.inject.Inject

class ObserveMonthTransactionsUseCase @Inject constructor(
    private val repository: TransactionRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(month: Flow<YearMonth>): Flow<MonthTransactions> = month.flatMapLatest { selected ->
        val previous = selected.minusMonths(1)
        combine(
            repository.observeBetween(selected.startMillis(), selected.endMillis()),
            repository.observeBetween(previous.startMillis(), previous.endMillis()),
        ) { current, last -> MonthTransactions(selected, current, last) }
    }
}

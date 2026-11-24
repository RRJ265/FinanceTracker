package com.sample.financetracker.domain.usecase

import com.sample.financetracker.core.util.endMillis
import com.sample.financetracker.core.util.startMillis
import com.sample.financetracker.domain.model.HomeSummary
import com.sample.financetracker.domain.repository.SettingsRepository
import com.sample.financetracker.domain.repository.TransactionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import java.time.YearMonth
import javax.inject.Inject

private const val RECENT_COUNT = 4

class ObserveHomeSummaryUseCase @Inject constructor(
    private val transactions: TransactionRepository,
    private val settings: SettingsRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(month: Flow<YearMonth>): Flow<HomeSummary> = month.flatMapLatest { selected ->
        val previous = selected.minusMonths(1)
        val months = combine(
            transactions.observeBetween(selected.startMillis(), selected.endMillis()),
            transactions.observeBetween(previous.startMillis(), previous.endMillis()),
            ::Pair,
        )
        val balances = combine(
            transactions.observeNetBalanceBefore(selected.endMillis()),
            transactions.observeNetBalanceBefore(selected.startMillis()),
            ::Pair,
        )
        val preferences = combine(settings.userName, settings.monthlyBudgetPaise, ::Pair)

        combine(
            months,
            balances,
            transactions.observeRecent(RECENT_COUNT),
            preferences,
        ) { (thisMonth, lastMonth), (balance, previousBalance), recent, (name, budget) ->
            HomeSummary(
                month = selected,
                userName = name,
                budgetLimitPaise = budget,
                thisMonth = thisMonth,
                lastMonth = lastMonth,
                balancePaise = balance,
                previousBalancePaise = previousBalance,
                recent = recent,
            )
        }
    }
}

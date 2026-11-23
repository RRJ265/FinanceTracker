package com.sample.financetracker.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sample.financetracker.data.FinanceDatabase
import com.sample.financetracker.data.Transaction
import com.sample.financetracker.data.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class LedgerFilter(val label: String, val type: TransactionType?) {
    All("All", null),
    Income("Income", TransactionType.INCOME),
    Expense("Expense", TransactionType.EXPENSE),
}

data class LedgerUiState(
    val transactions: List<Transaction> = emptyList(),
    val filter: LedgerFilter = LedgerFilter.All,
    val incomePaise: Long = 0,
    val expensePaise: Long = 0,
) {
    val balancePaise: Long get() = incomePaise - expensePaise
}

class LedgerViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = FinanceDatabase.get(application).transactionDao()
    private val filter = MutableStateFlow(LedgerFilter.All)

    val uiState: StateFlow<LedgerUiState> = combine(dao.observeAll(), filter) { all, selected ->
        LedgerUiState(
            transactions = all.filter { selected.type == null || it.type == selected.type },
            filter = selected,
            incomePaise = all.filter { it.type == TransactionType.INCOME }.sumOf { it.amountPaise },
            expensePaise = all.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amountPaise },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LedgerUiState())

    fun setFilter(value: LedgerFilter) {
        filter.value = value
    }

    fun add(transaction: Transaction) {
        viewModelScope.launch { dao.insert(transaction) }
    }

    fun delete(transaction: Transaction) {
        viewModelScope.launch { dao.delete(transaction) }
    }
}

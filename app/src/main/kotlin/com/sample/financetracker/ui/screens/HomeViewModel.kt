package com.sample.financetracker.ui.screens

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sample.financetracker.data.FinanceDatabase
import com.sample.financetracker.data.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.YearMonth

private const val PREFS_NAME = "finance_prefs"
private const val KEY_MONTHLY_BUDGET_PAISE = "monthly_budget_paise"
private const val DEFAULT_MONTHLY_BUDGET_PAISE = 5_000_000L

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = FinanceDatabase.get(application).transactionDao()
    private val prefs = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val month = MutableStateFlow(YearMonth.now())

    val uiState: StateFlow<HomeUiState> = combine(dao.observeAll(), month) { all, selected ->
        buildHomeState(all, selected, prefs.getLong(KEY_MONTHLY_BUDGET_PAISE, DEFAULT_MONTHLY_BUDGET_PAISE))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun selectMonth(value: YearMonth) {
        month.value = value
    }

    fun add(transaction: Transaction) {
        viewModelScope.launch { dao.insert(transaction) }
    }
}

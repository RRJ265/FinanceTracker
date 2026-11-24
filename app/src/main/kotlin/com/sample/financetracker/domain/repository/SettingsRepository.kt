package com.sample.financetracker.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val userName: Flow<String>
    val monthlyBudgetPaise: Flow<Long>

    suspend fun setMonthlyBudget(paise: Long)
}

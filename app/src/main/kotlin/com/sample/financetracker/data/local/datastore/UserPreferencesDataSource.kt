package com.sample.financetracker.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

const val DEFAULT_USER_NAME = "Priya"
const val DEFAULT_MONTHLY_BUDGET_PAISE = 5_000_000L

class UserPreferencesDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    val userName: Flow<String> = dataStore.data.map { it[USER_NAME] ?: DEFAULT_USER_NAME }
    val monthlyBudgetPaise: Flow<Long> = dataStore.data.map { it[MONTHLY_BUDGET] ?: DEFAULT_MONTHLY_BUDGET_PAISE }

    suspend fun setMonthlyBudget(paise: Long) {
        dataStore.edit { it[MONTHLY_BUDGET] = paise }
    }

    private companion object {
        val USER_NAME = stringPreferencesKey("user_name")
        val MONTHLY_BUDGET = longPreferencesKey("monthly_budget_paise")
    }
}

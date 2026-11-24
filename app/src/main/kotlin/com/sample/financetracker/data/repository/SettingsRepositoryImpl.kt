package com.sample.financetracker.data.repository

import com.sample.financetracker.core.common.toAppError
import com.sample.financetracker.data.local.datastore.UserPreferencesDataSource
import com.sample.financetracker.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlin.coroutines.cancellation.CancellationException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val source: UserPreferencesDataSource,
) : SettingsRepository {
    override val userName: Flow<String> = source.userName.catch { throw it.toAppError() }
    override val monthlyBudgetPaise: Flow<Long> = source.monthlyBudgetPaise.catch { throw it.toAppError() }

    override suspend fun setMonthlyBudget(paise: Long) {
        try {
            source.setMonthlyBudget(paise)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            throw e.toAppError()
        }
    }
}

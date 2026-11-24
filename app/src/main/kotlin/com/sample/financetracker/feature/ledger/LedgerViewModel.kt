package com.sample.financetracker.feature.ledger

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.financetracker.core.common.DefaultDispatcher
import com.sample.financetracker.core.common.toAppError
import com.sample.financetracker.core.ui.UiMessage
import com.sample.financetracker.domain.model.Transaction
import com.sample.financetracker.domain.usecase.AddTransactionUseCase
import com.sample.financetracker.domain.usecase.DeleteTransactionUseCase
import com.sample.financetracker.domain.usecase.ObserveMonthTransactionsUseCase
import com.sample.financetracker.domain.usecase.ObserveUserNameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

private const val TAG = "LedgerViewModel"

@HiltViewModel
class LedgerViewModel @Inject constructor(
    observeMonthTransactions: ObserveMonthTransactionsUseCase,
    observeUserName: ObserveUserNameUseCase,
    private val addTransaction: AddTransactionUseCase,
    private val deleteTransaction: DeleteTransactionUseCase,
    @DefaultDispatcher defaultDispatcher: CoroutineDispatcher,
) : ViewModel() {
    private val query = MutableStateFlow(LedgerQuery())
    private val retryCount = MutableStateFlow(0)
    private val messageChannel = Channel<UiMessage>(Channel.BUFFERED)

    val messages: Flow<UiMessage> = messageChannel.receiveAsFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<LedgerUiState> = retryCount
        .flatMapLatest {
            val months = query.map { it.month }.distinctUntilChanged()
            combine(observeMonthTransactions(months), query, observeUserName()) { data, q, name ->
                buildLedgerUiState(data, q.copy(month = data.month), name)
            }
                .flowOn(defaultDispatcher)
                .catch { emit(LedgerUiState(isLoading = false, error = it.toAppError().message)) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), LedgerUiState())

    fun setFilter(value: LedgerFilter) = query.update { it.copy(filter = value) }

    fun setMonth(value: YearMonth) = query.update { it.copy(month = value, category = null) }

    fun setCategory(value: String?) = query.update { it.copy(category = value) }

    fun setSearch(value: String) = query.update { it.copy(search = value) }

    fun retry() = retryCount.update { it + 1 }

    fun delete(transaction: Transaction) {
        viewModelScope.launch {
            deleteTransaction(transaction)
                .onSuccess {
                    messageChannel.send(UiMessage("Transaction deleted", "Undo") { undoDelete(transaction) })
                }
                .onFailure { reportFailure("delete", it) }
        }
    }

    private fun undoDelete(transaction: Transaction) {
        viewModelScope.launch {
            addTransaction(transaction).onFailure { reportFailure("restore", it) }
        }
    }

    private suspend fun reportFailure(action: String, error: Throwable) {
        Log.e(TAG, "Failed to $action transaction", error)
        messageChannel.send(UiMessage(error.toAppError().message.orEmpty()))
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}

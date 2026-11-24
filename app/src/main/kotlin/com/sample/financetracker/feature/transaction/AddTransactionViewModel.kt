package com.sample.financetracker.feature.transaction

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.financetracker.core.common.AppError
import com.sample.financetracker.core.common.toAppError
import com.sample.financetracker.domain.model.Transaction
import com.sample.financetracker.domain.usecase.AddTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddTransactionUiState(val isSaving: Boolean = false, val error: String? = null)

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val addTransaction: AddTransactionUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()

    private val savedChannel = Channel<Unit>(Channel.CONFLATED)
    val saved: Flow<Unit> = savedChannel.receiveAsFlow()

    fun save(transaction: Transaction) {
        if (_uiState.value.isSaving) return
        _uiState.value = AddTransactionUiState(isSaving = true)
        viewModelScope.launch {
            addTransaction(transaction)
                .onSuccess {
                    _uiState.value = AddTransactionUiState()
                    savedChannel.send(Unit)
                }
                .onFailure { error ->
                    if (error !is AppError.Validation) Log.e("AddTransaction", "Save failed", error)
                    _uiState.value = AddTransactionUiState(error = error.toAppError().message)
                }
        }
    }

    fun clearError() = _uiState.update { it.copy(error = null) }
}

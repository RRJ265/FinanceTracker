package com.sample.financetracker.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.financetracker.core.common.DefaultDispatcher
import com.sample.financetracker.core.common.toAppError
import com.sample.financetracker.domain.usecase.ObserveHomeSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    observeHomeSummary: ObserveHomeSummaryUseCase,
    @DefaultDispatcher defaultDispatcher: CoroutineDispatcher,
) : ViewModel() {
    private val month = MutableStateFlow(YearMonth.now())
    private val retryCount = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<HomeUiState> = retryCount
        .flatMapLatest {
            observeHomeSummary(month)
                .map(::buildHomeUiState)
                .flowOn(defaultDispatcher)
                .catch { emit(HomeUiState(isLoading = false, error = it.toAppError().message)) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), HomeUiState())

    fun selectMonth(value: YearMonth) {
        month.value = value
    }

    fun retry() = retryCount.update { it + 1 }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}

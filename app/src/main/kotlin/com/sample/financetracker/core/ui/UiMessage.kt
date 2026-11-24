package com.sample.financetracker.core.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

data class UiMessage(
    val text: String,
    val actionLabel: String? = null,
    val onAction: () -> Unit = {},
)

@Composable
fun CollectMessages(messages: Flow<UiMessage>, snackbarHostState: SnackbarHostState) {
    val host = rememberUpdatedState(snackbarHostState)
    LaunchedEffect(messages) {
        messages.collectLatest { message ->
            val result = host.value.showSnackbar(message.text, message.actionLabel)
            if (result == SnackbarResult.ActionPerformed) message.onAction()
        }
    }
}

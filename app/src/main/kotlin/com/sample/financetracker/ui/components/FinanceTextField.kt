package com.sample.financetracker.ui.components

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FinanceTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isCurrency: Boolean = false,
    singleLine: Boolean = true,
) {
    val scheme = MaterialTheme.colorScheme
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.heightIn(min = 56.dp),
        label = { Text(label) },
        prefix = if (isCurrency) {
            { Text("₹", style = MaterialTheme.typography.bodyLarge, color = scheme.onSurfaceVariant) }
        } else {
            null
        },
        singleLine = singleLine,
        keyboardOptions = keyboardOptions,
        shape = MaterialTheme.shapes.medium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = scheme.surface,
            unfocusedContainerColor = scheme.surface,
            focusedBorderColor = scheme.primaryContainer,
            unfocusedBorderColor = scheme.outlineVariant,
            focusedLabelColor = scheme.primaryContainer,
            unfocusedLabelColor = scheme.onSurfaceVariant,
            cursorColor = scheme.primaryContainer,
        ),
    )
}

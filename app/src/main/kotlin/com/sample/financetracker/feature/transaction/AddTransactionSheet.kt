package com.sample.financetracker.feature.transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sample.financetracker.domain.model.Transaction
import com.sample.financetracker.domain.model.TransactionType
import com.sample.financetracker.core.designsystem.component.FinanceChip
import com.sample.financetracker.core.designsystem.component.FinanceTextField
import com.sample.financetracker.core.designsystem.component.PrimaryButton
import com.sample.financetracker.core.designsystem.theme.SheetShape
import com.sample.financetracker.core.util.parsePaise

private val ExpenseCategories = listOf("Food", "Rent", "Travel", "Shopping", "Bills", "Health", "Other")
private val IncomeCategories = listOf("Salary", "Business", "Investments", "Gift", "Other")

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddTransactionSheet(
    onDismiss: () -> Unit,
    initialType: TransactionType = TransactionType.EXPENSE,
    viewModel: AddTransactionViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) {
        viewModel.clearError()
        viewModel.saved.collect { onDismiss() }
    }

    var type by rememberSaveable { mutableStateOf(initialType) }
    var amount by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable {
        mutableStateOf((if (initialType == TransactionType.EXPENSE) ExpenseCategories else IncomeCategories).first())
    }
    var note by rememberSaveable { mutableStateOf("") }

    val categories = if (type == TransactionType.EXPENSE) ExpenseCategories else IncomeCategories
    val amountPaise = parsePaise(amount)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = SheetShape,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("New transaction", style = MaterialTheme.typography.headlineSmall)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FinanceChip("Expense", type == TransactionType.EXPENSE, {
                    type = TransactionType.EXPENSE
                    category = ExpenseCategories.first()
                })
                FinanceChip("Income", type == TransactionType.INCOME, {
                    type = TransactionType.INCOME
                    category = IncomeCategories.first()
                })
            }

            FinanceTextField(
                value = amount,
                onValueChange = { input ->
                    if (input.matches(Regex("""\d{0,9}(\.\d{0,2})?"""))) amount = input
                },
                label = "Amount",
                modifier = Modifier.fillMaxWidth(),
                isCurrency = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                categories.forEach { name ->
                    FinanceChip(name, category == name, { category = name })
                }
            }

            FinanceTextField(
                value = note,
                onValueChange = { note = it },
                label = "Note (optional)",
                modifier = Modifier.fillMaxWidth(),
            )

            state.error?.let {
                Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
            }

            PrimaryButton(
                text = if (state.isSaving) "Saving…" else "Save",
                enabled = amountPaise != null && !state.isSaving,
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel.save(
                        Transaction(
                            type = type,
                            amountPaise = amountPaise ?: return@PrimaryButton,
                            category = category,
                            note = note.trim(),
                            dateMillis = System.currentTimeMillis(),
                        ),
                    )
                },
            )
        }
    }
}

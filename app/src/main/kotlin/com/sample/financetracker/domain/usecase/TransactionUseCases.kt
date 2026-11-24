package com.sample.financetracker.domain.usecase

import com.sample.financetracker.core.common.AppError
import com.sample.financetracker.core.common.suspendRunCatching
import com.sample.financetracker.domain.model.Transaction
import com.sample.financetracker.domain.repository.TransactionRepository
import javax.inject.Inject

const val MAX_AMOUNT_PAISE = 99_99_99_999L * 100
const val MAX_NOTE_LENGTH = 100

class AddTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository,
) {
    suspend operator fun invoke(transaction: Transaction): Result<Unit> = suspendRunCatching {
        validate(transaction)
        repository.add(transaction.copy(note = transaction.note.trim()))
    }

    private fun validate(transaction: Transaction) {
        if (transaction.amountPaise <= 0) throw AppError.Validation("Enter an amount greater than zero.")
        if (transaction.amountPaise > MAX_AMOUNT_PAISE) throw AppError.Validation("That amount is too large.")
        if (transaction.category.isBlank()) throw AppError.Validation("Choose a category.")
        if (transaction.note.trim().length > MAX_NOTE_LENGTH) {
            throw AppError.Validation("Notes can be at most $MAX_NOTE_LENGTH characters.")
        }
    }
}

class DeleteTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository,
) {
    suspend operator fun invoke(transaction: Transaction): Result<Unit> =
        suspendRunCatching { repository.delete(transaction) }
}

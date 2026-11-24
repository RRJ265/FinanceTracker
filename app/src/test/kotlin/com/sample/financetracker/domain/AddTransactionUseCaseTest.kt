package com.sample.financetracker.domain

import com.sample.financetracker.core.common.AppError
import com.sample.financetracker.domain.model.Transaction
import com.sample.financetracker.domain.model.TransactionType
import com.sample.financetracker.domain.repository.TransactionRepository
import com.sample.financetracker.domain.usecase.AddTransactionUseCase
import com.sample.financetracker.domain.usecase.DeleteTransactionUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

private class FakeRepository(private val failure: Throwable? = null) : TransactionRepository {
    val added = mutableListOf<Transaction>()
    val deleted = mutableListOf<Transaction>()

    override fun observeBetween(startMillis: Long, endMillis: Long): Flow<List<Transaction>> = emptyFlow()
    override fun observeRecent(limit: Int): Flow<List<Transaction>> = emptyFlow()
    override fun observeNetBalanceBefore(endMillis: Long): Flow<Long> = emptyFlow()

    override suspend fun add(transaction: Transaction) {
        failure?.let { throw it }
        added += transaction
    }

    override suspend fun delete(transaction: Transaction) {
        failure?.let { throw it }
        deleted += transaction
    }
}

class AddTransactionUseCaseTest {
    private fun transaction(amountPaise: Long = 12_500, category: String = "Food", note: String = "  lunch ") =
        Transaction(type = TransactionType.EXPENSE, amountPaise = amountPaise, category = category, note = note, dateMillis = 1L)

    @Test
    fun savesValidTransactionWithTrimmedNote() = runTest {
        val repository = FakeRepository()
        val result = AddTransactionUseCase(repository)(transaction())
        assertTrue(result.isSuccess)
        assertEquals("lunch", repository.added.single().note)
    }

    @Test
    fun rejectsInvalidInputWithoutTouchingStorage() = runTest {
        val repository = FakeRepository()
        val useCase = AddTransactionUseCase(repository)
        listOf(transaction(amountPaise = 0), transaction(category = " "), transaction(note = "x".repeat(101))).forEach {
            assertTrue(useCase(it).exceptionOrNull() is AppError.Validation)
        }
        assertTrue(repository.added.isEmpty())
    }

    @Test
    fun mapsStorageFailuresToAppErrors() = runTest {
        val result = AddTransactionUseCase(FakeRepository(IOException("disk full")))(transaction())
        assertTrue(result.exceptionOrNull() is AppError.Storage)
    }

    @Test
    fun deleteDelegatesToRepository() = runTest {
        val repository = FakeRepository()
        assertTrue(DeleteTransactionUseCase(repository)(transaction()).isSuccess)
        assertEquals(1, repository.deleted.size)
    }
}

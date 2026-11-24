package com.sample.financetracker.data.repository

import com.sample.financetracker.core.common.IoDispatcher
import com.sample.financetracker.core.common.toAppError
import com.sample.financetracker.data.local.db.TransactionDao
import com.sample.financetracker.data.local.db.TransactionEntity
import com.sample.financetracker.data.mapper.toDomain
import com.sample.financetracker.data.mapper.toEntity
import com.sample.financetracker.domain.model.Transaction
import com.sample.financetracker.domain.repository.TransactionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepositoryImpl @Inject constructor(
    private val dao: TransactionDao,
    @IoDispatcher private val io: CoroutineDispatcher,
) : TransactionRepository {
    override fun observeBetween(startMillis: Long, endMillis: Long): Flow<List<Transaction>> =
        dao.observeBetween(startMillis, endMillis).mapEntities()

    override fun observeRecent(limit: Int): Flow<List<Transaction>> = dao.observeRecent(limit).mapEntities()

    override fun observeNetBalanceBefore(endMillis: Long): Flow<Long> =
        dao.observeNetBalanceBefore(endMillis).guarded()

    override suspend fun add(transaction: Transaction) = guarded { dao.insert(transaction.toEntity()) }

    override suspend fun delete(transaction: Transaction) = guarded { dao.delete(transaction.toEntity()) }

    private fun Flow<List<TransactionEntity>>.mapEntities() =
        map { rows -> rows.map { it.toDomain() } }.guarded()

    private fun <T> Flow<T>.guarded(): Flow<T> = catch { throw it.toAppError() }.flowOn(io)

    private suspend fun <T> guarded(block: suspend () -> T): T = try {
        withContext(io) { block() }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        throw e.toAppError()
    }
}

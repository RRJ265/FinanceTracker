package com.sample.financetracker.domain.repository

import com.sample.financetracker.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun observeBetween(startMillis: Long, endMillis: Long): Flow<List<Transaction>>

    fun observeRecent(limit: Int): Flow<List<Transaction>>

    fun observeNetBalanceBefore(endMillis: Long): Flow<Long>

    suspend fun add(transaction: Transaction)

    suspend fun delete(transaction: Transaction)
}

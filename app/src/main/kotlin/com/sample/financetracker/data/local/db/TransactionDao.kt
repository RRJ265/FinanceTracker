package com.sample.financetracker.data.local.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query(
        "SELECT * FROM transactions WHERE dateMillis >= :startMillis AND dateMillis < :endMillis " +
            "ORDER BY dateMillis DESC, id DESC",
    )
    fun observeBetween(startMillis: Long, endMillis: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY dateMillis DESC, id DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<TransactionEntity>>

    @Query(
        "SELECT COALESCE(SUM(CASE WHEN type = 'INCOME' THEN amountPaise ELSE -amountPaise END), 0) " +
            "FROM transactions WHERE dateMillis < :endMillis",
    )
    fun observeNetBalanceBefore(endMillis: Long): Flow<Long>

    @Insert
    suspend fun insert(transaction: TransactionEntity)

    @Delete
    suspend fun delete(transaction: TransactionEntity)
}

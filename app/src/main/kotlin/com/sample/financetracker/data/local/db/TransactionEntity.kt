package com.sample.financetracker.data.local.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.sample.financetracker.domain.model.TransactionType

@Entity(tableName = "transactions", indices = [Index("dateMillis")])
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: TransactionType,
    val amountPaise: Long,
    val category: String,
    val note: String,
    val dateMillis: Long,
)

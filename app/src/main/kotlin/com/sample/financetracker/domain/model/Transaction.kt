package com.sample.financetracker.domain.model

enum class TransactionType { INCOME, EXPENSE }

data class Transaction(
    val id: Long = 0,
    val type: TransactionType,
    val amountPaise: Long,
    val category: String,
    val note: String,
    val dateMillis: Long,
)

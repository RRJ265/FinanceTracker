package com.sample.financetracker.data.mapper

import com.sample.financetracker.data.local.db.TransactionEntity
import com.sample.financetracker.domain.model.Transaction

fun TransactionEntity.toDomain() = Transaction(id, type, amountPaise, category, note, dateMillis)

fun Transaction.toEntity() = TransactionEntity(id, type, amountPaise, category, note, dateMillis)

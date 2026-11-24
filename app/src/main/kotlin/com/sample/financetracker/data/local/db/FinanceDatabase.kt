package com.sample.financetracker.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [TransactionEntity::class], version = 2, exportSchema = false)
abstract class FinanceDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    companion object {
        const val NAME = "finance_tracker.db"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_dateMillis` ON `transactions` (`dateMillis`)")
            }
        }
    }
}

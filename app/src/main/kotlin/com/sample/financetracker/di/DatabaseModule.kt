package com.sample.financetracker.di

import android.content.Context
import androidx.room.Room
import com.sample.financetracker.data.local.db.FinanceDatabase
import com.sample.financetracker.data.local.db.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun database(@ApplicationContext context: Context): FinanceDatabase =
        Room.databaseBuilder(context, FinanceDatabase::class.java, FinanceDatabase.NAME)
            .addMigrations(FinanceDatabase.MIGRATION_1_2)
            .build()

    @Provides
    fun transactionDao(database: FinanceDatabase): TransactionDao = database.transactionDao()
}

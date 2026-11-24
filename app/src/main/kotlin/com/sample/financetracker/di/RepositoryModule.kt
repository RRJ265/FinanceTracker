package com.sample.financetracker.di

import com.sample.financetracker.data.repository.SettingsRepositoryImpl
import com.sample.financetracker.data.repository.TransactionRepositoryImpl
import com.sample.financetracker.domain.repository.SettingsRepository
import com.sample.financetracker.domain.repository.TransactionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun transactions(impl: TransactionRepositoryImpl): TransactionRepository

    @Binds
    abstract fun settings(impl: SettingsRepositoryImpl): SettingsRepository
}

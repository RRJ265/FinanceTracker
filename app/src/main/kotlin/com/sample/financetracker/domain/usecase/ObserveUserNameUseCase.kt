package com.sample.financetracker.domain.usecase

import com.sample.financetracker.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveUserNameUseCase @Inject constructor(
    private val settings: SettingsRepository,
) {
    operator fun invoke(): Flow<String> = settings.userName
}

package com.breckneck.deptbook.domain.usecase.Settings

import com.breckneck.deptbook.domain.repository.SettingsRepository

class SetFinanceCurrency(private val settingsRepository: SettingsRepository) {

    fun execute(currency: String) {
        settingsRepository.setFinanceCurrency(currency = currency)
    }
}
package com.breckneck.deptbook.domain.usecase.Settings

import com.breckneck.deptbook.domain.repository.SettingsRepository

class SetDefaultCurrency(private val settingsRepository: SettingsRepository) {

    fun execute(currency: String) {
        settingsRepository.setDefaultCurrency(currency = currency)
    }
}
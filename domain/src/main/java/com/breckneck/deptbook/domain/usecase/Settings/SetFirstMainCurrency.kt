package com.breckneck.deptbook.domain.usecase.Settings

import com.breckneck.deptbook.domain.repository.SettingsRepository

class SetFirstMainCurrency(private val settingsRepository: SettingsRepository) {

    fun execute(currency: String) {
        settingsRepository.setFirstMainCurrency(currency = currency)
    }
}
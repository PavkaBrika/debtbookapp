package com.breckneck.deptbook.domain.usecase.Settings

import com.breckneck.deptbook.domain.repository.SettingsRepository

class SetSecondMainCurrency(private val settingsRepository: SettingsRepository) {

    fun execute(currency: String) {
        settingsRepository.setSecondMainCurrency(currency = currency)
    }
}
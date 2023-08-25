package com.breckneck.deptbook.domain.usecase.Settings

import com.breckneck.deptbook.domain.repository.SettingsRepository

class GetDefaultCurrency(private val settingsRepository: SettingsRepository) {

    fun execute(): String {
        return settingsRepository.getDefaultCurrency()
    }
}
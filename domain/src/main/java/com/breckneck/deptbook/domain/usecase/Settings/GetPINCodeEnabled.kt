package com.breckneck.deptbook.domain.usecase.Settings

import com.breckneck.deptbook.domain.repository.SettingsRepository

class GetPINCodeEnabled(private val settingsRepository: SettingsRepository) {

    fun execute(): Boolean {
        return settingsRepository.getPINCodeEnabled()
    }
}
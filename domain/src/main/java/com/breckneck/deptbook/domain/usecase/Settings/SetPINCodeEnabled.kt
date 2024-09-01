package com.breckneck.deptbook.domain.usecase.Settings

import com.breckneck.deptbook.domain.repository.SettingsRepository

class SetPINCodeEnabled(private val settingsRepository: SettingsRepository) {

    fun execute(isEnabled: Boolean) {
        settingsRepository.setPINCodeEnabled(isEnabled = isEnabled)
    }
}
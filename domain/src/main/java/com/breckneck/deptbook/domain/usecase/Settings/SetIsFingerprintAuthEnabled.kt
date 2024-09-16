package com.breckneck.deptbook.domain.usecase.Settings

import com.breckneck.deptbook.domain.repository.SettingsRepository

class SetIsFingerprintAuthEnabled(private val settingsRepository: SettingsRepository) {

    fun execute(isEnabled: Boolean) {
        settingsRepository.setIsFingerprintAuthEnabled(isEnabled = isEnabled)
    }
}
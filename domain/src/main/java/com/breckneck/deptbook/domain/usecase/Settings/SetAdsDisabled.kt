package com.breckneck.deptbook.domain.usecase.Settings

import com.breckneck.deptbook.domain.repository.SettingsRepository

class SetAdsDisabled(private val settingsRepository: SettingsRepository) {

    fun execute(isDisabled: Boolean) {
        settingsRepository.setAdsDisabled(isDisabled = isDisabled)
    }
}

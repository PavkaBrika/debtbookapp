package com.breckneck.deptbook.domain.usecase.Settings

import com.breckneck.deptbook.domain.repository.SettingsRepository

class SetIsAuthorized(private val settingsRepository: SettingsRepository) {

    fun execute(isAuthorized: Boolean) {
        settingsRepository.setIsAuthorized(isAuthorized = isAuthorized)
    }
}
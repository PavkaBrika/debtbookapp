package com.breckneck.deptbook.domain.usecase.Settings

import com.breckneck.deptbook.domain.repository.SettingsRepository

class SetAppIsRated(private val settingsRepository: SettingsRepository) {

    fun execute(isRated: Boolean) {
        settingsRepository.setAppIsRated(isRated = isRated)
    }
}
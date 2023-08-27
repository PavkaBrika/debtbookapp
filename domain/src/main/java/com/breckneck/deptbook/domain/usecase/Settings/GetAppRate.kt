package com.breckneck.deptbook.domain.usecase.Settings

import com.breckneck.deptbook.domain.repository.SettingsRepository

class GetAppRate(private val settingsRepository: SettingsRepository) {

    fun execute(): Int {
        return settingsRepository.getAppRate()
    }
}
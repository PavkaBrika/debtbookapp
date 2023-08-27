package com.breckneck.deptbook.domain.usecase.Settings

import com.breckneck.deptbook.domain.repository.SettingsRepository

class SetAppRate(private val settingsRepository: SettingsRepository) {

    fun execute(rate: Int) {
        settingsRepository.setAppRate(rate = rate)
    }
}
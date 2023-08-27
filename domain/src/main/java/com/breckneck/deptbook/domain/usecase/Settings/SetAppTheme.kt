package com.breckneck.deptbook.domain.usecase.Settings

import com.breckneck.deptbook.domain.repository.SettingsRepository

class SetAppTheme(private val settingsRepository: SettingsRepository) {

    fun execute(theme: String) {
        settingsRepository.setAppTheme(theme = theme)
    }
}
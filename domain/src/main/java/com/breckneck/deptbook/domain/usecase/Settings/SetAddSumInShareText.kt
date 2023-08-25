package com.breckneck.deptbook.domain.usecase.Settings

import com.breckneck.deptbook.domain.repository.SettingsRepository

class SetAddSumInShareText(private val settingsRepository: SettingsRepository) {

    fun execute(addSumInShareText: Boolean) {
        settingsRepository.setAddSumInShareText(addSumInShareText = addSumInShareText)
    }
}
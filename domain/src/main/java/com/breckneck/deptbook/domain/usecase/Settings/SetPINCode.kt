package com.breckneck.deptbook.domain.usecase.Settings

import com.breckneck.deptbook.domain.repository.SettingsRepository

class SetPINCode(private val settingsRepository: SettingsRepository) {

    fun execute(PINCode: String) {
        settingsRepository.setPINCode(PINCode = PINCode)
    }

}
package com.breckneck.deptbook.domain.usecase.Settings

import com.breckneck.deptbook.domain.repository.SettingsRepository

class SetDebtQuantityForAppRateDialogShow(private val settingsRepository: SettingsRepository) {

    fun execute(quantity: Int) {
        settingsRepository.setDebtQuantityForAppRateDialogShow(quantity = quantity)
    }
}
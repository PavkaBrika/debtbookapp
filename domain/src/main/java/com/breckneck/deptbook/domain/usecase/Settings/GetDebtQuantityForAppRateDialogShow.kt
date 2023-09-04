package com.breckneck.deptbook.domain.usecase.Settings

import com.breckneck.deptbook.domain.repository.SettingsRepository

class GetDebtQuantityForAppRateDialogShow(private val settingsRepository: SettingsRepository) {

    fun execute(): Int {
        return settingsRepository.getDebtQuantityForAppRateDialogShow()
    }

}
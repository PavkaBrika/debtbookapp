package com.breckneck.deptbook.domain.usecase.Settings

import com.breckneck.deptbook.domain.repository.SettingsRepository
import com.breckneck.deptbook.domain.util.DebtOrderAttribute

class SetDebtOrder(private val settingsRepository: SettingsRepository) {

    fun execute(attribute: DebtOrderAttribute, byIncrease: Boolean) {
        settingsRepository.setDebtOrder(Pair(attribute, byIncrease))
    }
}
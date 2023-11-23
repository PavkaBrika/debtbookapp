package com.breckneck.deptbook.domain.usecase.Settings

import com.breckneck.deptbook.domain.repository.SettingsRepository
import com.breckneck.deptbook.domain.util.DebtOrderAttribute

class SetDebtOrder(private val settingsRepository: SettingsRepository) {

    fun execute(order: Pair<DebtOrderAttribute, Boolean>) {
        settingsRepository.setDebtOrder(order = order)
    }
}
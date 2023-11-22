package com.breckneck.deptbook.domain.usecase.Settings

import com.breckneck.deptbook.domain.repository.SettingsRepository
import com.breckneck.deptbook.domain.util.DebtOrder
import com.breckneck.deptbook.domain.util.DebtOrderAttribute

class GetDebtOrder(private val settingsRepository: SettingsRepository) {

    fun execute(): Pair<DebtOrderAttribute, Boolean> {
        return settingsRepository.getDebtOrder()
    }
}
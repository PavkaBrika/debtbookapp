package com.breckneck.deptbook.domain.usecase.Settings

import com.breckneck.deptbook.domain.repository.SettingsRepository
import com.breckneck.deptbook.domain.util.HumanOrderAttribute

class SetHumanOrder(private val settingsRepository: SettingsRepository) {

    fun execute(order: Pair<HumanOrderAttribute, Boolean>) {
        settingsRepository.setHumanOrder(order = order)
    }
}
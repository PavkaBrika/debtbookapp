package com.breckneck.deptbook.domain.usecase.Settings

import com.breckneck.deptbook.domain.repository.SettingsRepository
import com.breckneck.deptbook.domain.util.HumanOrderAttribute

class GetHumanOrder(private val settingsRepository: SettingsRepository) {

    fun execute(): Pair<HumanOrderAttribute, Boolean> {
        return settingsRepository.getHumanOrder()
    }
}
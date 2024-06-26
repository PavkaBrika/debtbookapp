package com.breckneck.deptbook.domain.usecase.Settings

import com.breckneck.deptbook.domain.repository.SettingsRepository

class GetLastSyncDate(private val settingsRepository: SettingsRepository) {

    fun execute(): Long {
        return settingsRepository.getLastSyncDate()
    }
}
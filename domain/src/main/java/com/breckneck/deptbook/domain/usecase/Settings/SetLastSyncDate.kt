package com.breckneck.deptbook.domain.usecase.Settings

import com.breckneck.deptbook.domain.repository.SettingsRepository

class SetLastSyncDate(private val settingsRepository: SettingsRepository) {

    fun execute(lastSyncDateInMillis: Long) {
        settingsRepository.setLastSyncDate(lastSyncDateInMillis = lastSyncDateInMillis)
    }
}
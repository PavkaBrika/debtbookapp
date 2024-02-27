package com.breckneck.deptbook.domain.usecase.Settings

import com.breckneck.deptbook.domain.model.User
import com.breckneck.deptbook.domain.repository.SettingsRepository

class GetUserData(private val settingsRepository: SettingsRepository) {

    fun execute(): User {
        return settingsRepository.getUserData()
    }
}
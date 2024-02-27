package com.breckneck.deptbook.domain.usecase.Settings

import com.breckneck.deptbook.domain.model.User
import com.breckneck.deptbook.domain.repository.SettingsRepository

class SetUserData(private val settingsRepository: SettingsRepository) {

    fun execute(name: String, email: String) {
        settingsRepository.setUserData(user = User(name = name, email = email))
    }
}
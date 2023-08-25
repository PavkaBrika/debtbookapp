package repository

import com.breckneck.deptbook.data.storage.SettingsStorage
import com.breckneck.deptbook.domain.repository.SettingsRepository

class SettingsRepositoryImpl(private val settingsStorage: SettingsStorage): SettingsRepository {

    override fun setFirstMainCurrency(currency: String) {
        settingsStorage.setFirstMainCurrency(currency = currency)
    }

    override fun getFirstMainCurrency(): String {
        return settingsStorage.getFirstMainCurrency()
    }
}
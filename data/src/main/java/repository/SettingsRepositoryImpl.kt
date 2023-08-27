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

    override fun setSecondMainCurrency(currency: String) {
        settingsStorage.setSecondMainCurrency(currency = currency)
    }

    override fun getSecondMainCurrency(): String {
        return settingsStorage.getSecondMainCurrency()
    }

    override fun setDefaultCurrency(currency: String) {
        settingsStorage.setDefaultCurrency(currency = currency)
    }

    override fun getDefaultCurrency(): String {
        return settingsStorage.getDefaultCurrency()
    }

    override fun setAddSumInShareText(addSumInShareText: Boolean) {
        settingsStorage.setAddSumInShareText(addSumInShareText = addSumInShareText)
    }

    override fun getAddSumInShareText(): Boolean {
        return settingsStorage.getAddSumInShareText()
    }

    override fun setAppRate(rate: Int) {
        settingsStorage.setAppRate(rate = rate)
    }

    override fun getAppRate(): Int {
        return settingsStorage.getAppRate()
    }
}
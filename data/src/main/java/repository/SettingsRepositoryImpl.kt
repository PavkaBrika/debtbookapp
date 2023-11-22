package repository

import com.breckneck.deptbook.data.storage.SettingsStorage
import com.breckneck.deptbook.domain.repository.SettingsRepository
import com.breckneck.deptbook.domain.util.DebtOrderAttribute
import util.*

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

    override fun setAppIsRated(isRated: Boolean) {
        settingsStorage.setAppIsRated(isRated = isRated)
    }

    override fun getAppIsRated(): Boolean {
        return settingsStorage.getAppIsRated()
    }

    override fun setAppTheme(theme: String) {
        settingsStorage.setAppTheme(theme = theme)
    }

    override fun getAppTheme(): String {
        return settingsStorage.getAppTheme()
    }

    override fun setDebtQuantityForAppRateDialogShow(quantity: Int) {
        settingsStorage.setDebtQuantityForAppRateDialogShow(quantity = quantity)
    }

    override fun getDebtQuantityForAppRateDialogShow(): Int {
        return settingsStorage.getDebtQuantityForAppRateDialogShow()
    }

    override fun setDebtOrder(order: Pair<DebtOrderAttribute, Boolean>) {
        val debtAttribute: Int
        when (order.first) {
            DebtOrderAttribute.Date -> debtAttribute = ORDER_BY_DATE
            DebtOrderAttribute.Sum -> debtAttribute = ORDER_BY_SUM
        }
        settingsStorage.setDebtOrder(Pair(debtAttribute, order.second))
    }

    override fun getDebtOrder(): Pair<DebtOrderAttribute, Boolean> {
        var debtAttribute: DebtOrderAttribute = DebtOrderAttribute.Date
        val debtOrder = settingsStorage.getDebtOrder()
        when (debtOrder.first) {
            ORDER_BY_DATE -> debtAttribute = DebtOrderAttribute.Date
            ORDER_BY_SUM -> debtAttribute = DebtOrderAttribute.Sum
        }
        return Pair(debtAttribute, debtOrder.second)
    }
}
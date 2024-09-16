package repository

import com.breckneck.deptbook.data.storage.SettingsStorage
import com.breckneck.deptbook.domain.model.User
import com.breckneck.deptbook.domain.repository.SettingsRepository
import com.breckneck.deptbook.domain.util.DebtOrderAttribute
import com.breckneck.deptbook.domain.util.HumanOrderAttribute
import entity.UserData
import util.*

class SettingsRepositoryImpl(private val settingsStorage: SettingsStorage): SettingsRepository {

    override fun setUserData(user: User) {
        settingsStorage.setUserData(userData = UserData(name = user.name, email = user.email))
    }

    override fun getUserData(): User {
        val userData = settingsStorage.getUserData()
        return User(name = userData.name, userData.email)
    }

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
        debtAttribute = when (order.first) {
            DebtOrderAttribute.CreationDate -> ORDER_DEBT_BY_CREATION_DATE
            DebtOrderAttribute.Date -> ORDER_DEBT_BY_DATE
            DebtOrderAttribute.Sum -> ORDER_DEBT_BY_SUM
        }
        settingsStorage.setDebtOrder(Pair(debtAttribute, order.second))
    }

    override fun getDebtOrder(): Pair<DebtOrderAttribute, Boolean> {
        var debtAttribute: DebtOrderAttribute = DebtOrderAttribute.Date
        val debtOrder = settingsStorage.getDebtOrder()
        when (debtOrder.first) {
            ORDER_DEBT_BY_DATE -> debtAttribute = DebtOrderAttribute.Date
            ORDER_DEBT_BY_SUM -> debtAttribute = DebtOrderAttribute.Sum
        }
        return Pair(debtAttribute, debtOrder.second)
    }

    override fun setHumanOrder(order: Pair<HumanOrderAttribute, Boolean>) {
        val humanAttribute: Int
        when (order.first) {
            HumanOrderAttribute.Date -> humanAttribute = ORDER_HUMAN_BY_DATE
            HumanOrderAttribute.Sum -> humanAttribute = ORDER_HUMAN_BY_SUM
        }
        settingsStorage.setHumanOrder(Pair(humanAttribute, order.second))
    }

    override fun getHumanOrder(): Pair<HumanOrderAttribute, Boolean> {
        var humanAttribute: HumanOrderAttribute = HumanOrderAttribute.Date
        val humanOrder = settingsStorage.getHumanOrder()
        when (humanOrder.first) {
            ORDER_HUMAN_BY_DATE -> humanAttribute = HumanOrderAttribute.Date
            ORDER_HUMAN_BY_SUM -> humanAttribute = HumanOrderAttribute.Sum
        }
        return Pair(humanAttribute, humanOrder.second)
    }

    override fun setIsAuthorized(isAuthorized: Boolean) {
        settingsStorage.setIsAuthorized(isAuthorized = isAuthorized)
    }

    override fun getIsAuthorized(): Boolean {
        return settingsStorage.getIsAuthorized()
    }

    override fun setLastSyncDate(lastSyncDateInMillis: Long) {
        settingsStorage.setLastSyncDate(lastSyncDateInMillis = lastSyncDateInMillis)
    }

    override fun getLastSyncDate(): Long {
        return settingsStorage.getLastSyncDate()
    }

    override fun setFinanceCurrency(currency: String) {
        settingsStorage.setFinanceCurrency(currency = currency)
    }

    override fun getFinanceCurrency(): String {
        return settingsStorage.getFinanceCurrency()
    }

    override fun setPINCodeEnabled(isEnabled: Boolean) {
        settingsStorage.setPINCodeEnabled(isEnabled = isEnabled)
    }

    override fun getPINCodeEnabled(): Boolean {
        return settingsStorage.getPINCodeEnabled()
    }

    override fun getPINCode(): String {
        return settingsStorage.getPINCode()
    }

    override fun setPINCode(PINCode: String) {
        settingsStorage.setPINCode(PINCode = PINCode)
    }

    override fun setIsFingerprintAuthEnabled(isEnabled: Boolean) {
        settingsStorage.setIsFingerprintAuthEnabled(isEnabled = isEnabled)
    }

    override fun getIsFingerprintAuthEnabled(): Boolean {
        return settingsStorage.getIsFingerprintAuthEnabled()
    }
}
package com.breckneck.deptbook.domain.repository

import com.breckneck.deptbook.domain.model.User
import com.breckneck.deptbook.domain.util.DebtOrderAttribute
import com.breckneck.deptbook.domain.util.HumanOrderAttribute

interface SettingsRepository {

    fun setFirstMainCurrency(currency: String)

    fun getFirstMainCurrency(): String

    fun setSecondMainCurrency(currency: String)

    fun getSecondMainCurrency(): String

    fun setDefaultCurrency(currency: String)

    fun getDefaultCurrency(): String

    fun setAddSumInShareText(addSumInShareText: Boolean)

    fun getAddSumInShareText(): Boolean

    fun setAppIsRated(isRated: Boolean)

    fun getAppIsRated(): Boolean

    fun setAppTheme(theme: String)

    fun getAppTheme(): String

    fun setDebtQuantityForAppRateDialogShow(quantity: Int)

    fun getDebtQuantityForAppRateDialogShow(): Int

    fun setDebtOrder(order: Pair<DebtOrderAttribute, Boolean>)

    fun getDebtOrder(): Pair<DebtOrderAttribute, Boolean>

    fun setHumanOrder(order: Pair<HumanOrderAttribute, Boolean>)

    fun getHumanOrder(): Pair<HumanOrderAttribute, Boolean>

    fun setIsAuthorized(isAuthorized: Boolean)

    fun getIsAuthorized(): Boolean

    fun setUserData(user: User)

    fun getUserData(): User

    fun setLastSyncDate(lastSyncDateInMillis: Long)

    fun getLastSyncDate(): Long

    fun setFinanceCurrency(currency: String)

    fun getFinanceCurrency(): String

    fun setPINCodeEnabled(isEnabled: Boolean)

    fun getPINCodeEnabled(): Boolean

    fun getPINCode(): String

    fun setPINCode(PINCode: String)

    fun setIsFingerprintAuthEnabled(isEnabled: Boolean)

    fun getIsFingerprintAuthEnabled(): Boolean
}
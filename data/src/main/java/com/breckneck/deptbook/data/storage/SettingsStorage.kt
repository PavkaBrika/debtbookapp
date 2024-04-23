package com.breckneck.deptbook.data.storage

import entity.UserData

interface SettingsStorage {

    fun setUserData(userData: UserData)

    fun getUserData(): UserData

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

    fun setDebtOrder(order: Pair<Int, Boolean>)

    fun getDebtOrder(): Pair<Int, Boolean>

    fun setHumanOrder(order: Pair<Int, Boolean>)

    fun getHumanOrder(): Pair<Int, Boolean>

    fun setIsAuthorized(isAuthorized: Boolean)

    fun getIsAuthorized(): Boolean

    fun setLastSyncDate(lastSyncDateInMillis: Long)

    fun getLastSyncDate(): Long

    fun setFinanceCurrency(currency: String)

    fun getFinanceCurrency(): String
}
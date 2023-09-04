package com.breckneck.deptbook.domain.repository

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
}
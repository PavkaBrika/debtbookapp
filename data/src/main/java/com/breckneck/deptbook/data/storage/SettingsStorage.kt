package com.breckneck.deptbook.data.storage

interface SettingsStorage {

    fun setFirstMainCurrency(currency: String)

    fun getFirstMainCurrency(): String

    fun setSecondMainCurrency(currency: String)

    fun getSecondMainCurrency(): String

    fun setDefaultCurrency(currency: String)

    fun getDefaultCurrency(): String

    fun setAddSumInShareText(addSumInShareText: Boolean)

    fun getAddSumInShareText(): Boolean

    fun setAppRate(rate: Int)

    fun getAppRate(): Int
}
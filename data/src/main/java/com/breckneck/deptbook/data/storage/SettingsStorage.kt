package com.breckneck.deptbook.data.storage

interface SettingsStorage {

    fun setFirstMainCurrency(currency: String)

    fun getFirstMainCurrency(): String

}
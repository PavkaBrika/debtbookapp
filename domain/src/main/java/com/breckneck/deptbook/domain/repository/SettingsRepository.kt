package com.breckneck.deptbook.domain.repository

interface SettingsRepository {

    fun setFirstMainCurrency(currency: String)

    fun getFirstMainCurrency(): String


}
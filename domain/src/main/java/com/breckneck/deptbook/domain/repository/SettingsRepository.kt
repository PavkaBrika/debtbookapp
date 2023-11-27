package com.breckneck.deptbook.domain.repository

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
}
package com.breckneck.deptbook.domain.usecase.Debt

import com.breckneck.deptbook.domain.model.DebtDomain
import com.breckneck.deptbook.domain.util.Filter

class FilterDebts {

    fun execute(debtList: List<DebtDomain>, filter: Filter): List<DebtDomain> {
        val resultDebtList = ArrayList<DebtDomain>()
        when (filter) {
            Filter.All -> {
                return debtList
            }
            Filter.Negative -> {
                for (debt in debtList)
                    if (debt.sum <= 0)
                        resultDebtList.add(debt)
            }
            Filter.Positive -> {
                for (debt in debtList)
                    if (debt.sum >= 0)
                        resultDebtList.add(debt)
            }
        }
        return resultDebtList
    }
}
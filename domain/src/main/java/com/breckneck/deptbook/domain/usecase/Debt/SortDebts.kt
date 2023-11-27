package com.breckneck.deptbook.domain.usecase.Debt

import com.breckneck.deptbook.domain.model.DebtDomain
import com.breckneck.deptbook.domain.util.DebtOrderAttribute

class SortDebts {

    fun execute(debtList: List<DebtDomain>, order: Pair<DebtOrderAttribute, Boolean>): List<DebtDomain> {
        val sortedList: List<DebtDomain>
        when (order.first) {
            DebtOrderAttribute.Date -> {
                if (order.second) {
                    sortedList = debtList.sortedBy { it.id }.asReversed()
                } else {
                    sortedList = debtList.sortedBy { it.id }
                }
            }
            DebtOrderAttribute.Sum -> {
                if (order.second) {
                    sortedList = debtList.sortedWith(comparator = compareBy {it.sum})
                } else {
                    sortedList = debtList.sortedWith(comparator = compareBy {it.sum}).asReversed()
                }
            }
        }
        return sortedList
    }
}
package com.breckneck.deptbook.domain.usecase.Human

import com.breckneck.deptbook.domain.model.HumanDomain
import com.breckneck.deptbook.domain.util.HumanOrderAttribute

class SortHumans {

    fun execute(debtList: List<HumanDomain>, order: Pair<HumanOrderAttribute, Boolean>): List<HumanDomain> {
        val sortedList: List<HumanDomain>
        when (order.first) {
            HumanOrderAttribute.Date -> {
                if (order.second) {
                    sortedList = debtList.sortedBy { it.id }.asReversed()
                } else {
                    sortedList = debtList.sortedBy { it.id }
                }
            }
            HumanOrderAttribute.Sum -> {
                if (order.second) {
                    sortedList = debtList.sortedWith(comparator = compareBy {it.sumDebt})
                } else {
                    sortedList = debtList.sortedWith(comparator = compareBy {it.sumDebt}).asReversed()
                }
            }
        }
        return sortedList
    }
}
package com.breckneck.deptbook.domain.usecase.Debt

import com.breckneck.deptbook.domain.model.DebtDomain
import com.breckneck.deptbook.domain.util.DebtOrderAttribute
import java.text.SimpleDateFormat

class SortDebts {

    fun execute(debtList: List<DebtDomain>, order: Pair<DebtOrderAttribute, Boolean>): List<DebtDomain> {
        val sortedList: List<DebtDomain>
        when (order.first) {
            DebtOrderAttribute.CreationDate -> {
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
            DebtOrderAttribute.Date -> {
                val sdf = SimpleDateFormat("d MMM yyyy")
                if (order.second) {
                    sortedList = debtList.sortedWith(comparator = compareBy { sdf.parse(it.date) }).asReversed()
                } else {
                    sortedList = debtList.sortedWith(comparator = compareBy { sdf.parse(it.date) })
                }
            }
        }
        return sortedList
    }
}
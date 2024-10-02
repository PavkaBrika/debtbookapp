package com.breckneck.deptbook.domain.util

import com.breckneck.deptbook.domain.model.DebtDomain
import com.breckneck.deptbook.domain.model.HumanDomain
import java.text.SimpleDateFormat

fun sortHumans(debtList: List<HumanDomain>, order: Pair<HumanOrderAttribute, Boolean>): List<HumanDomain> {
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

fun sortDebts(debtList: List<DebtDomain>, order: Pair<DebtOrderAttribute, Boolean>): List<DebtDomain> {
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
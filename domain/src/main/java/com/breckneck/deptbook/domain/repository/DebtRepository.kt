package com.breckneck.deptbook.domain.repository

import com.breckneck.deptbook.domain.model.DebtDomain

interface DebtRepository {

    fun getAllDebts(): List<DebtDomain>

    fun getAllDebtsById(id: Int): List<DebtDomain>

    fun replaceAllDebts(debtList: List<DebtDomain>)

    fun setDebt(debtDomain: DebtDomain)

    fun deleteDebt(debtDomain: DebtDomain)

    fun editDebt(debtDomain: DebtDomain)

    fun deleteDebtsByHumanId(id: Int)

    fun getDebtQuantity(): Int
}
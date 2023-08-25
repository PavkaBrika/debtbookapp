package com.breckneck.deptbook.data.storage

import entity.Debt

interface DebtStorage {

    fun getAllDebtsById(id: Int) : List<Debt>

    fun setDebt(debt: Debt)

    fun deleteDebt(debt: Debt)

    fun editDebt(debt: Debt)

    fun deleteDebtsByHumanId(id: Int)
}
package com.breckneck.deptbook.data.storage

import com.breckneck.deptbook.data.storage.entity.Debt

interface DebtStorage {

    fun getAllDebtsById(id: Int) : List<Debt>

    fun setDebt(debt: Debt)
}
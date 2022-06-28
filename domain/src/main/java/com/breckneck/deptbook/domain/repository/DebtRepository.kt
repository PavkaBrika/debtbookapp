package com.breckneck.deptbook.domain.repository

import com.breckneck.deptbook.domain.model.DebtDomain

interface DebtRepository {

    fun getAllDebtsById(id: Int): List<DebtDomain>
}
package com.breckneck.deptbook.data.storage.repository

import com.breckneck.deptbook.data.storage.DebtStorage
import com.breckneck.deptbook.data.storage.entity.Debt
import com.breckneck.deptbook.domain.model.DebtDomain
import com.breckneck.deptbook.domain.repository.DebtRepository

class DebtRepositoryImpl(val debtStorage: DebtStorage): DebtRepository {

    override fun getAllDebtsById(id: Int): List<DebtDomain> {
        val debtList = debtStorage.getAllDebtsById(id = id)
        val humanDomainList = debtList.map {
            DebtDomain(id = it.id, sum = it.sum, currency = it.currency, idHuman = it.idHuman)
        }
        return humanDomainList
    }
}
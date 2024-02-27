package com.breckneck.deptbook.domain.usecase.Debt

import com.breckneck.deptbook.domain.model.DebtDomain
import com.breckneck.deptbook.domain.repository.DebtRepository

class GetAllDebts(private val debtRepository: DebtRepository) {

    fun execute(): List<DebtDomain> {
        return debtRepository.getAllDebts()
    }
}
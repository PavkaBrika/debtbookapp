package com.breckneck.deptbook.domain.usecase.Debt

import com.breckneck.deptbook.domain.model.DebtDomain
import com.breckneck.deptbook.domain.repository.DebtRepository

class ReplaceAllDebts(private val debtRepository: DebtRepository) {

    fun execute(debtList: List<DebtDomain>) {
        debtRepository.replaceAllDebts(debtList = debtList)
    }

}

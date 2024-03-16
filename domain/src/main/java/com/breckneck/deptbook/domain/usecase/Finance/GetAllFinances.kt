package com.breckneck.deptbook.domain.usecase.Finance

import com.breckneck.deptbook.domain.model.Finance
import com.breckneck.deptbook.domain.repository.FinanceRepository

class GetAllFinances(private val financeRepository: FinanceRepository) {

    fun execute(): List<Finance> {
        return financeRepository.getAllFinance()
    }

}
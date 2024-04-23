package com.breckneck.deptbook.domain.usecase.Finance

import com.breckneck.deptbook.domain.model.Finance
import com.breckneck.deptbook.domain.repository.FinanceRepository

class ReplaceAllFinances(private val financeRepository: FinanceRepository) {

    fun execute(financeList: List<Finance>) {
        financeRepository.replaceAllFinances(financeList = financeList)
    }
}
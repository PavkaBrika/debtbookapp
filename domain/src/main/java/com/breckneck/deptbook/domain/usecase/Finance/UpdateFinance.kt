package com.breckneck.deptbook.domain.usecase.Finance

import com.breckneck.deptbook.domain.model.Finance
import com.breckneck.deptbook.domain.repository.FinanceRepository

class UpdateFinance(private val financeRepository: FinanceRepository) {

    fun execute(finance: Finance) {
        financeRepository.updateFinance(finance = finance)
    }
}
package com.breckneck.deptbook.domain.usecase.Finance

import com.breckneck.deptbook.domain.model.Finance
import com.breckneck.deptbook.domain.repository.FinanceRepository

class SetFinance(private val financeRepository: FinanceRepository) {

    fun execute(finance: Finance) {
        financeRepository.setFinance(finance = finance)
    }

}
package com.breckneck.deptbook.domain.usecase.FinanceCategory

import com.breckneck.deptbook.domain.model.FinanceCategory
import com.breckneck.deptbook.domain.repository.FinanceRepository

class GetAllFinanceCategories(private val financeRepository: FinanceRepository) {

    fun execute(): List<FinanceCategory> {
        return financeRepository.getAllFinanceCategories()
    }
}
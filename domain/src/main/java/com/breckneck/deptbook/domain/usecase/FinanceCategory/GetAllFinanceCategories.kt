package com.breckneck.deptbook.domain.usecase.FinanceCategory

import com.breckneck.deptbook.domain.model.FinanceCategory
import com.breckneck.deptbook.domain.repository.FinanceCategoryRepository
import com.breckneck.deptbook.domain.repository.FinanceRepository

class GetAllFinanceCategories(private val financeCategoryRepository: FinanceCategoryRepository) {

    fun execute(): List<FinanceCategory> {
        return financeCategoryRepository.getAllFinanceCategories()
    }
}
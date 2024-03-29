package com.breckneck.deptbook.domain.usecase.FinanceCategory

import com.breckneck.deptbook.domain.model.FinanceCategoryWithFinances
import com.breckneck.deptbook.domain.repository.FinanceCategoryRepository

class GetAllCategoriesWithFinances(private val financeCategoryRepository: FinanceCategoryRepository) {

    fun execute(): List<FinanceCategoryWithFinances> {
        return financeCategoryRepository.getAllCategoriesWithFinances()
    }
}
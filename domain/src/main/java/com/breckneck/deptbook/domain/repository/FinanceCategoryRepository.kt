package com.breckneck.deptbook.domain.repository

import com.breckneck.deptbook.domain.model.FinanceCategory
import com.breckneck.deptbook.domain.model.FinanceCategoryWithFinances
import com.breckneck.deptbook.domain.util.FinanceCategoryState

interface FinanceCategoryRepository {

    fun getAllCategoriesWithFinances(): List<FinanceCategoryWithFinances>

    fun getAllFinanceCategories(): List<FinanceCategory>

    fun setFinanceCategory(category: FinanceCategory)

    fun deleteFinanceCategory(category: FinanceCategory)

    fun getFinanceCategoriesByState(financeCategoryState: FinanceCategoryState): List<FinanceCategory>
}
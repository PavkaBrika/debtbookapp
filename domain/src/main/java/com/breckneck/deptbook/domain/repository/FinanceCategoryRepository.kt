package com.breckneck.deptbook.domain.repository

import com.breckneck.deptbook.domain.model.FinanceCategory
import com.breckneck.deptbook.domain.model.FinanceCategoryWithFinances

interface FinanceCategoryRepository {

    fun getAllCategoriesWithFinances(): List<FinanceCategoryWithFinances>

    fun getAllFinanceCategories(): List<FinanceCategory>

    fun setFinanceCategory(category: FinanceCategory)
}
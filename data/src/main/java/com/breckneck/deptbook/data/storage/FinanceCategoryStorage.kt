package com.breckneck.deptbook.data.storage

import entity.FinanceCategoryData
import entity.relations.FinanceCategoryDataWithFinanceData

interface FinanceCategoryStorage {

    fun getAllCategoriesWithFinances(): List<FinanceCategoryDataWithFinanceData>

    fun getAllFinanceCategories(): List<FinanceCategoryData>

    fun setFinanceCategory(category: FinanceCategoryData)

    fun deleteFinanceCategory(category: FinanceCategoryData)

}
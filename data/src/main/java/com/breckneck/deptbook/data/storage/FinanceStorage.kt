package com.breckneck.deptbook.data.storage

import entity.FinanceData

interface FinanceStorage {

    fun setFinance(finance: FinanceData)

    fun getAllFinance(): List<FinanceData>

    fun getFinanceByCategoryIdAndExpenses(categoryId: Int, isExpenses: Boolean): List<FinanceData>

    fun deleteFinance(financeData: FinanceData)

    fun updateFinance(financeData: FinanceData)
}
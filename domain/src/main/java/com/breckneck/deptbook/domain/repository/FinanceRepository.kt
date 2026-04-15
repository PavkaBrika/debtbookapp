package com.breckneck.deptbook.domain.repository

import com.breckneck.deptbook.domain.model.Finance

interface FinanceRepository {

    fun setFinance(finance: Finance)

    fun getAllFinance(): List<Finance>

    suspend fun getFinanceByCategoryId(categoryId: Int): List<Finance>

    suspend fun deleteFinance(finance: Finance)

    fun updateFinance(finance: Finance)

    fun deleteFinanceByCategoryId(financeCategoryId: Int)

    fun replaceAllFinances(financeList: List<Finance>)
}
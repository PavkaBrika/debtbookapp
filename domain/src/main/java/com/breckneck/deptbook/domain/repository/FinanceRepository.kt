package com.breckneck.deptbook.domain.repository

import com.breckneck.deptbook.domain.model.Finance

interface FinanceRepository {

    fun setFinance(finance: Finance)

    fun getAllFinance(): List<Finance>
}
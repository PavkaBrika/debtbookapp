package com.breckneck.deptbook.domain.repository

import com.breckneck.deptbook.domain.model.Finance
import com.breckneck.deptbook.domain.model.FinanceCategory

interface FinanceRepository {

    fun setFinance(finance: Finance)

    fun getAllFinance(): List<Finance>

    //categories
    fun getAllFinanceCategories(): List<FinanceCategory>
}
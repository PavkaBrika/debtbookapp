package repository

import com.breckneck.deptbook.data.storage.FinanceStorage
import com.breckneck.deptbook.domain.model.Finance
import com.breckneck.deptbook.domain.repository.FinanceRepository
import entity.FinanceData

class FinanceRepositoryImpl(private val financeStorage: FinanceStorage) : FinanceRepository {

    override fun setFinance(finance: Finance) {
        financeStorage.setFinance(
            finance = FinanceData(
                id = finance.id,
                sum = finance.sum,
                isRevenue = finance.isRevenue,
                info = finance.info,
                financeCategoryId = finance.financeCategoryId,
                date = finance.date
            )
        )
    }

    override fun getAllFinance(): List<Finance> {
        return financeStorage.getAllFinance().map { financeData ->
            Finance(
                id = financeData.id,
                sum = financeData.sum,
                isRevenue = financeData.isRevenue,
                info = financeData.info,
                financeCategoryId = financeData.financeCategoryId,
                date = financeData.date
            )
        }
    }

    override fun getFinanceByCategoryIdAndRevenue(
        categoryId: Int,
        isRevenue: Boolean
    ): List<Finance> {
        return financeStorage.getFinanceByCategoryIdAndRevenue(
            categoryId = categoryId,
            isRevenue = isRevenue
        ).map { financeData ->
            Finance(
                id = financeData.id,
                sum = financeData.sum,
                isRevenue = financeData.isRevenue,
                info = financeData.info,
                financeCategoryId = financeData.financeCategoryId,
                date = financeData.date
            )
        }
    }

    override fun deleteFinance(finance: Finance) {
        financeStorage.deleteFinance(
            financeData = FinanceData(
                id = finance.id,
                sum = finance.sum,
                isRevenue = finance.isRevenue,
                date = finance.date,
                info = finance.info,
                financeCategoryId = finance.financeCategoryId
            )
        )
    }

    override fun updateFinance(finance: Finance) {
        financeStorage.updateFinance(
            financeData = FinanceData(
                id = finance.id,
                sum = finance.sum,
                isRevenue = finance.isRevenue,
                date = finance.date,
                info = finance.info,
                financeCategoryId = finance.financeCategoryId
            )
        )
    }
}
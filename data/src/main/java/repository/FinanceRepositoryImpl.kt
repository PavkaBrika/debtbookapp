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
                info = financeData.info,
                financeCategoryId = financeData.financeCategoryId,
                date = financeData.date
            )
        }
    }

    override fun getFinanceByCategoryId(
        categoryId: Int
    ): List<Finance> {
        return financeStorage.getFinanceByCategoryId(
            categoryId = categoryId,
        ).map { financeData ->
            Finance(
                id = financeData.id,
                sum = financeData.sum,
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
                date = finance.date,
                info = finance.info,
                financeCategoryId = finance.financeCategoryId
            )
        )
    }

    override fun deleteFinanceByCategoryId(financeCategoryId: Int) {
        financeStorage.deleteAllFinancesByCategoryId(financeCategoryId = financeCategoryId)
    }

    override fun replaceAllFinances(financeList: List<Finance>) {
        financeStorage.replaceAllFinances(
            financeDataList = financeList.map { finance ->
                FinanceData(
                    id = finance.id,
                    sum = finance.sum,
                    date = finance.date,
                    info = finance.info,
                    financeCategoryId = finance.financeCategoryId
                )
            })
    }
}
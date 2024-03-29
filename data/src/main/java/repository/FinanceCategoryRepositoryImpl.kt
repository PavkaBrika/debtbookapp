package repository

import com.breckneck.deptbook.data.storage.FinanceCategoryStorage
import com.breckneck.deptbook.domain.model.Finance
import com.breckneck.deptbook.domain.model.FinanceCategory
import com.breckneck.deptbook.domain.model.FinanceCategoryWithFinances
import com.breckneck.deptbook.domain.repository.FinanceCategoryRepository
import com.breckneck.deptbook.domain.repository.FinanceRepository
import entity.FinanceCategoryData

class FinanceCategoryRepositoryImpl(private val financeCategoryStorage: FinanceCategoryStorage) :
    FinanceCategoryRepository {

    override fun getAllCategoriesWithFinances(): List<FinanceCategoryWithFinances> {
        return financeCategoryStorage.getAllCategoriesWithFinances().map { financeCategoryWithFinance ->
            FinanceCategoryWithFinances(
                financeCategory = FinanceCategory(
                    id = financeCategoryWithFinance.financeCategoryData.id,
                    name = financeCategoryWithFinance.financeCategoryData.name,
                    color = financeCategoryWithFinance.financeCategoryData.color,
                    image = financeCategoryWithFinance.financeCategoryData.image
                ),
                financeList = financeCategoryWithFinance.financeDataList.map { financeData ->
                    Finance(
                        id = financeData.id,
                        name = financeData.name,
                        sum = financeData.sum,
                        isRevenue = financeData.isRevenue,
                        info = financeData.info
                    )
                }
            )
        }
    }

    override fun getAllFinanceCategories(): List<FinanceCategory> {
        return financeCategoryStorage.getAllFinanceCategories().map { financeCategoryData ->
            FinanceCategory(
                id = financeCategoryData.id,
                name = financeCategoryData.name,
                color = financeCategoryData.color,
                image = financeCategoryData.image
            )
        }
    }

    override fun setFinanceCategory(category: FinanceCategory) {
        financeCategoryStorage.setFinanceCategory(
            FinanceCategoryData(
                id = category.id,
                name = category.name,
                color = category.color,
                image = category.image
            )
        )
    }
}
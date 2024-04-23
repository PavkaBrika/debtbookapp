package repository

import com.breckneck.deptbook.data.storage.FinanceCategoryStorage
import com.breckneck.deptbook.domain.model.Finance
import com.breckneck.deptbook.domain.model.FinanceCategory
import com.breckneck.deptbook.domain.model.FinanceCategoryWithFinances
import com.breckneck.deptbook.domain.repository.FinanceCategoryRepository
import com.breckneck.deptbook.domain.util.FinanceCategoryState
import entity.FinanceCategoryData
import util.FinanceCategoryStateData

class FinanceCategoryRepositoryImpl(private val financeCategoryStorage: FinanceCategoryStorage) :
    FinanceCategoryRepository {

    override fun getAllCategoriesWithFinances(): List<FinanceCategoryWithFinances> {
        return financeCategoryStorage.getAllCategoriesWithFinances()
            .map { financeCategoryWithFinance ->
                FinanceCategoryWithFinances(
                    financeCategory = FinanceCategory(
                        id = financeCategoryWithFinance.financeCategoryData.id,
                        name = financeCategoryWithFinance.financeCategoryData.name,
                        state = when (financeCategoryWithFinance.financeCategoryData.state) {
                            FinanceCategoryStateData.INCOME -> FinanceCategoryState.INCOME
                            FinanceCategoryStateData.EXPENSE -> FinanceCategoryState.EXPENSE
                        },
                        color = financeCategoryWithFinance.financeCategoryData.color,
                        image = financeCategoryWithFinance.financeCategoryData.image
                    ),
                    financeList = financeCategoryWithFinance.financeDataList.map { financeData ->
                        Finance(
                            id = financeData.id,
                            sum = financeData.sum,
                            info = financeData.info,
                            financeCategoryId = financeData.financeCategoryId,
                            date = financeData.date
                        )
                    }.toMutableList()
                )
            }
    }

    override fun getAllFinanceCategories(): List<FinanceCategory> {
        return financeCategoryStorage.getAllFinanceCategories().map { financeCategoryData ->
            FinanceCategory(
                id = financeCategoryData.id,
                name = financeCategoryData.name,
                state = when (financeCategoryData.state) {
                    FinanceCategoryStateData.INCOME -> FinanceCategoryState.INCOME
                    FinanceCategoryStateData.EXPENSE -> FinanceCategoryState.EXPENSE
                },
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
                state = when (category.state) {
                    FinanceCategoryState.INCOME -> FinanceCategoryStateData.INCOME
                    FinanceCategoryState.EXPENSE -> FinanceCategoryStateData.EXPENSE
                },
                color = category.color,
                image = category.image
            )
        )
    }

    override fun deleteFinanceCategory(category: FinanceCategory) {
        financeCategoryStorage.deleteFinanceCategory(
            category = FinanceCategoryData(
                id = category.id,
                name = category.name,
                state = when (category.state) {
                    FinanceCategoryState.INCOME -> FinanceCategoryStateData.INCOME
                    FinanceCategoryState.EXPENSE -> FinanceCategoryStateData.EXPENSE
                },
                color = category.color,
                image = category.image
            )
        )
    }

    override fun getFinanceCategoriesByState(financeCategoryState: FinanceCategoryState): List<FinanceCategory> {
        return financeCategoryStorage.getFinanceCategoriesByState(
            financeCategoryStateData = when (financeCategoryState) {
                FinanceCategoryState.EXPENSE -> FinanceCategoryStateData.EXPENSE
                FinanceCategoryState.INCOME -> FinanceCategoryStateData.INCOME
            }
        ).map { financeCategoryData ->
            FinanceCategory(
                id = financeCategoryData.id,
                name = financeCategoryData.name,
                state = when (financeCategoryData.state) {
                    FinanceCategoryStateData.INCOME -> FinanceCategoryState.INCOME
                    FinanceCategoryStateData.EXPENSE -> FinanceCategoryState.EXPENSE
                },
                color = financeCategoryData.color,
                image = financeCategoryData.image
            )
        }
    }

    override fun replaceAllFinanceCategories(financeCategoriesList: List<FinanceCategory>) {
        financeCategoryStorage.replaceAllFinanceCategories(
            financeCategoryDataList = financeCategoriesList.map { category ->
                FinanceCategoryData(
                    id = category.id,
                    name = category.name,
                    state = when (category.state) {
                        FinanceCategoryState.INCOME -> FinanceCategoryStateData.INCOME
                        FinanceCategoryState.EXPENSE -> FinanceCategoryStateData.EXPENSE
                    },
                    color = category.color,
                    image = category.image
                )
        })
    }
}
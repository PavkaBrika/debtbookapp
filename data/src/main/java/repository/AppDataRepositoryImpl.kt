package repository

import android.content.Context
import com.breckneck.deptbook.domain.model.AppDataLists
import com.breckneck.deptbook.domain.repository.AppDataRepository
import com.breckneck.deptbook.domain.util.FinanceCategoryState
import database.AppDataBase
import entity.Debt
import entity.FinanceCategoryData
import entity.FinanceData
import entity.GoalData
import entity.GoalDepositData
import entity.Human
import util.FinanceCategoryStateData

class AppDataRepositoryImpl(context: Context) : AppDataRepository {

    private val db = AppDataBase(context = context)

    override fun replaceAllAppData(appDataLists: AppDataLists) {
        db.appDao().replaceAllAppData(
            humans = appDataLists.humanList.map {
                Human(id = it.id, name = it.name, sumDebt = it.sumDebt, currency = it.currency)
            },
            debts = appDataLists.debtList.map {
                Debt(id = it.id, sum = it.sum, idHuman = it.idHuman, info = it.info, date = it.date)
            },
            financeCategories = appDataLists.financeCategoryList.map {
                FinanceCategoryData(
                    id = it.id,
                    name = it.name,
                    state = when (it.state) {
                        FinanceCategoryState.INCOME -> FinanceCategoryStateData.INCOME
                        FinanceCategoryState.EXPENSE -> FinanceCategoryStateData.EXPENSE
                    },
                    color = it.color,
                    image = it.image
                )
            },
            finances = appDataLists.financeList.map {
                FinanceData(id = it.id, sum = it.sum, date = it.date, info = it.info, financeCategoryId = it.financeCategoryId)
            },
            goals = appDataLists.goalList.map {
                GoalData(id = it.id, name = it.name, sum = it.sum, savedSum = it.savedSum, currency = it.currency, photoPath = it.photoPath, creationDate = it.creationDate, goalDate = it.goalDate)
            },
            goalDeposits = appDataLists.goalDepositList.map {
                GoalDepositData(id = it.id, sum = it.sum, date = it.date, goalId = it.goalId)
            }
        )
    }
}

package repository

import android.content.Context
import com.breckneck.deptbook.domain.model.AppDataLists
import com.breckneck.deptbook.domain.model.Finance
import com.breckneck.deptbook.domain.model.FinanceCategory
import com.breckneck.deptbook.domain.model.Goal
import com.breckneck.deptbook.domain.model.GoalDeposit
import com.breckneck.deptbook.domain.model.DebtDomain
import com.breckneck.deptbook.domain.model.HumanDomain
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

    override fun getAllAppData(): AppDataLists = db.runInTransaction<AppDataLists> {
        val dao = db.appDao()
        AppDataLists(
            humanList = dao.getAllHuman().map {
                HumanDomain(id = it.id, name = it.name, sumDebt = it.sumDebt, currency = it.currency)
            },
            debtList = dao.getAllDebts().map {
                DebtDomain(id = it.id, sum = it.sum, idHuman = it.idHuman, info = it.info, date = it.date)
            },
            financeCategoryList = dao.getAllFinanceCategories().map {
                FinanceCategory(
                    id = it.id,
                    name = it.name,
                    state = when (it.state) {
                        FinanceCategoryStateData.INCOME -> FinanceCategoryState.INCOME
                        FinanceCategoryStateData.EXPENSE -> FinanceCategoryState.EXPENSE
                    },
                    color = it.color,
                    image = it.image
                )
            },
            financeList = dao.getAllFinances().map {
                Finance(id = it.id, sum = it.sum, date = it.date, info = it.info, financeCategoryId = it.financeCategoryId)
            },
            goalList = dao.getAllGoals().map {
                Goal(id = it.id, name = it.name, sum = it.sum, savedSum = it.savedSum, currency = it.currency, photoPath = it.photoPath, creationDate = it.creationDate, goalDate = it.goalDate)
            },
            goalDepositList = dao.getAllGoalDeposits().map {
                GoalDeposit(id = it.id, sum = it.sum, date = it.date, goalId = it.goalId)
            }
        )
    }

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

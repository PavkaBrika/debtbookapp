package repository

import com.breckneck.deptbook.data.storage.GoalDepositStorage
import com.breckneck.deptbook.domain.model.GoalDeposit
import com.breckneck.deptbook.domain.repository.GoalDepositRepository
import entity.GoalDepositData

class GoalDepositRepositoryImpl(private val goalDepositStorage: GoalDepositStorage) :
    GoalDepositRepository {

    override fun setGoalDeposit(goalDeposit: GoalDeposit) {
        goalDepositStorage.setGoalDepositData(
            goalDepositData = GoalDepositData(
                id = goalDeposit.id,
                sum = goalDeposit.sum,
                date = goalDeposit.date,
                goalId = goalDeposit.goalId
            )
        )
    }

    override fun getGoalDepositsByGoalId(goalId: Int): List<GoalDeposit> {
        return goalDepositStorage.getGoalDepositsDataByGoalId(goalId = goalId)
            .map { GoalDeposit(id = it.id, sum = it.sum, date = it.date, goalId = it.goalId) }
    }

    override fun deleteGoalDepositsByGoalId(goalId: Int) {
        goalDepositStorage.deleteGoalDepositsDataByGoalId(goalId = goalId)
    }

    override fun replaceAllGoalDeposits(goalDepositList: List<GoalDeposit>) {
        goalDepositStorage.replaceAllGoalDepositData(
            goalDepositDataList = goalDepositList.map { goalDeposit ->
                GoalDepositData(
                    id = goalDeposit.id,
                    sum = goalDeposit.sum,
                    date = goalDeposit.date,
                    goalId = goalDeposit.goalId
                )
            })
    }

    override fun getAllGoalDeposits(): List<GoalDeposit> {
        return goalDepositStorage.getAllGoalDepositsData().map {
            GoalDeposit(id = it.id, sum = it.sum, date = it.date, goalId = it.goalId)
        }
    }
}
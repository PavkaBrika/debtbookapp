package repository

import com.breckneck.deptbook.data.storage.GoalStorage
import com.breckneck.deptbook.domain.model.Goal
import com.breckneck.deptbook.domain.repository.GoalRepository
import entity.GoalData

class GoalRepositoryImpl(private val goalStorage: GoalStorage) : GoalRepository {

    override fun getAllGoals(): List<Goal> {
        return goalStorage.getAllGoals().map { goalData ->
            Goal(
                id = goalData.id,
                name = goalData.name,
                sum = goalData.sum,
                savedSum = goalData.savedSum,
                creationDate = goalData.creationDate,
                goalDate = goalData.goalDate,
                currency = goalData.currency
            )
        }
    }

    override fun setGoal(goal: Goal) {
        goalStorage.setGoal(
            goal = GoalData(
                id = goal.id,
                name = goal.name,
                sum = goal.sum,
                savedSum = goal.savedSum,
                creationDate = goal.creationDate,
                goalDate = goal.goalDate,
                currency = goal.currency
            )
        )
    }
}
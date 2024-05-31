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
                currency = goalData.currency,
                photoPath = goalData.photoPath
            )
        }
    }

    override fun setGoal(goal: Goal) {
        goalStorage.setGoal(
            goalData = GoalData(
                id = goal.id,
                name = goal.name,
                sum = goal.sum,
                savedSum = goal.savedSum,
                creationDate = goal.creationDate,
                goalDate = goal.goalDate,
                currency = goal.currency,
                photoPath = goal.photoPath
            )
        )
    }

    override fun updateGoal(goal: Goal) {
        goalStorage.updateGoal(
            goalData = GoalData(
                id = goal.id,
                name = goal.name,
                sum = goal.sum,
                savedSum = goal.savedSum,
                creationDate = goal.creationDate,
                goalDate = goal.goalDate,
                currency = goal.currency,
                photoPath = goal.photoPath
            )
        )
    }

    override fun deleteGoal(goal: Goal) {
        goalStorage.deleteGoal(
            goalData = GoalData(
                id = goal.id,
                name = goal.name,
                sum = goal.sum,
                savedSum = goal.savedSum,
                creationDate = goal.creationDate,
                goalDate = goal.goalDate,
                currency = goal.currency,
                photoPath = goal.photoPath
            )
        )
    }

    override fun replaceAllGoals(goalList: List<Goal>) {
        goalStorage.replaceAllGoals(goalList = goalList.map {
            GoalData(
                id = it.id,
                name = it.name,
                sum = it.sum,
                savedSum = it.savedSum,
                currency = it.currency,
                photoPath = it.photoPath,
                creationDate = it.creationDate,
                goalDate = it.goalDate
            )
        })
    }
}
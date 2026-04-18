package com.breckneck.debtbook.goal.main

import android.util.Log
import androidx.lifecycle.ViewModel
import com.breckneck.debtbook.goal.main.GoalsAction
import com.breckneck.deptbook.domain.model.Goal
import com.breckneck.deptbook.domain.model.GoalDeposit
import com.breckneck.deptbook.domain.usecase.Goal.DeleteGoal
import com.breckneck.deptbook.domain.usecase.Goal.GetAllGoals
import com.breckneck.deptbook.domain.usecase.Goal.UpdateGoal
import com.breckneck.deptbook.domain.usecase.GoalDeposit.DeleteGoalDepositsByGoalId
import com.breckneck.deptbook.domain.usecase.GoalDeposit.SetGoalDeposit
import com.breckneck.deptbook.domain.util.ListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val getAllGoals: GetAllGoals,
    private val updateGoal: UpdateGoal,
    private val deleteGoal: DeleteGoal,
    private val deleteGoalDepositsByGoalId: DeleteGoalDepositsByGoalId,
    private val setGoalDeposit: SetGoalDeposit,
) : ViewModel(), ContainerHost<GoalsState, GoalsSideEffect> {

    private val TAG = "GoalsFragmentVM"

    override val container = container<GoalsState, GoalsSideEffect>(
        initialState = GoalsState.Companion.initial(),
        onCreate = { loadGoals() },
    )

    init {
        Log.e(TAG, "Created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.e(TAG, "Cleared")
    }

    fun onAction(action: GoalsAction) = when (action) {
        GoalsAction.AddGoalClick -> intent {
            postSideEffect(GoalsSideEffect.NavigateToAddGoal)
        }
        is GoalsAction.GoalClick -> intent {
            postSideEffect(GoalsSideEffect.NavigateToGoalDetails(action.goal))
        }
        is GoalsAction.AddGoalDeposit -> addGoalDeposit(action.goal, action.sum)
        is GoalsAction.DeleteGoal -> deleteGoal(action.goal)
        is GoalsAction.RefreshAfterNavigation -> refreshAfterNavigation(action.wasModified)
    }

    private fun loadGoals() = intent {
        reduce { state.copy(listState = ListState.LOADING) }
        try {
            val goals = withContext(Dispatchers.IO) { getAllGoals.execute() }
            reduce {
                state.copy(
                    goalList = goals,
                    listState = if (goals.isEmpty()) ListState.EMPTY else ListState.RECEIVED,
                )
            }
            Log.e(TAG, "Goals loaded")
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }
    }

    private fun addGoalDeposit(goal: Goal, sum: Double) = intent {
        val updatedGoal = goal.copy(savedSum = goal.savedSum + sum)
        try {
            withContext(Dispatchers.IO) {
                updateGoal.execute(goal = updatedGoal)
                setGoalDeposit.execute(
                    goalDeposit = GoalDeposit(sum = sum, date = Date(), goalId = goal.id)
                )
            }
            val updatedList = state.goalList.toMutableList().apply {
                val idx = indexOfFirst { it.id == goal.id }
                if (idx != -1) this[idx] = updatedGoal
            }
            reduce {
                state.copy(goalList = updatedList)
            }
            Log.e(TAG, "Goal deposit added")
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }
    }

    private fun deleteGoal(goal: Goal) = intent {
        try {
            withContext(Dispatchers.IO) {
                deleteGoal.execute(goal = goal)
                deleteGoalDepositsByGoalId.execute(goalId = goal.id)
            }
            val updatedList = state.goalList.filter { it.id != goal.id }
            reduce {
                state.copy(
                    goalList = updatedList,
                    listState = if (updatedList.isEmpty()) ListState.EMPTY else ListState.RECEIVED,
                )
            }
            Log.e(TAG, "Goal deleted")
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }
    }

    private fun refreshAfterNavigation(wasModified: Boolean) = intent {
        if (!wasModified) return@intent
        loadGoals()
    }
}
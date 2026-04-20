package com.breckneck.debtbook.goal.main

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.breckneck.debtbook.common.empty
import com.breckneck.debtbook.goal.main.mapper.toUi
import com.breckneck.debtbook.goal.main.model.GoalUi
import com.breckneck.deptbook.domain.model.Goal
import com.breckneck.deptbook.domain.model.GoalDeposit
import com.breckneck.deptbook.domain.usecase.Goal.DeleteGoal
import com.breckneck.deptbook.domain.usecase.Goal.GetAllGoals
import com.breckneck.deptbook.domain.usecase.Goal.UpdateGoal
import com.breckneck.deptbook.domain.usecase.GoalDeposit.DeleteGoalDepositsByGoalId
import com.breckneck.deptbook.domain.usecase.GoalDeposit.SetGoalDeposit
import com.breckneck.deptbook.domain.util.ListState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class GoalsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getAllGoals: GetAllGoals,
    private val updateGoal: UpdateGoal,
    private val deleteGoal: DeleteGoal,
    private val deleteGoalDepositsByGoalId: DeleteGoalDepositsByGoalId,
    private val setGoalDeposit: SetGoalDeposit,
) : ViewModel(), ContainerHost<GoalsState, GoalsSideEffect> {

    private val TAG = "GoalsViewModel"

    private var domainGoals: List<Goal> = emptyList()

    override val container = container<GoalsState, GoalsSideEffect>(
        initialState = GoalsState.initial(),
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
        GoalsAction.AddGoalClick -> navigateToAddGoal()
        is GoalsAction.GoalClick -> navigateToGoalDetails(action.goalUi)
        is GoalsAction.ShowAddDepositPopup -> showAddDepositPopup(action.goalUi)
        GoalsAction.DismissAddDepositPopup -> dismissAddDepositPopup()
        is GoalsAction.UpdateDepositSum -> updateDepositSum(action.text)
        GoalsAction.AddGoalDeposit -> addGoalDeposit()
        is GoalsAction.DeleteGoal -> deleteGoal(action.goalUi)
        is GoalsAction.RefreshAfterNavigation -> refreshAfterNavigation(action.wasModified)
    }

    private fun navigateToAddGoal() = intent {
        postSideEffect(GoalsSideEffect.NavigateToAddGoal)
    }

    private fun navigateToGoalDetails(goalUi: GoalUi) = intent {
        val goal = domainGoals.firstOrNull { it.id == goalUi.goalId } ?: return@intent
        postSideEffect(GoalsSideEffect.NavigateToGoalDetails(goal))
    }

    private fun showAddDepositPopup(goalUi: GoalUi) = intent {
        reduce {
            state.copy(
                addDepositPopup = state.addDepositPopup.copy(
                    isVisible = true,
                    selectedGoalId = goalUi.goalId
                )
            )
        }
    }

    private fun dismissAddDepositPopup() = intent {
        reduce { state.copy(addDepositPopup = AddDepositPopup.initial()) }
    }

    private fun updateDepositSum(text: String) = intent {
        val dotIndex = text.indexOf('.')
        val isValid = if (dotIndex != -1) {
            val beforeDot = text.substring(0, dotIndex)
            val afterDot = text.substring(dotIndex + 1)
            beforeDot.isNotEmpty() && afterDot.length <= 2
        } else {
            true
        }
        if (isValid) {
            reduce {
                state.copy(
                    addDepositPopup = state.addDepositPopup.copy(
                        sumText = text,
                        inputError = null,
                    )
                )
            }
        }
    }

    private fun loadGoals() = intent {
        reduce { state.copy(listState = ListState.LOADING) }
        try {
            val goals = withContext(Dispatchers.IO) { getAllGoals.execute() }
            domainGoals = goals
            val goalUiList = goals.map { it.toUi(context) }
            reduce {
                state.copy(
                    goalList = goalUiList,
                    listState = if (goalUiList.isEmpty()) ListState.EMPTY else ListState.RECEIVED,
                )
            }
            Log.e(TAG, "Goals loaded")
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }
    }

    private fun addGoalDeposit() = intent {
        val goalId = state.addDepositPopup.selectedGoalId ?: return@intent
        val goal = domainGoals.firstOrNull { it.id == goalId } ?: return@intent
        val sumText = state.addDepositPopup.sumText.trim()
        val parsed = sumText.toDoubleOrNull()
        when {
            sumText.isEmpty() -> {
                reduce {
                    state.copy(
                        addDepositPopup = state.addDepositPopup.copy(
                            inputError = AddDepositPopup.DepositInputError.EMPTY
                        )
                    )
                }
                return@intent
            }

            parsed == null || parsed == 0.0 -> {
                reduce {
                    state.copy(
                        addDepositPopup = state.addDepositPopup.copy(
                            inputError = AddDepositPopup.DepositInputError.ZERO_OR_INVALID
                        )
                    )
                }
                return@intent
            }
        }
        val updatedGoal = goal.copy(savedSum = goal.savedSum + parsed)
        try {
            withContext(Dispatchers.IO) {
                updateGoal.execute(goal = updatedGoal)
                setGoalDeposit.execute(
                    goalDeposit = GoalDeposit(sum = parsed, date = Date(), goalId = goal.id)
                )
            }
            domainGoals = domainGoals.toMutableList().apply {
                val idx = indexOfFirst { it.id == goal.id }
                if (idx != -1) this[idx] = updatedGoal
            }
            val updatedList = state.goalList.toMutableList().apply {
                val idx = indexOfFirst { it.goalId == goal.id }
                if (idx != -1) this[idx] = updatedGoal.toUi(context)
            }
            reduce {
                state.copy(
                    goalList = updatedList,
                    addDepositPopup = AddDepositPopup.initial(),
                )
            }
            Log.e(TAG, "Goal deposit added")
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }
    }

    private fun deleteGoal(goalUi: GoalUi) = intent {
        val goal = domainGoals.firstOrNull { it.id == goalUi.goalId } ?: return@intent
        try {
            withContext(Dispatchers.IO) {
                deleteGoal.execute(goal = goal)
                deleteGoalDepositsByGoalId.execute(goalId = goal.id)
            }
            domainGoals = domainGoals.filter { it.id != goal.id }
            val updatedList = state.goalList.filter { it.goalId != goal.id }
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

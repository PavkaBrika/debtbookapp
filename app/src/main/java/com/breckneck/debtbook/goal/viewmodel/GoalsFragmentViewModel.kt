package com.breckneck.debtbook.goal.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class GoalsFragmentViewModel @Inject constructor(
    private val getAllGoals: GetAllGoals,
    private val updateGoal: UpdateGoal,
    private val deleteGoal: DeleteGoal,
    private val deleteGoalDepositsByGoalId: DeleteGoalDepositsByGoalId,
    private val setGoalDeposit: SetGoalDeposit
) : ViewModel() {

    private val TAG = "GoalsFragmentVM"

    private val _goalList = MutableLiveData<List<Goal>>()
    val goalList: LiveData<List<Goal>>
        get() = _goalList
    private val _goalListState = MutableLiveData<ListState>(ListState.LOADING)
    val goalListState: LiveData<ListState>
        get() = _goalListState

    private var _isAddSumDialogOpened = false
    val isAddSumDialogOpened: Boolean
        get() = _isAddSumDialogOpened
    private var _changedGoal: Goal? = null
    val changedGoal: Goal?
        get() = _changedGoal
    private var _changedGoalPosition: Int? = null
    val changedGoalPosition: Int?
        get() = _changedGoalPosition
    private var _goalListNeedToUpdate = false
    val goalListNeedToUpdate: Boolean
        get() = _goalListNeedToUpdate

    init {
        Log.e(TAG, "initialized")
        getAllGoals()
    }

    fun getAllGoals() {
        _goalListState.value = ListState.LOADING
        viewModelScope.launch {
            try {
                val goals = withContext(Dispatchers.IO) { getAllGoals.execute() }
                _goalList.value = goals
                _goalListState.value = if (goals.isEmpty()) ListState.EMPTY else ListState.RECEIVED
                _goalListNeedToUpdate = false
                Log.e(TAG, "Goals loaded")
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }
    }

    fun updateGoalInList(goal: Goal) {
        val list = _goalList.value?.toMutableList() ?: return
        val idx = list.indexOfFirst { it.id == goal.id }
        if (idx != -1) {
            list[idx] = goal
            _goalList.value = list
        }
    }

    fun setListState(state: ListState) {
        _goalListState.value = state
    }

    fun onOpenAddSavedSumChangeDialog(changedGoal: Goal, changedGoalPosition: Int) {
        _isAddSumDialogOpened = true
        _changedGoal = changedGoal
        _changedGoalPosition = changedGoalPosition
    }

    fun onCloseAddSumChangeDialog() {
        _isAddSumDialogOpened = false
        _changedGoal = null
        _changedGoalPosition = null
    }

    fun updateGoal(goal: Goal) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) { updateGoal.execute(goal = goal) }
                Log.e(TAG, "goal updated")
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }
    }

    fun deleteGoal(goal: Goal) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    deleteGoal.execute(goal = goal)
                    deleteGoalDepositsByGoalId.execute(goalId = goal.id)
                }
                val updatedList = _goalList.value?.filter { it.id != goal.id } ?: emptyList()
                _goalList.value = updatedList
                _goalListState.value = if (updatedList.isEmpty()) ListState.EMPTY else ListState.RECEIVED
                _goalListNeedToUpdate = false
                Log.e(TAG, "goal deleted")
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }
    }

    fun setGoalDeposit(goalDeposit: GoalDeposit) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) { setGoalDeposit.execute(goalDeposit = goalDeposit) }
                Log.e(TAG, "goal deposit added")
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.e(TAG, "cleared")
    }
}

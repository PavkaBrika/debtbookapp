package com.breckneck.debtbook.goal.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.breckneck.deptbook.domain.model.Goal
import com.breckneck.deptbook.domain.model.GoalDeposit
import com.breckneck.deptbook.domain.usecase.Goal.DeleteGoal
import com.breckneck.deptbook.domain.usecase.Goal.UpdateGoal
import com.breckneck.deptbook.domain.usecase.GoalDeposit.DeleteGoalDepositsByGoalId
import com.breckneck.deptbook.domain.usecase.GoalDeposit.GetGoalDepositsByGoalId
import com.breckneck.deptbook.domain.usecase.GoalDeposit.SetGoalDeposit
import com.breckneck.deptbook.domain.util.ChangeGoalSavedSumDialogState
import com.breckneck.deptbook.domain.util.ListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class GoalDetailsFragmentViewModel @Inject constructor(
    private val updateGoal: UpdateGoal,
    private val setGoalDeposit: SetGoalDeposit,
    private val getGoalDepositsByGoalId: GetGoalDepositsByGoalId,
    private val deleteGoal: DeleteGoal,
    private val deleteGoalDepositsByGoalId: DeleteGoalDepositsByGoalId
): ViewModel() {

    private val TAG = "GoalDetailsFragmentVM"

    private val _goalDepositList = MutableLiveData<List<GoalDeposit>>()
    val goalDepositList: LiveData<List<GoalDeposit>>
        get() = _goalDepositList
    private val _goalDepositListState = MutableLiveData(ListState.LOADING)
    val goalDepositListState: LiveData<ListState>
        get() = _goalDepositListState
    private var _isGoalDepositListNeedToUpdate = false
    val isGoalDepositListNeedToUpdate: Boolean
        get() = _isGoalDepositListNeedToUpdate
    private val _goalSavedSum = MutableLiveData<Double>()
    val goalSavedSum: LiveData<Double>
        get() = _goalSavedSum
    private val _goal = MutableLiveData<Goal>()
    val goal: LiveData<Goal>
        get() = _goal
    private var _isChangeSavedSumDialogOpened = false
    val isChangeSavedSumDialogOpened: Boolean
        get() = _isChangeSavedSumDialogOpened
    private var _changeDialogState: ChangeGoalSavedSumDialogState? = null
    val changeDialogState: ChangeGoalSavedSumDialogState?
        get() = _changeDialogState
    private var _isEditOptionsDialogOpened = false
    val isEditOptionsDialogOpened: Boolean
        get() = _isEditOptionsDialogOpened

    init {
        Log.e(TAG, "Created")
    }

    fun getGoalFromDB(id: Int) {

    }

    fun setGoal(goal: Goal) {
        _goal.value = goal
        _goalSavedSum.value = goal.savedSum
        getGoalDepositList()
    }

    fun getGoalDepositList() {
        _goalDepositListState.value = ListState.LOADING
        viewModelScope.launch {
            try {
                val list = withContext(Dispatchers.IO) {
                    getGoalDepositsByGoalId.execute(goalId = _goal.value!!.id)
                }
                if (list.isEmpty()) {
                    _goalDepositListState.value = ListState.EMPTY
                } else {
                    _goalDepositList.value = list
                }
                _isGoalDepositListNeedToUpdate = false
                Log.e(TAG, "Goal deposit list loaded")
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }
    }

    fun updateGoalSum(sum: Double) {
        viewModelScope.launch {
            try {
                val goal = _goal.value!!
                goal.savedSum = goal.savedSum + sum
                withContext(Dispatchers.IO) {
                    updateGoal.execute(goal = goal)
                }
                _goalSavedSum.value = goal.savedSum
                Log.e(TAG, "Goal updated")
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }
    }

    fun setGoalDeposit(goalDeposit: GoalDeposit) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) { setGoalDeposit.execute(goalDeposit = goalDeposit) }
                Log.e(TAG, "Goal deposit added")
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
                Log.e(TAG, "Goal deleted")
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }
    }

    fun setGoalListState(state: ListState) {
        _goalDepositListState.value = state
    }

    fun onOpenChangeSavedSumChangeDialog(state: ChangeGoalSavedSumDialogState) {
        _isChangeSavedSumDialogOpened = true
        _changeDialogState = state
    }

    fun onCloseChangeSumChangeDialog() {
        _isChangeSavedSumDialogOpened = false
        _changeDialogState = null
    }

    fun onOpenEditOptionsDialog() {
        _isEditOptionsDialogOpened = true
    }

    fun onCloseEditOptionsDialog() {
        _isEditOptionsDialogOpened = false
    }

    override fun onCleared() {
        super.onCleared()
        Log.e(TAG, "Cleared")
    }
}

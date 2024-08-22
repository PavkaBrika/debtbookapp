package com.breckneck.debtbook.goal.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.breckneck.deptbook.domain.model.Goal
import com.breckneck.deptbook.domain.model.GoalDeposit
import com.breckneck.deptbook.domain.usecase.Goal.DeleteGoal
import com.breckneck.deptbook.domain.usecase.Goal.UpdateGoal
import com.breckneck.deptbook.domain.usecase.GoalDeposit.DeleteGoalDepositsByGoalId
import com.breckneck.deptbook.domain.usecase.GoalDeposit.GetGoalDepositsByGoalId
import com.breckneck.deptbook.domain.usecase.GoalDeposit.SetGoalDeposit
import com.breckneck.deptbook.domain.util.ChangeGoalSavedSumDialogState
import com.breckneck.deptbook.domain.util.ListState
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class GoalDetailsFragmentViewModel(
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

    private val disposeBag = CompositeDisposable()

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
        val result = Single.create {
            it.onSuccess(getGoalDepositsByGoalId.execute(goalId = _goal.value!!.id))
        }
            .subscribeOn(Schedulers.io())
            .doOnSubscribe {
                _goalDepositListState.value = ListState.LOADING
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ list ->
                if (list.isEmpty()) {
                    _goalDepositListState.value = ListState.EMPTY
                } else {
                    _goalDepositList.value = list
                }
                _isGoalDepositListNeedToUpdate = false
                Log.e(TAG, "Goal deposit list loaded")
            }, {
                Log.e(TAG, it.message.toString())
            })
        disposeBag.add(result)
    }

    fun updateGoalSum(sum: Double) {
        val result = Completable.create {
            _goal.value!!.savedSum = _goal.value!!.savedSum + sum
            updateGoal.execute(goal = _goal.value!!)
            it.onComplete()
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _goalSavedSum.value = _goal.value!!.savedSum
                Log.e(TAG, "Goal updated")
            }, {
                Log.e(TAG, it.message.toString())
            })
        disposeBag.add(result)
    }

    fun setGoalDeposit(goalDeposit: GoalDeposit) {
        val result = Completable.create {
            setGoalDeposit.execute(goalDeposit = goalDeposit)
            it.onComplete()
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e(TAG, "Goal deposit added")
            }, {
                Log.e(TAG, it.message.toString())
            })
        disposeBag.add(result)
    }

    fun deleteGoal(goal: Goal) {
        val result = Completable.create {
            deleteGoal.execute(goal = goal)
            deleteGoalDepositsByGoalId.execute(goalId = goal.id)
            it.onComplete()
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e(TAG, "Goal deleted")
            }, {
                Log.e(TAG, it.message.toString())
            })
        disposeBag.add(result)
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
        disposeBag.clear()
        Log.e(TAG, "Cleared")
    }
}
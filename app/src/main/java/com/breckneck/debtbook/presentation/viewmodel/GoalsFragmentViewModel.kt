package com.breckneck.debtbook.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.breckneck.deptbook.domain.model.Goal
import com.breckneck.deptbook.domain.model.GoalDeposit
import com.breckneck.deptbook.domain.usecase.Goal.DeleteGoal
import com.breckneck.deptbook.domain.usecase.Goal.GetAllGoals
import com.breckneck.deptbook.domain.usecase.Goal.UpdateGoal
import com.breckneck.deptbook.domain.usecase.GoalTransaction.DeleteGoalDepositsByGoalId
import com.breckneck.deptbook.domain.usecase.GoalTransaction.SetGoalDeposit
import com.breckneck.deptbook.domain.util.ListState
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class GoalsFragmentViewModel(
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

    private val disposeBag = CompositeDisposable()

    init {
        Log.e(TAG, "initialized")
        getAllGoals()
    }

    fun getAllGoals() {
        val result = Single.create {
            it.onSuccess(getAllGoals.execute())
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                _goalListState.value = ListState.LOADING
            }
            .subscribe({
                _goalList.value = it
                if (_goalList.value!!.isEmpty())
                    _goalListState.value = ListState.EMPTY
                _goalListNeedToUpdate = false
                Log.e(TAG, "Goals loaded")
            }, {
                Log.e(TAG, it.message.toString())
            })
        disposeBag.add(result)
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
        val result = Completable.create {
            updateGoal.execute(goal = goal)
            it.onComplete()
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e(TAG, "goal updated")
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
                _goalListNeedToUpdate = true
                Log.e(TAG, "goal deleted")
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
                Log.e(TAG, "goal deposit added")
            }, {
                Log.e(TAG, it.message.toString())
            })
        disposeBag.add(result)
    }

    override fun onCleared() {
        super.onCleared()
        disposeBag.clear()
        Log.e(TAG, "cleared")
    }
}
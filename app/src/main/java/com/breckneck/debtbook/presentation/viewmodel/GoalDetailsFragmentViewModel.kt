package com.breckneck.debtbook.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.breckneck.deptbook.domain.model.Goal
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.util.Date

class GoalDetailsFragmentViewModel: ViewModel() {

    private val TAG = "GoalDetailsFragmentVM"

//    private val _goalName = MutableLiveData<String>()
//    val goalName: LiveData<String>
//        get() = _goalName
//    private val _goalSum = MutableLiveData<Double>()
//    val goalSum: LiveData<Double>
//        get() = _goalSum
//    private val _goalSavedSum = MutableLiveData<Double>()
//    val goalSavedSum: LiveData<Double>
//        get() = _goalSavedSum
//    private val _goalCurrency = MutableLiveData<String>()
//    val goalCurrency: LiveData<String>
//        get() = _goalCurrency
//    private val _photoPath = MutableLiveData<String?>()
//    val photoPath: LiveData<String?>
//        get() = _photoPath
//    private val _goalDate = MutableLiveData<Date?>()
//    val goalData: LiveData<Date?>
//        get() = _goalDate
    private val _goal = MutableLiveData<Goal>()
    val goal: LiveData<Goal>
        get() = _goal


    private val disposeBag = CompositeDisposable()

    init {
        Log.e(TAG, "Created")
    }

    fun setGoal(goal: Goal) {
        _goal.value = goal
//        _goalName.value = goal.name
//        _goalSum.value = goal.sum
//        _goalSavedSum.value = goal.savedSum
//        _goalCurrency.value = goal.currency
//        _photoPath.value = goal.photoPath
//        _goalDate.value = goal.goalDate
    }

    override fun onCleared() {
        super.onCleared()
        disposeBag.clear()
        Log.e(TAG, "Cleared")
    }
}
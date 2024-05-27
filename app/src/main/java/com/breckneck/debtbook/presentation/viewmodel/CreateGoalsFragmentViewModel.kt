package com.breckneck.debtbook.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.breckneck.deptbook.domain.model.Goal
import com.breckneck.deptbook.domain.usecase.Goal.SetGoal
import com.breckneck.deptbook.domain.usecase.Settings.GetDefaultCurrency
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.Date

class CreateGoalsFragmentViewModel(
    private val setGoal: SetGoal,
    private val getDefaultCurrency: GetDefaultCurrency
): ViewModel() {

    private val TAG = "CreateGoalsFragmentVM"

    private val _goalDate = MutableLiveData<Date>()
    val goalDate: LiveData<Date>
        get() = _goalDate
    private val _selectedCurrencyPosition = MutableLiveData<Int>()
    val selectedCurrencyPosition: LiveData<Int>
        get() = _selectedCurrencyPosition
    private val _isCurrencyDialogOpened = MutableLiveData<Boolean>(false)
    val isCurrencyDialogOpened: LiveData<Boolean>
        get() = _isCurrencyDialogOpened
    private val _currency = MutableLiveData<String>()
    val currency: LiveData<String>
        get() = _currency

    private val disposeBag = CompositeDisposable()

    init {
        Log.e(TAG, "Initialized")
    }

    fun setGoal(goal: Goal) {
        val result = Completable.create {
            setGoal.execute(goal = goal)
            it.onComplete()
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e(TAG, "finance added")
            }, {
                Log.e(TAG, it.message.toString())
            })
        disposeBag.add(result)
    }

    fun onCurrencyDialogOpen(selectedCurrencyPosition: Int) {
        _isCurrencyDialogOpened.value = true
        _selectedCurrencyPosition.value = selectedCurrencyPosition
    }

    fun onCurrencyDialogClose() {
        _isCurrencyDialogOpened.value = false
    }

    fun setCurrency(currency: String) {
        _currency.value = currency
    }

    private fun getDefaultCurrency() {
        _currency.value = getDefaultCurrency.execute()
    }

    override fun onCleared() {
        super.onCleared()
        disposeBag.clear()
        Log.e(TAG, "Cleared")
    }
}
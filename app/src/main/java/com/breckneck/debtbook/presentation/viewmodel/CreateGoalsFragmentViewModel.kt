package com.breckneck.debtbook.presentation.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.breckneck.deptbook.domain.model.Goal
import com.breckneck.deptbook.domain.usecase.Goal.SetGoal
import com.breckneck.deptbook.domain.usecase.Goal.UpdateGoal
import com.breckneck.deptbook.domain.usecase.Settings.GetDefaultCurrency
import com.breckneck.deptbook.domain.util.CreateFragmentState
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.Date

class CreateGoalsFragmentViewModel(
    private val setGoal: SetGoal,
    private val getDefaultCurrency: GetDefaultCurrency,
    private val updateGoal: UpdateGoal
): ViewModel() {

    private val TAG = "CreateGoalsFragmentVM"

    private var _goalDate = MutableLiveData<Date>()
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
    private val _imageUri = MutableLiveData<Uri?>(null)
    val imageUri: LiveData<Uri?>
        get() = _imageUri
    private val _imagePath = MutableLiveData<String?>(null)
    val imagePath: LiveData<String?>
        get() = _imagePath
    private var _createFragmentState: CreateFragmentState? = null
    val createFragmentState: CreateFragmentState?
        get() = _createFragmentState
    private var _goal: Goal? = null
    val goal: Goal?
        get() = _goal

    private val disposeBag = CompositeDisposable()

    init {
        Log.e(TAG, "Initialized")
        getDefaultCurrency()
    }

    fun setGoal(goal: Goal) {
        val result = Completable.create {
            setGoal.execute(goal = goal)
            it.onComplete()
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e(TAG, "Goal added")
            }, {
                Log.e(TAG, it.message.toString())
            })
        disposeBag.add(result)
    }

    fun editGoal(goal: Goal) {
        var result = Completable.create {
            updateGoal.execute(goal = goal)
            it.onComplete()
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e(TAG, "Goal edited")
            }, {
                Log.e(TAG, it.message.toString())
            })
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

    fun setGoalDate(date: Date) {
        _goalDate.value = date
    }

    fun setImageUri(uri: Uri?) {
        _imageUri.value = uri
    }

    fun setImagePath(path: String?) {
        _imagePath.value = path
    }

    private fun getDefaultCurrency() {
        _currency.value = getDefaultCurrency.execute()
    }

    fun setGoalEdit(goal: Goal) {
        _goal = goal
    }

    fun setCreateFragmentState(state: CreateFragmentState) {
        _createFragmentState = state
    }

    override fun onCleared() {
        super.onCleared()
        disposeBag.clear()
        Log.e(TAG, "Cleared")
    }
}
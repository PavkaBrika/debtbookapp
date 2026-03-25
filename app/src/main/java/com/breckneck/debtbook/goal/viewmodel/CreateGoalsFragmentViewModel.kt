package com.breckneck.debtbook.goal.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.breckneck.deptbook.domain.model.Goal
import com.breckneck.deptbook.domain.usecase.Goal.SetGoal
import com.breckneck.deptbook.domain.usecase.Goal.UpdateGoal
import com.breckneck.deptbook.domain.usecase.Settings.GetDefaultCurrency
import com.breckneck.deptbook.domain.util.CreateFragmentState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CreateGoalsFragmentViewModel @Inject constructor(
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

    init {
        Log.e(TAG, "Initialized")
        getDefaultCurrency()
    }

    fun setGoal(goal: Goal) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) { setGoal.execute(goal = goal) }
                Log.e(TAG, "Goal added")
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }
    }

    fun editGoal(goal: Goal) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) { updateGoal.execute(goal = goal) }
                Log.e(TAG, "Goal edited")
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }
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
        Log.e(TAG, "Cleared")
    }
}

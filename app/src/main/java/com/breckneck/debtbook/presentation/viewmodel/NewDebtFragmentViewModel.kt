package com.breckneck.debtbook.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.breckneck.deptbook.domain.usecase.Debt.GetCurrentDateUseCase
import com.breckneck.deptbook.domain.usecase.Debt.SetDateUseCase
import com.breckneck.deptbook.domain.usecase.Settings.GetDefaultCurrency

class NewDebtFragmentViewModel(
    private val getDefaultCurrency: GetDefaultCurrency,
    private val getCurrentDateUseCase: GetCurrentDateUseCase,
    private val setDateUseCase: SetDateUseCase
) : ViewModel() {

    private val TAG = "NewDebtFragmentVM"

    private val _currency = MutableLiveData<String>()
    val currency: LiveData<String>
        get() = _currency
    private val _selectedCurrencyPosition = MutableLiveData<Int>()
    val selectedCurrencyPosition: LiveData<Int>
        get() = _selectedCurrencyPosition
    private val _isCurrencyDialogOpened = MutableLiveData<Boolean>()
    val isCurrencyDialogOpened: LiveData<Boolean>
        get() = _isCurrencyDialogOpened
    private val _date = MutableLiveData<String>()
    val date: LiveData<String>
        get() = _date

    init {
        Log.e(TAG, "NewDebtFragmentViewModel created")
        getDefaultCurrency()
        getCurrentDate()
    }

    override fun onCleared() {
        super.onCleared()
        Log.e(TAG, "NewDebtFragmentViewModel cleared")
    }

    fun setCurrency(currency: String) {
        _currency.value = currency
    }

    fun onCurrencyDialogOpen(selectedCurrencyPosition: Int) {
        _isCurrencyDialogOpened.value = true
        _selectedCurrencyPosition.value = selectedCurrencyPosition
    }

    fun onCurrencyDialogClose() {
        _isCurrencyDialogOpened.value = false
    }

    private fun getDefaultCurrency() {
        _currency.value = getDefaultCurrency.execute()
    }

    fun getCurrentDate() {
        _date.value = getCurrentDateUseCase.execute()
    }

    fun setCurrentDate(year: Int, month: Int, day: Int) {
        _date.value = setDateUseCase.execute(year = year, month = month, day = day)
    }
}
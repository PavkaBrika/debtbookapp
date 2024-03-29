package com.breckneck.debtbook.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.breckneck.deptbook.domain.model.Finance
import com.breckneck.deptbook.domain.model.FinanceCategory
import com.breckneck.deptbook.domain.model.FinanceCategoryWithFinances
import com.breckneck.deptbook.domain.usecase.Finance.GetAllFinances
import com.breckneck.deptbook.domain.usecase.FinanceCategory.GetAllCategoriesWithFinances
import com.breckneck.deptbook.domain.usecase.FinanceCategory.GetAllFinanceCategories
import com.breckneck.deptbook.domain.usecase.Settings.GetFinanceCurrency
import com.breckneck.deptbook.domain.usecase.Settings.SetFinanceCurrency
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class FinanceFragmentViewModel(
    private val getAllFinances: GetAllFinances,
    private val getAllFinanceCategories: GetAllFinanceCategories,
    private val getFinanceCurrency: GetFinanceCurrency,
    private val setFinanceCurrency: SetFinanceCurrency,
    private val getAllCategoriesWithFinances: GetAllCategoriesWithFinances
): ViewModel() {

    private val TAG = "FinanceFragmentVM"

    private val _financeList = MutableLiveData<List<Finance>>()
    val financeList: LiveData<List<Finance>>
        get() = _financeList
    private val _financeCategoryList = MutableLiveData<List<FinanceCategory>>()
    val financeCategoryList: LiveData<List<FinanceCategory>>
        get() = _financeCategoryList
    private val _categoryWithFinancesList = MutableLiveData<List<FinanceCategoryWithFinances>>()
    val categoryWithFinancesList: LiveData<List<FinanceCategoryWithFinances>>
        get() = _categoryWithFinancesList
    private val _isRevenueSwitch = MutableLiveData<Boolean>(true)
    val isRevenueSwitch: LiveData<Boolean>
        get() = _isRevenueSwitch
    private val _isCurrencyDialogOpened = MutableLiveData<Boolean>(false)
    val isCurrencyDialogOpened: LiveData<Boolean>
        get() = _isCurrencyDialogOpened
    private val _selectedCurrencyPosition = MutableLiveData<Int>()
    val selectedCurrencyPosition: LiveData<Int>
        get() = _selectedCurrencyPosition
    private val _currency = MutableLiveData<String>()
    val currency: LiveData<String>
        get() = _currency

    private val disposeBag = CompositeDisposable()

    init {
        getAllFinances()
        getAllCategories()
        getFinanceCurrency()
    }

    override fun onCleared() {
        super.onCleared()
        disposeBag.clear()
        Log.e(TAG, "Cleared")
    }

    fun getAllFinances() {
        val result = Single.create {
            it.onSuccess(getAllFinances.execute())
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _financeList.value = it
                Log.e(TAG, "finances loaded in VM")
            }, {
                Log.e(TAG, it.message.toString())
            })
        disposeBag.add(result)
    }

    fun getAllCategories() {
        val result = Single.create {
            getAllCategoriesWithFinances.execute()
            it.onSuccess(getAllFinanceCategories.execute())
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _financeCategoryList.value = it
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
        setFinanceCurrency.execute(currency = currency)
    }

    private fun getFinanceCurrency() {
        _currency.value = getFinanceCurrency.execute()
    }
}
package com.breckneck.debtbook.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.breckneck.deptbook.domain.model.Finance
import com.breckneck.deptbook.domain.usecase.Finance.DeleteFinance
import com.breckneck.deptbook.domain.usecase.Finance.GetFinanceByCategoryIdAndExpenses
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class FinanceDetailsViewModel(
    private val getFinanceByCategoryIdAndExpenses: GetFinanceByCategoryIdAndExpenses,
    private val deleteFinance: DeleteFinance
): ViewModel() {

    private val TAG = "FinanceDetailsViewModel"

    private val _financeList = MutableLiveData<List<Finance>>()
    val financeList: LiveData<List<Finance>>
        get() = _financeList
    private val _isSettingsDialogOpened = MutableLiveData(false)
    val isSettingsDialogOpened: LiveData<Boolean>
        get() = _isSettingsDialogOpened
    private val _settingsFinance = MutableLiveData<Finance>()
    val settingsFinance: LiveData<Finance>
        get() = _settingsFinance
    private val _categoryName = MutableLiveData<String>()
    val categoryName: LiveData<String>
        get() = _categoryName
    private val _categoryId = MutableLiveData<Int>()
    val categoryId: LiveData<Int>
        get() = _categoryId
    private val _isExpenses = MutableLiveData<Boolean>()
    val isExpenses: LiveData<Boolean>
        get() = _isExpenses

    private val disposeBag = CompositeDisposable()

    init {
        Log.e(TAG, "Created")
    }

    override fun onCleared() {
        super.onCleared()
        disposeBag.clear()
        Log.e(TAG, "Cleared")
    }

    fun getFinanceByCategoryIdAndExpenses(categoryId: Int, isExpenses: Boolean) {
        val result = Single.create {
            val financeList = getFinanceByCategoryIdAndExpenses.execute(categoryId = categoryId, isExpenses = isExpenses)
            it.onSuccess(financeList.sortedByDescending { finance -> finance.date })
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _financeList.value = it
                Log.e(TAG, "Finances load success")
            }, {
                Log.e(TAG, it.message.toString())
            })
        disposeBag.add(result)
    }

    fun deleteFinance(finance: Finance) {
        val result = Completable.create {
            deleteFinance.execute(finance = finance)
            it.onComplete()
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                getFinanceByCategoryIdAndExpenses(categoryId = categoryId.value!!, isExpenses = isExpenses.value!!)
                Log.e(TAG, "Finance delete success")
            }, {
                Log.e(TAG, it.message.toString())
            })
        disposeBag.add(result)
    }

    fun onSetSettingFinance(finance: Finance) {
        _settingsFinance.value = finance
    }

    fun onFinanceSettingsDialogOpen() {
        _isSettingsDialogOpened.value = true
    }

    fun onFinanceSettingsDialogClose() {
        _isSettingsDialogOpened.value = false
    }

    fun setCategoryId(categoryId: Int) {
        _categoryId.value = categoryId
    }

    fun setExpenses(isExpenses: Boolean) {
        _isExpenses.value = isExpenses
    }
}
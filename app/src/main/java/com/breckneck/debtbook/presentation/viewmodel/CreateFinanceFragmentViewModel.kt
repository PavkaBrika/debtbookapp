package com.breckneck.debtbook.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.breckneck.deptbook.domain.model.Finance
import com.breckneck.deptbook.domain.model.FinanceCategory
import com.breckneck.deptbook.domain.usecase.Debt.GetCurrentDateUseCase
import com.breckneck.deptbook.domain.usecase.Debt.SetDateUseCase
import com.breckneck.deptbook.domain.usecase.Finance.SetFinance
import com.breckneck.deptbook.domain.usecase.FinanceCategory.GetAllFinanceCategories
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class CreateFinanceFragmentViewModel(
    private val setFinance: SetFinance,
    private val getAllFinanceCategories: GetAllFinanceCategories,
    private val setDateUseCase: SetDateUseCase,
    private val getCurrentDateUseCase: GetCurrentDateUseCase
    ): ViewModel() {

    private val TAG = "CreateFinanceFragmentVM"

    private val disposeBag = CompositeDisposable()

    private val _financeCategoryList = MutableLiveData<List<FinanceCategory>>()
    val financeCategoryList: LiveData<List<FinanceCategory>>
        get() = _financeCategoryList
    private val _date = MutableLiveData<String>()
    val date: LiveData<String>
        get() = _date
    private val _checkedFinanceCategory = MutableLiveData<FinanceCategory>()
    val checkedFinanceCategory: LiveData<FinanceCategory>
        get() = _checkedFinanceCategory

    init {
        Log.e(TAG, "Initialized")
        getAllFinanceCategories()
        getCurrentDate()
    }

    override fun onCleared() {
        super.onCleared()
        disposeBag.clear()
        Log.e(TAG, "Cleared")
    }

    fun getAllFinanceCategories() {
        val result = Single.create {
            it.onSuccess(getAllFinanceCategories.execute())
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e(TAG, "Finance categories loaded")
                _financeCategoryList.value = it
            }, {
                Log.e(TAG, it.message.toString())
            })
        disposeBag.add(result)
    }

    fun setFinance(finance: Finance) {
        val result = Completable.create {
            setFinance.execute(finance = finance)
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

    fun getCurrentDate() {
        _date.value = getCurrentDateUseCase.execute()
    }

    fun setCurrentDate(year: Int, month: Int, day: Int) {
        _date.value = setDateUseCase.execute(year = year, month = month, day = day)
    }

    fun setCheckedFinanceCategory(financeCategory: FinanceCategory) {
        _checkedFinanceCategory.value = financeCategory
    }
}
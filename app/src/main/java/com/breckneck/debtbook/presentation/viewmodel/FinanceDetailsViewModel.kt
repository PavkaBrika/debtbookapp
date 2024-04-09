package com.breckneck.debtbook.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.breckneck.deptbook.domain.model.Finance
import com.breckneck.deptbook.domain.usecase.Finance.GetFinanceByCategoryIdAndRevenue
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class FinanceDetailsViewModel(
    private val getFinanceByCategoryIdAndRevenue: GetFinanceByCategoryIdAndRevenue
): ViewModel() {

    private val TAG = "FinanceDetailsViewModel"

    private val _financeList = MutableLiveData<List<Finance>>()
    val financeList: LiveData<List<Finance>>
        get() = _financeList

    private val disposeBag = CompositeDisposable()

    init {
        Log.e(TAG, "Created")
    }

    override fun onCleared() {
        super.onCleared()
        disposeBag.clear()
        Log.e(TAG, "Cleared")
    }

    fun getFinanceByCategoryIdAndRevenue(categoryId: Int, isRevenue: Boolean) {
        val result = Single.create {
            val financeList = getFinanceByCategoryIdAndRevenue.execute(categoryId = categoryId, isRevenue = isRevenue)
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





}
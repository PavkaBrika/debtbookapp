package com.breckneck.debtbook.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.breckneck.deptbook.domain.model.Finance
import com.breckneck.deptbook.domain.usecase.Finance.GetAllFinances
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class FinanceFragmentViewModel(
    private val getAllFinances: GetAllFinances,
): ViewModel() {

    private val TAG = "FinanceFragmentVM"

    private val _financeList = MutableLiveData<List<Finance>>()
    val financeList: LiveData<List<Finance>>
        get() = _financeList

    private val disposeBag = CompositeDisposable()

    init {
        getAllFinances()
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

}
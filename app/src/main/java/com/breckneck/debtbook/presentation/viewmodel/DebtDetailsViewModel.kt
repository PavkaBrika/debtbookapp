package com.breckneck.debtbook.presentation.viewmodel

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.breckneck.debtbook.adapter.DebtAdapter
import com.breckneck.deptbook.domain.model.DebtDomain
import com.breckneck.deptbook.domain.usecase.Debt.GetAllDebtsUseCase
import com.breckneck.deptbook.domain.usecase.Human.GetHumanSumDebtUseCase
import com.breckneck.deptbook.domain.usecase.Human.GetLastHumanIdUseCase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class DebtDetailsViewModel(
    private val getAllDebtsUseCase: GetAllDebtsUseCase,
    private val getLastHumanIdUseCase: GetLastHumanIdUseCase,
    private val getHumanSumDebtUseCase: GetHumanSumDebtUseCase
): ViewModel() {


    val debtList = MutableLiveData<List<DebtDomain>>()
    val humanId = MutableLiveData<Int>()
    val overallSum = MutableLiveData<Double>()
    private val disposeBag = CompositeDisposable()

    init {
        Log.e("TAG", "Debt Fragment View Model Started")
    }

    override fun onCleared() {
        super.onCleared()
        disposeBag.clear()
        Log.e("TAG", "Debt Fragment View Model cleared")
    }

    fun getAllDebts() {
        val getDebtsSingle = Single.create {
            Log.e("TAG", "Open Debt Details of Human id = $humanId")
            it.onSuccess(getAllDebtsUseCase.execute(id = humanId.value!!))
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                debtList.value = it
                Log.e("TAG", "Debts load success")
            }, {
                it.printStackTrace()
            })
        disposeBag.add(getDebtsSingle)
    }

    fun getLastHumanId() {
        val getLastHumanIdSingle = Single.create {
            it.onSuccess(getLastHumanIdUseCase.exectute())
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                humanId.value = it
            }, {
                it.printStackTrace()
            })
        disposeBag.add(getLastHumanIdSingle)
    }

    fun getOverallSum() {
        val getOverallSumSingle = Single.create {
            it.onSuccess(getHumanSumDebtUseCase.execute(humanId = humanId.value!!))
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                overallSum.value = it
            }, {
                it.printStackTrace()
            })
        disposeBag.add(getOverallSumSingle)
    }
}
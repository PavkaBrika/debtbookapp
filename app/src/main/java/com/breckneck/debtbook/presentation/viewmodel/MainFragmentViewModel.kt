package com.breckneck.debtbook.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.breckneck.deptbook.domain.util.HumanFilters
import com.breckneck.deptbook.domain.model.HumanDomain
import com.breckneck.deptbook.domain.usecase.Human.*
import com.breckneck.deptbook.domain.usecase.Settings.GetFirstMainCurrency
import com.breckneck.deptbook.domain.usecase.Settings.GetHumanOrder
import com.breckneck.deptbook.domain.usecase.Settings.GetSecondMainCurrency
import com.breckneck.deptbook.domain.usecase.Settings.SetHumanOrder
import com.breckneck.deptbook.domain.util.HumanOrderAttribute
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MainFragmentViewModel(
    private val getAllHumansUseCase: GetAllHumansUseCase,
    private val getAllDebtsSumUseCase: GetAllDebtsSumUseCase,
    private val getPositiveHumansUseCase: GetPositiveHumansUseCase,
    private val getNegativeHumansUseCase: GetNegativeHumansUseCase,
    private val getFirstMainCurrency: GetFirstMainCurrency,
    private val getSecondMainCurrency: GetSecondMainCurrency,
    private val getHumanOrder: GetHumanOrder,
    private val setHumanOrder: SetHumanOrder
) : ViewModel() {

    var resultPos = MutableLiveData<String>()
    var resultNeg = MutableLiveData<String>()
    var resultHumanList = MutableLiveData<List<HumanDomain>>()
    var resultIsSortDialogShown = MutableLiveData<Boolean>()
    var resultHumanFilters = MutableLiveData<HumanFilters>()
    var resultHumanOrder = MutableLiveData<Pair<HumanOrderAttribute, Boolean>>()
    private val sortHumans = SortHumans()
    private val disposeBag = CompositeDisposable()

    init {
        Log.e("TAG", "MainFragment VM created")
        resultHumanFilters.value = HumanFilters.AllHumans
    }

    override fun onCleared() {
        Log.e("TAG", "MainFragment VM cleared")
        disposeBag.clear()
        super.onCleared()
    }

    fun sortHumans() {
        if (resultHumanList.value != null)
            resultHumanList.value = sortHumans.execute(resultHumanList.value!!, resultHumanOrder.value!!)
    }

    fun getHumanOrder() {
        resultHumanOrder.value = getHumanOrder.execute()
    }

    fun setHumanOrder(order: Pair<HumanOrderAttribute, Boolean>) {
        setHumanOrder.execute(order = order)
    }

    fun getAllHumans() {
        val result = Single.create {
            it.onSuccess(getAllHumansUseCase.execute())
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                resultHumanList.value = it
                sortHumans()
                Log.e("TAG", "humans loaded in VM")
            }, {
                Log.e("TAG", it.stackTrace.toString())
            })
        disposeBag.add(result)
    }

    fun getPositiveHumans() {
        val result = Single.create {
            it.onSuccess(getPositiveHumansUseCase.execute())
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                resultHumanList.value = it
                Log.e("TAG", "humans loaded in VM")
            }, {
                Log.e("TAG", it.stackTrace.toString())
            })
        disposeBag.add(result)
    }

    fun getNegativeHumans() {
        val result = Single.create {
            it.onSuccess(getNegativeHumansUseCase.execute())
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                resultHumanList.value = it
                Log.e("TAG", "humans loaded in VM")
            }, {
                Log.e("TAG", it.stackTrace.toString())
            })
        disposeBag.add(result)
    }

    fun getPositiveSum() {
        val result = Single.create {
            it.onSuccess(
                getAllDebtsSumUseCase.execute(
                    "positive",
                    getFirstMainCurrency.execute(),
                    getSecondMainCurrency.execute()
                )
            )
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                resultPos.value = it
            }, {
                Log.e("TAG", it.stackTrace.toString())
            })
        disposeBag.add(result)
    }

    fun getNegativeSum() {
        val result = Single.create {
            it.onSuccess(
                getAllDebtsSumUseCase.execute(
                    "negative",
                    getFirstMainCurrency.execute(),
                    getSecondMainCurrency.execute()
                )
            )
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                resultNeg.value = it
            }, {
                Log.e("TAG", it.stackTrace.toString())
            })
        disposeBag.add(result)
    }
}
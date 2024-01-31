package com.breckneck.debtbook.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.breckneck.deptbook.domain.util.HumanFilter
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

    private val _positiveSum = MutableLiveData<String>()
    val positiveSum: LiveData<String>
        get() = _positiveSum
    private val _negativeSum = MutableLiveData<String>()
    val negativeSum: LiveData<String>
        get() = _negativeSum
    private val _humanList = MutableLiveData<List<HumanDomain>>()
    val humanList: LiveData<List<HumanDomain>>
        get() = _humanList
    private val _isSortDialogOpened = MutableLiveData<Boolean>()
    val isSortDialogOpened: LiveData<Boolean>
        get() = _isSortDialogOpened
    private val _humanFilter = MutableLiveData<HumanFilter>(HumanFilter.AllHumans)
    val humanFilter: LiveData<HumanFilter>
        get() = _humanFilter
    private val _humanOrder = MutableLiveData<Pair<HumanOrderAttribute, Boolean>>()
    val humanOrder: LiveData<Pair<HumanOrderAttribute, Boolean>>
        get() = _humanOrder
    private val sortHumans = SortHumans()
    private val disposeBag = CompositeDisposable()

    init {
        Log.e("TAG", "MainFragment VM created")
        getNegativeSum()
        getPositiveSum()
        getHumanOrder()
    }

    override fun onCleared() {
        Log.e("TAG", "MainFragment VM cleared")
        disposeBag.clear()
        super.onCleared()
    }

    fun sortHumans() {
        if (humanList.value != null)
            _humanList.value = sortHumans.execute(humanList.value!!, _humanOrder.value!!)
    }

    fun getHumanOrder() {
        _humanOrder.value = getHumanOrder.execute()
    }

    fun setHumanOrder(order: Pair<HumanOrderAttribute, Boolean>) {
        setHumanOrder.execute(order = order)
    }

    fun getHumans() {
        val result = Single.create {
            when (_humanFilter.value!!) {
                HumanFilter.AllHumans -> it.onSuccess(getAllHumansUseCase.execute())
                HumanFilter.NegativeHumans -> it.onSuccess(getNegativeHumansUseCase.execute())
                HumanFilter.PositiveHumans -> it.onSuccess(getPositiveHumansUseCase.execute())
            }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _humanList.value = it
                sortHumans()
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
                _positiveSum.value = it
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
                _negativeSum.value = it
            }, {
                Log.e("TAG", it.stackTrace.toString())
            })
        disposeBag.add(result)
    }

    fun onHumanSortDialogOpen() {
        _isSortDialogOpened.value = true
    }

    fun onHumanSortDialogClose() {
        _isSortDialogOpened.value = false
    }

    fun onSetHumanOrder(order: Pair<HumanOrderAttribute, Boolean>) {
        _humanOrder.value = order
    }

    fun onSetHumanFilter(filter: HumanFilter) {
        _humanFilter.value = filter
    }
}
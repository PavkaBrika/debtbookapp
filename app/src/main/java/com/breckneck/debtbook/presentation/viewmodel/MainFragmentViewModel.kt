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
import io.reactivex.rxjava3.core.Completable
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
    private val setHumanOrder: SetHumanOrder,
    private val updateHuman: UpdateHuman
) : ViewModel() {

    private val TAG = "MainFragmentViewModel"

    private val _mainSums = MutableLiveData<Pair<String, String>>()
    val mainSums: LiveData<Pair<String, String>>
        get() = _mainSums
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
    private val _isChangeDebtNameDialogOpened = MutableLiveData<Boolean>()
    val isChangeDebtNameDialogOpened: LiveData<Boolean>
        get() = _isChangeDebtNameDialogOpened
    private val _changedHuman = MutableLiveData<HumanDomain>()
    val changedHuman: LiveData<HumanDomain>
        get() = _changedHuman
    private val _changedHumanPosition = MutableLiveData<Int>()
    val changedHumanPosition: LiveData<Int>
        get() = _changedHumanPosition
    private val sortHumans by lazy { SortHumans() }
    private val disposeBag = CompositeDisposable()

    init {
        Log.e(TAG, "MainFragment VM created")
        getMainSums()
        getHumanOrder()
    }

    override fun onCleared() {
        Log.e(TAG, "MainFragment VM cleared")
        disposeBag.clear()
        super.onCleared()
    }

    fun sortHumans() {
        if (humanList.value != null)
            _humanList.value = sortHumans.execute(humanList.value!!, _humanOrder.value!!)
    }

    private fun getHumanOrder() {
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
                Log.e(TAG, "humans loaded in VM")
            }, {
                Log.e(TAG, it.stackTrace.toString())
            })
        disposeBag.add(result)
    }

    fun updateHuman(human: HumanDomain) {
        val result = Completable.create {
            updateHuman.execute(humanDomain = human)
            it.onComplete()
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e(TAG, "human updated")
            }, {
                Log.e(TAG, it.stackTrace.toString())
            })
        disposeBag.add(result)
    }

    private fun getMainSums() {
        val result = Single.create {
            it.onSuccess(
                getAllDebtsSumUseCase.execute(
                    getFirstMainCurrency.execute(),
                    getSecondMainCurrency.execute()
                )
            )
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _mainSums.value = it
            }, {
                Log.e(TAG, it.stackTrace.toString())
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

    fun onChangeDebtNameDialogOpen(humanDomain: HumanDomain, position: Int) {
        _isChangeDebtNameDialogOpened.value = true
        _changedHuman.value = humanDomain
        _changedHumanPosition.value = position
    }

    fun onChangeDebtNameDialogClose() {
        _isChangeDebtNameDialogOpened.value = false
    }
}
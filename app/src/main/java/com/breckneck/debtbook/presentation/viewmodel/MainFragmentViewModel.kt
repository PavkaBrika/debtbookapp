package com.breckneck.debtbook.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.breckneck.deptbook.domain.util.Filter
import com.breckneck.deptbook.domain.model.HumanDomain
import com.breckneck.deptbook.domain.usecase.Human.*
import com.breckneck.deptbook.domain.usecase.Settings.GetFirstMainCurrency
import com.breckneck.deptbook.domain.usecase.Settings.GetHumanOrder
import com.breckneck.deptbook.domain.usecase.Settings.GetSecondMainCurrency
import com.breckneck.deptbook.domain.usecase.Settings.SetHumanOrder
import com.breckneck.deptbook.domain.util.HumanOrderAttribute
import com.breckneck.deptbook.domain.util.ListState
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
    private val _resultHumanList = MutableLiveData<List<HumanDomain>>()
    val resultHumanList: LiveData<List<HumanDomain>>
        get() = _resultHumanList
    private val _humanList = MutableLiveData<List<HumanDomain>>()
    private val _isSortDialogOpened = MutableLiveData<Boolean>()
    val isSortDialogOpened: LiveData<Boolean>
        get() = _isSortDialogOpened
    private val _humanFilter = MutableLiveData<Filter>(Filter.All)
    val humanFilter: LiveData<Filter>
        get() = _humanFilter
    private val _humanOrder = MutableLiveData<Pair<HumanOrderAttribute, Boolean>>()
    val humanOrder: LiveData<Pair<HumanOrderAttribute, Boolean>>
        get() = _humanOrder
    private val _isHumanSorted = MutableLiveData<Boolean>(false)
    private val _isChangeDebtNameDialogOpened = MutableLiveData<Boolean>()
    val isChangeDebtNameDialogOpened: LiveData<Boolean>
        get() = _isChangeDebtNameDialogOpened
    private val _changedHuman = MutableLiveData<HumanDomain>()
    val changedHuman: LiveData<HumanDomain>
        get() = _changedHuman
    private val _changedHumanPosition = MutableLiveData<Int>()
    val changedHumanPosition: LiveData<Int>
        get() = _changedHumanPosition
    private val _humanListState = MutableLiveData<ListState>(ListState.LOADING)
    val humanListState: LiveData<ListState>
        get() = _humanListState
    private val sortHumans by lazy { SortHumans() }
    private val disposeBag = CompositeDisposable()

    init {
        Log.e(TAG, "MainFragment VM created")
        init()
    }

    override fun onCleared() {
        Log.e(TAG, "MainFragment VM cleared")
        disposeBag.clear()
        super.onCleared()
    }

    fun init() {
        getHumans()
        getMainSums()
    }

    fun sortHumans() {
        if ((_humanList.value != null) && (_isHumanSorted.value == false)) {
            val result = Single.create {
                when (humanFilter.value!!) {
                    Filter.All -> it.onSuccess(sortHumans.execute(_humanList.value!!, _humanOrder.value!!))
                    Filter.Negative -> it.onSuccess(sortHumans.execute(getNegativeHumansUseCase.execute(_humanList.value!!), _humanOrder.value!!))
                    Filter.Positive -> it.onSuccess(sortHumans.execute(getPositiveHumansUseCase.execute(_humanList.value!!), _humanOrder.value!!))
                }
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    _humanListState.value = ListState.LOADING
                }
                .subscribe({
                    _resultHumanList.value = it
                    _isHumanSorted.value = true
                    if (_resultHumanList.value!!.isEmpty())
                        _humanListState.value = ListState.EMPTY
                    else
                        _humanListState.value = ListState.FILLED
                }, {
                    Log.e(TAG, it.message.toString())
                })
            disposeBag.add(result)
            Log.e(TAG, "Humans sorted")
        }
    }

    fun onSetHumanFilter(filter: Filter) {
        _isHumanSorted.value = false
        _humanFilter.value = filter
        Log.e(TAG, "Human filter set ${filter}")
    }

    fun onSetHumanOrder(order: Pair<HumanOrderAttribute, Boolean>) {
        _isHumanSorted.value = false
        _humanOrder.value = order
        Log.e(TAG, "Human order set ${order.first}, ${order.second}")
    }

//    fun onSetListState(state: ListState) {
//        _debtListState.value = state
//    }

    private fun getHumanOrder() {
        _humanOrder.value = getHumanOrder.execute()
    }

    fun saveHumanOrder(order: Pair<HumanOrderAttribute, Boolean>) {
        setHumanOrder.execute(order = order)
    }

    private fun getHumans() {
        val result = Single.create {
            it.onSuccess(getAllHumansUseCase.execute())
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                _humanListState.value = ListState.LOADING
            }
            .subscribe({
                _humanList.value = it
                Log.e(TAG, "humans loaded in VM")
                getHumanOrder()
            }, {
                Log.e(TAG, it.message.toString())
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
                Log.e(TAG, it.message.toString())
            })
        disposeBag.add(result)
    }

    fun getMainSums() {
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
                Log.e(TAG, it.message.toString())
            })
        disposeBag.add(result)
    }

    fun onHumanSortDialogOpen() {
        _isSortDialogOpened.value = true
    }

    fun onHumanSortDialogClose() {
        _isSortDialogOpened.value = false
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
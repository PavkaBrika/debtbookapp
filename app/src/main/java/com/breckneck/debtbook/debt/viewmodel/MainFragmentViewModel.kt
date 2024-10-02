package com.breckneck.debtbook.debt.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.breckneck.deptbook.domain.util.Filter
import com.breckneck.deptbook.domain.model.HumanDomain
import com.breckneck.deptbook.domain.usecase.Human.*
import com.breckneck.deptbook.domain.usecase.Settings.GetFirstMainCurrency
import com.breckneck.deptbook.domain.usecase.Settings.GetHumanOrder
import com.breckneck.deptbook.domain.usecase.Settings.GetSecondMainCurrency
import com.breckneck.deptbook.domain.usecase.Settings.SetHumanOrder
import com.breckneck.deptbook.domain.util.DebtLogicListState
import com.breckneck.deptbook.domain.util.HumanOrderAttribute
import com.breckneck.deptbook.domain.util.ScreenState
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class MainFragmentViewModel(
    private val getAllHumansUseCase: GetAllHumansUseCase,
    private val getAllDebtsSumUseCase: GetAllDebtsSumUseCase,
    private val getFirstMainCurrency: GetFirstMainCurrency,
    private val getSecondMainCurrency: GetSecondMainCurrency,
    private val getHumanOrder: GetHumanOrder,
    private val setHumanOrder: SetHumanOrder,
    private val updateHuman: UpdateHuman
) : ViewModel() {

    private val TAG = "MainFragmentViewModel"

    private val _screenState = MutableLiveData(ScreenState.LOADING)
    val screenState: LiveData<ScreenState>
        get() = _screenState
    private val _listState: MutableStateFlow<DebtLogicListState> =
        MutableStateFlow(DebtLogicListState.Loading)

    private val _mainSums = MutableLiveData<Pair<String, String>>()
    val mainSums: LiveData<Pair<String, String>>
        get() = _mainSums

    private val _initialHumanList = MutableLiveData<List<HumanDomain>>()
    private val _sortedHumanList = MutableLiveData<List<HumanDomain>>()
    private val _resultedHumanList = MutableLiveData<List<HumanDomain>>()
    val resultedHumanList: LiveData<List<HumanDomain>>
        get() = _resultedHumanList

    private val _humanFilter = MutableLiveData<Filter>(Filter.ALL)
    val humanFilter: LiveData<Filter>
        get() = _humanFilter
    private val _humanOrder = MutableLiveData<Pair<HumanOrderAttribute, Boolean>>()
    val humanOrder: LiveData<Pair<HumanOrderAttribute, Boolean>>
        get() = _humanOrder

    private val _isSortDialogOpened = MutableLiveData<Boolean>()
    val isSortDialogOpened: LiveData<Boolean>
        get() = _isSortDialogOpened
    private val _isChangeDebtNameDialogOpened = MutableLiveData<Boolean>()
    val isChangeDebtNameDialogOpened: LiveData<Boolean>
        get() = _isChangeDebtNameDialogOpened

    private val _changedHuman = MutableLiveData<HumanDomain>()
    val changedHuman: LiveData<HumanDomain>
        get() = _changedHuman
    private val _changedHumanPosition = MutableLiveData<Int>()
    val changedHumanPosition: LiveData<Int>
        get() = _changedHumanPosition

    private val _isSearching = MutableLiveData(false)
    val isSearching: LiveData<Boolean>
        get() = _isSearching
    private val _resultHumanListCopyForSearching = MutableLiveData<List<HumanDomain>>()
    private val _searchQuery = MutableStateFlow("")

    private val disposeBag = CompositeDisposable()

    init {
        Log.e(TAG, "MainFragment VM created")

        viewModelScope.launch {
            _listState.collectLatest { state ->
                when (state) {
                    is DebtLogicListState.Loading -> {
                        _screenState.value = ScreenState.LOADING
                        getHumanOrder()
                        getMainSums()
                        getHumans()
                    }

                    is DebtLogicListState.Received -> {
                        _screenState.value = ScreenState.LOADING
                        sortHumans(state = state)
                    }

                    is DebtLogicListState.Sorted -> {
                        _resultedHumanList.value = _sortedHumanList.value
                        _screenState.value = ScreenState.SUCCESS
                    }
                }
            }
        }
    }

    fun setListStateLoading() {
        _listState.value = DebtLogicListState.Loading
    }

    private fun getHumans() {
        val result = Single.create {
            it.onSuccess(getAllHumansUseCase.execute())
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.isNotEmpty()) {
                    _initialHumanList.value = it
                    _sortedHumanList.value = it
                    _listState.value =
                        DebtLogicListState.Received(needToSetFilter = true)
                    Log.e(TAG, "humans loaded in VM")
                } else
                    _screenState.value = ScreenState.EMPTY
            }, {
                Log.e(TAG, it.stackTraceToString())
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
                Log.e(TAG, it.stackTraceToString())
            })
        disposeBag.add(result)
    }

    fun getHumanOrder() {
        _humanOrder.value = getHumanOrder.execute()
    }

    private fun filterHumans(list: List<HumanDomain>): List<HumanDomain> {
        return when (humanFilter.value!!) {
            Filter.ALL -> list
            Filter.NEGATIVE -> list.filter { human -> human.sumDebt <= 0 }
            Filter.POSITIVE -> list.filter { human -> human.sumDebt >= 0 }
        }
    }

    private fun orderHumans(list: List<HumanDomain>): List<HumanDomain> {
        return com.breckneck.deptbook.domain.util.sortHumans(
            debtList = list,
            order = humanOrder.value!!
        )
    }

    private fun sortHumans(state: DebtLogicListState) {
        val result = Single.create {
            var list = _sortedHumanList.value
            if (state.needToSetFilter == true) {
                list = filterHumans(list!!)
                Log.e(TAG, "FILTERED")
            }
            list = orderHumans(list!!)
            Log.e(TAG, "SORTED")
            it.onSuccess(list)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e(TAG, "RECEIVED")
                if (it.isNotEmpty()) {
                    _sortedHumanList.value = it
                    _listState.value = DebtLogicListState.Sorted
                } else
                    _screenState.value = ScreenState.EMPTY
            }, {
                Log.e(TAG, it.stackTraceToString())
            })
        disposeBag.add(result)
    }

    fun onSetHumanSort(filter: Filter, order: Pair<HumanOrderAttribute, Boolean>) {
        if (humanFilter.value!! != filter && humanOrder.value!! == order) {
            _humanFilter.value = filter
            _sortedHumanList.value = _initialHumanList.value
            _listState.value = DebtLogicListState.Received(needToSetFilter = true)
        } else if (humanFilter.value!! == filter && humanOrder.value!! != order) {
            _humanOrder.value = order
            _listState.value = DebtLogicListState.Received(needToSetFilter = false)
        } else if (humanFilter.value!! != filter && humanOrder.value!! != order) {
            _humanFilter.value = filter
            _humanOrder.value = order
            _sortedHumanList.value = _initialHumanList.value
            _listState.value = DebtLogicListState.Received(needToSetFilter = true)
        }
    }

    fun saveHumanOrder(order: Pair<HumanOrderAttribute, Boolean>) {
        setHumanOrder.execute(order = order)
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
                Log.e(TAG, it.stackTraceToString())
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

    fun setSearchQuery(query: String) {
        viewModelScope.launch {
            _searchQuery.emit(query)
        }
    }

    fun onStartSearch() {
        _searchQuery
            .debounce(500)
            .onStart {
                _resultHumanListCopyForSearching.value = _resultedHumanList.value
                Log.e(TAG, "SEARCH FLOW STARTED")
            }
            .onEach { query ->
                _resultedHumanList.value =
                    _resultHumanListCopyForSearching.value!!.filter { human ->
                        human.name.lowercase().trim().contains(query.lowercase().trim())
                    }
                Log.e(TAG, "SEARCH")
            }.launchIn(viewModelScope)
    }

    fun onStopSearch() {
        if (_resultHumanListCopyForSearching.value != null)
            _resultedHumanList.value = _resultHumanListCopyForSearching.value
        Log.e(TAG, "RESULT")
    }

    fun setIsSearching(isSearching: Boolean) {
        _isSearching.value = isSearching
    }

    override fun onCleared() {
        Log.e(TAG, "MainFragment VM cleared")
        disposeBag.clear()
        super.onCleared()
    }
}
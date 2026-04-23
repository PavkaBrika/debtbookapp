package com.breckneck.debtbook.debt.details

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.breckneck.deptbook.domain.model.DebtDomain
import com.breckneck.deptbook.domain.usecase.Debt.DeleteDebtUseCase
import com.breckneck.deptbook.domain.usecase.Debt.DeleteDebtsByHumanIdUseCase
import com.breckneck.deptbook.domain.usecase.Debt.GetAllDebtsByIdUseCase
import com.breckneck.deptbook.domain.usecase.Human.AddSumUseCase
import com.breckneck.deptbook.domain.usecase.Human.DeleteHumanUseCase
import com.breckneck.deptbook.domain.usecase.Human.GetHumanSumDebtUseCase
import com.breckneck.deptbook.domain.usecase.Human.GetLastHumanIdUseCase
import com.breckneck.deptbook.domain.usecase.Settings.GetDebtOrder
import com.breckneck.deptbook.domain.usecase.Settings.SetDebtOrder
import com.breckneck.deptbook.domain.util.DebtLogicListState
import com.breckneck.deptbook.domain.util.DebtOrderAttribute
import com.breckneck.deptbook.domain.util.Filter
import com.breckneck.deptbook.domain.util.ScreenState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DebtDetailsViewModel @Inject constructor(
    private val getAllDebtsByIdUseCase: GetAllDebtsByIdUseCase,
    private val getLastHumanIdUseCase: GetLastHumanIdUseCase,
    private val getHumanSumDebtUseCase: GetHumanSumDebtUseCase,
    private val deleteHumanUseCase: DeleteHumanUseCase,
    private val deleteDebtsByHumanIdUseCase: DeleteDebtsByHumanIdUseCase,
    private val deleteDebtUseCase: DeleteDebtUseCase,
    private val addSumUseCase: AddSumUseCase,
    private val getDebtOrder: GetDebtOrder,
    private val setDebtOrder: SetDebtOrder,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val TAG = "DebtDetailsViewModel"

    private val _screenState = MutableLiveData(ScreenState.LOADING)
    val screenState: LiveData<ScreenState>
        get() = _screenState
    private val _listState: MutableStateFlow<DebtLogicListState> =
        MutableStateFlow(DebtLogicListState.Loading)

    private val _initialDebtList = MutableLiveData<List<DebtDomain>>()
    private val _sortedDebtList = MutableLiveData<List<DebtDomain>>()
    private val _resultedDebtList = MutableLiveData<List<DebtDomain>>()
    val resultedDebtList: LiveData<List<DebtDomain>>
        get() = _resultedDebtList

    private var _humanId = savedStateHandle.get<Int>("idHuman")
    val humanId: Int?
        get() = _humanId
    private val _humanName = savedStateHandle.getLiveData<String>("name")
    val humanName: LiveData<String>
        get() = _humanName
    private val _humanCurrency = savedStateHandle.get<String>("currency")
    val humanCurrency: String?
        get() = _humanCurrency
    private val _newHuman = savedStateHandle.get<Boolean>("newHuman")

    private val _overallSum = MutableLiveData<Double>()
    val overallSum: LiveData<Double>
        get() = _overallSum

    private val _isSortDialogShown by lazy { MutableLiveData<Boolean>() }
    val isSortDialogShown: LiveData<Boolean>
        get() = _isSortDialogShown
    private val _isHumanDeleteDialogShown by lazy { MutableLiveData<Boolean>() }
    val isHumanDeleteDialogShown: LiveData<Boolean>
        get() = _isHumanDeleteDialogShown
    private val _isShareDialogShown by lazy { MutableLiveData<Boolean>() }
    val isShareDialogShown: LiveData<Boolean>
        get() = _isShareDialogShown
    private val _isDebtSettingsDialogShown by lazy { MutableLiveData<Boolean>() }
    val isDebtSettingsDialogShown: LiveData<Boolean>
        get() = _isDebtSettingsDialogShown
    private val _settingDebt by lazy { MutableLiveData<DebtDomain>() }
    val settingDebt: LiveData<DebtDomain>
        get() = _settingDebt

    private val _debtOrder = MutableLiveData<Pair<DebtOrderAttribute, Boolean>>()
    val debtOrder: LiveData<Pair<DebtOrderAttribute, Boolean>>
        get() = _debtOrder
    private val _debtFilter = MutableLiveData<Filter>(Filter.ALL)
    val debtFilter: LiveData<Filter>
        get() = _debtFilter

    init {
        Log.e(TAG, "Debt Fragment View Model Started")

        viewModelScope.launch {
            _listState.collectLatest { state ->
                when (state) {
                    DebtLogicListState.Loading -> {
                        _screenState.value = ScreenState.LOADING
                        if (_newHuman == true) {
                            try {
                                _humanId = withContext(Dispatchers.IO) {
                                    getLastHumanIdUseCase.exectute()
                                }
                                getDebtInfo()
                                Log.e(TAG, "New human id is ${_humanId}")
                            } catch (e: Exception) {
                                Log.e(TAG, e.stackTraceToString())
                            }
                        } else {
                            getDebtInfo()
                        }
                    }

                    is DebtLogicListState.Received -> {
                        _screenState.value = ScreenState.LOADING
                        sortDebts(state = state)
                    }

                    DebtLogicListState.Sorted -> {
                        _resultedDebtList.value = _sortedDebtList.value
                        _screenState.value = ScreenState.SUCCESS
                    }
                }
            }
        }
    }

    private fun getDebtInfo() {
        getDebtOrder()
        getAllDebts()
        getOverallSum()
    }

    override fun onCleared() {
        super.onCleared()
        Log.e(TAG, "Debt Fragment View Model cleared")
    }

    private fun getDebtOrder() {
        _debtOrder.value = getDebtOrder.execute()
    }

    fun saveDebtOrder(order: Pair<DebtOrderAttribute, Boolean>) {
        setDebtOrder.execute(order = order)
    }

    private fun getAllDebts() {
        viewModelScope.launch {
            try {
                val debts = withContext(Dispatchers.IO) {
                    getAllDebtsByIdUseCase.execute(id = humanId!!)
                        .also { Log.e(TAG, "Open Debt Details of Human id = ${humanId}") }
                }
                if (debts.isNotEmpty()) {
                    _initialDebtList.value = debts
                    _sortedDebtList.value = debts
                    _listState.value = DebtLogicListState.Received(needToSetFilter = true)
                    Log.e(TAG, "Debts load success")
                } else {
                    _screenState.value = ScreenState.EMPTY
                }
            } catch (e: Exception) {
                Log.e(TAG, e.stackTraceToString())
            }
        }
    }

    private fun filterDebts(list: List<DebtDomain>): List<DebtDomain> {
        return when (debtFilter.value!!) {
            Filter.ALL -> list
            Filter.NEGATIVE -> list.filter { debt -> debt.sum <= 0 }
            Filter.POSITIVE -> list.filter { debt -> debt.sum >= 0 }
        }
    }

    private suspend fun sortDebts(state: DebtLogicListState) {
        try {
            val sorted = withContext(Dispatchers.Default) {
                var list = _sortedDebtList.value
                if (state.needToSetFilter == true) {
                    list = filterDebts(list!!)
                    Log.e(TAG, "FILTERED")
                }
                list = orderDebts(list!!)
                Log.e(TAG, "SORTED")
                list
            }
            Log.e(TAG, "RECEIVED")
            if (sorted.isNotEmpty()) {
                _sortedDebtList.value = sorted
                _listState.value = DebtLogicListState.Sorted
            } else {
                _screenState.value = ScreenState.EMPTY
            }
        } catch (e: Exception) {
            Log.e(TAG, e.stackTraceToString())
        }
    }

    private fun orderDebts(list: List<DebtDomain>): List<DebtDomain> {
        return com.breckneck.deptbook.domain.util.sortDebts(
            debtList = list,
            order = debtOrder.value!!
        )
    }

    private fun getOverallSum() {
        viewModelScope.launch {
            try {
                val sum = withContext(Dispatchers.IO) {
                    getHumanSumDebtUseCase.execute(humanId = humanId!!)
                }
                _overallSum.value = sum
            } catch (e: Exception) {
                Log.e(TAG, e.stackTraceToString())
            }
        }
    }

    fun deleteHuman() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    deleteHumanUseCase.execute(id = humanId!!)
                    deleteDebtsByHumanIdUseCase.execute(id = humanId!!)
                }
                Log.e(TAG, "Deleted human with id = ${humanId}")
            } catch (e: Exception) {
                Log.e(TAG, e.stackTraceToString())
            }
        }
    }

    fun deleteDebt(debtDomain: DebtDomain) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    deleteDebtUseCase.execute(debtDomain)
                    addSumUseCase.execute(humanId = humanId!!, sum = (debtDomain.sum * (-1.0)))
                    Log.e(TAG, "Debt delete success")
                }
                _listState.value = DebtLogicListState.Loading
            } catch (e: Exception) {
                Log.e(TAG, e.stackTraceToString())
            }
        }
    }

    fun onHumanDeleteDialogOpen() {
        _isHumanDeleteDialogShown.value = true
    }

    fun onHumanDeleteDialogClose() {
        _isHumanDeleteDialogShown.value = false
    }

    fun onDebtSettingsDialogOpen() {
        _isDebtSettingsDialogShown.value = true
    }

    fun onDebtSettingsDialogClose() {
        _isDebtSettingsDialogShown.value = false
    }

    fun onSetSettingDebt(debt: DebtDomain) {
        _settingDebt.value = debt
    }

    fun onShareDialogOpen() {
        _isShareDialogShown.value = true
    }

    fun onShareDialogClose() {
        _isShareDialogShown.value = false
    }

    fun onSortDialogOpen() {
        _isSortDialogShown.value = true
    }

    fun onOrderDialogClose() {
        _isSortDialogShown.value = false
    }

    fun onSetDebtSort(filter: Filter, order: Pair<DebtOrderAttribute, Boolean>) {
        if (debtFilter.value!! != filter && debtOrder.value!! == order) {
            _debtFilter.value = filter
            _sortedDebtList.value = _initialDebtList.value
            _listState.value = DebtLogicListState.Received(needToSetFilter = true)
        } else if (debtFilter.value!! == filter && debtOrder.value!! != order) {
            _debtOrder.value = order
            _listState.value = DebtLogicListState.Received(needToSetFilter = false)
        } else if (debtFilter.value!! != filter && debtOrder.value!! != order) {
            _debtFilter.value = filter
            _debtOrder.value = order
            _sortedDebtList.value = _initialDebtList.value
            _listState.value = DebtLogicListState.Received(needToSetFilter = true)
        }
    }
}

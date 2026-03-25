package com.breckneck.debtbook.finance.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.breckneck.deptbook.domain.model.Finance
import com.breckneck.deptbook.domain.usecase.Finance.DeleteFinance
import com.breckneck.deptbook.domain.usecase.Finance.GetFinanceByCategoryId
import com.breckneck.deptbook.domain.util.ListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FinanceDetailsViewModel @Inject constructor(
    private val getFinanceByCategoryId: GetFinanceByCategoryId,
    private val deleteFinance: DeleteFinance
): ViewModel() {

    private val TAG = "FinanceDetailsViewModel"

    private val _financeList = MutableLiveData<List<Finance>>()
    val financeList: LiveData<List<Finance>>
        get() = _financeList
    private val _isSettingsDialogOpened = MutableLiveData(false)
    val isSettingsDialogOpened: LiveData<Boolean>
        get() = _isSettingsDialogOpened
    private val _settingsFinance = MutableLiveData<Finance>()
    val settingsFinance: LiveData<Finance>
        get() = _settingsFinance
    private val _categoryName = MutableLiveData<String>()
    val categoryName: LiveData<String>
        get() = _categoryName
    private val _categoryId = MutableLiveData<Int>()
    val categoryId: LiveData<Int>
        get() = _categoryId
    private val _isExpenses = MutableLiveData<Boolean>()
    val isExpenses: LiveData<Boolean>
        get() = _isExpenses
    private val _financeListState = MutableLiveData<ListState>(ListState.LOADING)
    val financeListState: LiveData<ListState>
        get() = _financeListState

    init {
        Log.e(TAG, "Created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.e(TAG, "Cleared")
    }

    fun getFinanceByCategoryId(categoryId: Int) {
        _financeListState.value = ListState.LOADING
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    getFinanceByCategoryId.execute(categoryId = categoryId)
                        .sortedByDescending { finance -> finance.date }
                }
                _financeList.value = result
                if (_financeList.value!!.isEmpty())
                    _financeListState.value = ListState.EMPTY
                Log.e(TAG, "Finances load success")
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }
    }

    fun deleteFinance(finance: Finance) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    deleteFinance.execute(finance = finance)
                }
                getFinanceByCategoryId(categoryId = categoryId.value!!)
                Log.e(TAG, "Finance delete success")
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }
    }

    fun setFinanceListState(state: ListState) {
        _financeListState.value = state
    }

    fun onSetSettingFinance(finance: Finance) {
        _settingsFinance.value = finance
    }

    fun onFinanceSettingsDialogOpen() {
        _isSettingsDialogOpened.value = true
    }

    fun onFinanceSettingsDialogClose() {
        _isSettingsDialogOpened.value = false
    }

    fun setCategoryId(categoryId: Int) {
        _categoryId.value = categoryId
    }

    fun setExpenses(isExpenses: Boolean) {
        _isExpenses.value = isExpenses
    }
}

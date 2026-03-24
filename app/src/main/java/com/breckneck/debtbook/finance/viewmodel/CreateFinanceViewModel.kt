package com.breckneck.debtbook.finance.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.breckneck.deptbook.domain.model.Finance
import com.breckneck.deptbook.domain.model.FinanceCategory
import com.breckneck.deptbook.domain.usecase.Finance.DeleteAllFinancesByCategoryId
import com.breckneck.deptbook.domain.usecase.Finance.SetFinance
import com.breckneck.deptbook.domain.usecase.Finance.UpdateFinance
import com.breckneck.deptbook.domain.usecase.FinanceCategory.DeleteFinanceCategory
import com.breckneck.deptbook.domain.usecase.FinanceCategory.GetAllFinanceCategories
import com.breckneck.deptbook.domain.usecase.FinanceCategory.GetFinanceCategoriesByState
import com.breckneck.deptbook.domain.usecase.Settings.GetFinanceCurrency
import com.breckneck.deptbook.domain.util.CreateFragmentState
import com.breckneck.deptbook.domain.util.FinanceCategoryState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date

class CreateFinanceViewModel(
    private val setFinance: SetFinance,
    private val getAllFinanceCategories: GetAllFinanceCategories,
    private val getFinanceCurrency: GetFinanceCurrency,
    private val updateFinance: UpdateFinance,
    private val deleteFinanceCategoryUseCase: DeleteFinanceCategory,
    private val getFinanceCategoriesByState: GetFinanceCategoriesByState,
    private val deleteAllFinancesByCategoryId: DeleteAllFinancesByCategoryId
) : ViewModel() {

    private val TAG = "CreateFinanceFragmentVM"

    val sdf = SimpleDateFormat("d MMM yyyy")

    private val _financeCategoryList = MutableLiveData<List<FinanceCategory>>()
    val financeCategoryList: LiveData<List<FinanceCategory>>
        get() = _financeCategoryList
    private val _date = MutableLiveData<Date>()
    val date: LiveData<Date>
        get() = _date
    private val _dateString = MutableLiveData<String>()
    val dateString: LiveData<String>
        get() = _dateString
    private val _checkedFinanceCategory = MutableLiveData<FinanceCategory>()
    val checkedFinanceCategory: LiveData<FinanceCategory>
        get() = _checkedFinanceCategory
    private val _currency = MutableLiveData<String>()
    val currency: LiveData<String>
        get() = _currency
    private val _financeCategoryState = MutableLiveData<FinanceCategoryState>()
    val financeCategoryState: LiveData<FinanceCategoryState>
        get() = _financeCategoryState
    private val _dayInMillis = MutableLiveData<Long>()
    val dayInMillis: LiveData<Long>
        get() = _dayInMillis
    private val _financeEdit = MutableLiveData<Finance>()
    val financeEdit: LiveData<Finance>
        get() = _financeEdit
    private val _createFragmentState = MutableLiveData<CreateFragmentState>()
    val createFragmentState: LiveData<CreateFragmentState>
        get() = _createFragmentState
    private val _isDeleteCategoryDialogOpened = MutableLiveData<Boolean>(false)
    val isDeleteCategoryDialogOpened: LiveData<Boolean>
        get() = _isDeleteCategoryDialogOpened
    private val _deleteFinanceCategory = MutableLiveData<FinanceCategory>()
    val deleteFinanceCategory: LiveData<FinanceCategory>
        get() = _deleteFinanceCategory

    init {
        Log.e(TAG, "Initialized")
        getFinanceCurrency()
    }

    override fun onCleared() {
        super.onCleared()
        Log.e(TAG, "Cleared")
    }

    fun getAllFinanceCategories() {
        viewModelScope.launch {
            try {
                val categories = withContext(Dispatchers.IO) { getAllFinanceCategories.execute() }
                _financeCategoryList.value = categories
                Log.e(TAG, "Finance categories loaded")
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }
    }

    fun getFinanceCategoriesByState() {
        viewModelScope.launch {
            try {
                val categories = withContext(Dispatchers.IO) {
                    getFinanceCategoriesByState.execute(financeCategoryState = financeCategoryState.value!!)
                }
                _financeCategoryList.value = categories
                Log.e(TAG, "Finance categories loaded")
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }
    }

    fun setFinance(finance: Finance) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) { setFinance.execute(finance = finance) }
                Log.e(TAG, "finance added")
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }
    }

    fun editFinance(finance: Finance) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) { updateFinance.execute(finance = finance) }
                Log.e(TAG, "finance updated")
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }
    }

    fun deleteFinanceCategory() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    deleteFinanceCategoryUseCase.execute(financeCategory = deleteFinanceCategory.value!!)
                    deleteAllFinancesByCategoryId.execute(financeCategoryId = deleteFinanceCategory.value!!.id)
                }
                Log.e(TAG, "Category deleted")
                getFinanceCategoriesByState()
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }
    }

    private fun getFinanceCurrency() {
        _currency.value = getFinanceCurrency.execute()
    }

    fun setFinanceEdit(finance: Finance) {
        _financeEdit.value = finance
    }

    fun setFinanceCategoryState(financeCategoryState: FinanceCategoryState) {
        _financeCategoryState.value = financeCategoryState
    }

    fun setDayInMillis(dayInMillis: Long) {
        _dayInMillis.value = dayInMillis
    }

    fun setCurrentDate(date: Date) {
        _date.value = date
        _dateString.value = sdf.format(date)
    }

    fun setCheckedFinanceCategory(financeCategory: FinanceCategory) {
        _checkedFinanceCategory.value = financeCategory
    }

    fun setCreateFinanceState(createFragmentState: CreateFragmentState) {
        _createFragmentState.value = createFragmentState
    }

    fun setDeleteCategoryDialogOpened(isOpened: Boolean) {
        _isDeleteCategoryDialogOpened.value = isOpened
    }

    fun setDeleteCategory(financeCategory: FinanceCategory) {
        _deleteFinanceCategory.value = financeCategory
    }

    fun onChangeFinanceCategoryState() {
        _financeCategoryState.value = when (_financeCategoryState.value!!) {
            FinanceCategoryState.EXPENSE -> FinanceCategoryState.INCOME
            FinanceCategoryState.INCOME -> FinanceCategoryState.EXPENSE
        }
    }
}

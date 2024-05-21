package com.breckneck.debtbook.presentation.viewmodel

import android.text.format.DateFormat
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.breckneck.deptbook.domain.model.Finance
import com.breckneck.deptbook.domain.model.FinanceCategoryWithFinances
import com.breckneck.deptbook.domain.usecase.Finance.GetAllFinances
import com.breckneck.deptbook.domain.usecase.FinanceCategory.GetAllCategoriesWithFinances
import com.breckneck.deptbook.domain.usecase.FinanceCategory.GetAllFinanceCategories
import com.breckneck.deptbook.domain.usecase.Settings.GetFinanceCurrency
import com.breckneck.deptbook.domain.usecase.Settings.SetFinanceCurrency
import com.breckneck.deptbook.domain.util.FinanceCategoryState
import com.breckneck.deptbook.domain.util.FinanceInterval
import com.breckneck.deptbook.domain.util.ListState
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.Calendar
import kotlin.math.roundToInt

class FinanceViewModel(
    private val getFinanceCurrency: GetFinanceCurrency,
    private val setFinanceCurrency: SetFinanceCurrency,
    private val getAllCategoriesWithFinances: GetAllCategoriesWithFinances
): ViewModel() {

    private val TAG = "FinanceFragmentVM"

    private val _categoriesWithFinancesList = MutableLiveData<List<FinanceCategoryWithFinances>>()
    val categoriesWithFinancesList: LiveData<List<FinanceCategoryWithFinances>>
        get() = _categoriesWithFinancesList
    private val _financeCategoryState = MutableLiveData<FinanceCategoryState>(FinanceCategoryState.EXPENSE)
    val financeCategoryState: LiveData<FinanceCategoryState>
        get() = _financeCategoryState
    private val _isCurrencyDialogOpened = MutableLiveData<Boolean>(false)
    val isCurrencyDialogOpened: LiveData<Boolean>
        get() = _isCurrencyDialogOpened
    private val _selectedIntervalPosition = MutableLiveData<Int>()
    val selectedIntervalPosition: LiveData<Int>
        get() = _selectedIntervalPosition
    private val _isFinanceIntervalDialogOpened = MutableLiveData(false)
    val isFinanceIntervalDialogOpened: LiveData<Boolean>
        get() = _isFinanceIntervalDialogOpened
    private val _selectedCurrencyPosition = MutableLiveData<Int>()
    val selectedCurrencyPosition: LiveData<Int>
        get() = _selectedCurrencyPosition
    private val _currency = MutableLiveData<String>()
    val currency: LiveData<String>
        get() = _currency
    private val _financeInterval = MutableLiveData(FinanceInterval.DAY)
    val financeInterval: LiveData<FinanceInterval>
        get() = _financeInterval
    private val _financeIntervalString = MutableLiveData<String>()
    val financeIntervalString: LiveData<String>
        get() = _financeIntervalString
    private val _financeIntervalUnix = MutableLiveData<Pair<Long, Long>>() // 1 - left border in millis, 2 - right border in millis
    val financeIntervalUnix: LiveData<Pair<Long, Long>>
        get() = _financeIntervalUnix
    private val _currentDayInSeconds = MutableLiveData<Long>()
    val currentDayInSeconds: LiveData<Long>
        get() = _currentDayInSeconds
    private val _overallSum = MutableLiveData<Double>(0.0)
    val overallSum: LiveData<Double>
        get() = _overallSum
    private val _financeListState = MutableLiveData<ListState>(ListState.LOADING)
    val financeListState: LiveData<ListState>
        get() = _financeListState

    private val disposeBag = CompositeDisposable()

    init {
        getFinanceCurrency()
        getFinanceInterval()
        getCurrentDayInSeconds()
    }

    override fun onCleared() {
        super.onCleared()
        disposeBag.clear()
        Log.e(TAG, "Cleared")
    }

    private fun getCurrentDayInSeconds() {
        _currentDayInSeconds.value = financeIntervalUnix.value!!.first!! / 1000
    }

    private fun getFinanceInterval() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.HOUR, 0)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        when (financeInterval.value) {
            FinanceInterval.DAY -> {
                val leftIntervalBorder = calendar.timeInMillis
                calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 1)
                val rightIntervalBorder = calendar.timeInMillis
                _financeIntervalUnix.value = Pair(leftIntervalBorder, rightIntervalBorder)
                _financeIntervalString.value = DateFormat.format("dd MMM", leftIntervalBorder).toString()
            }
            FinanceInterval.WEEK -> {
                calendar.set(Calendar.DAY_OF_YEAR, 0)
                calendar.set(Calendar.DAY_OF_MONTH, 0)
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                val leftIntervalBorder = calendar.timeInMillis
                calendar.set(Calendar.SECOND, 59)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.HOUR, 23)
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                val rightIntervalBorder = calendar.timeInMillis
                _financeIntervalUnix.value = Pair(leftIntervalBorder, rightIntervalBorder)
                _financeIntervalString.value = "${DateFormat.format("dd MMM", leftIntervalBorder)} - ${DateFormat.format("dd MMM", rightIntervalBorder)}"
            }
            FinanceInterval.MONTH ->  {
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH))
                val leftIntervalBorder = calendar.timeInMillis
                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1)
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH))
                val rightIntervalBorder = calendar.timeInMillis
                _financeIntervalUnix.value = Pair(leftIntervalBorder, rightIntervalBorder)
                _financeIntervalString.value = DateFormat.format("MMM yyyy", leftIntervalBorder).toString()
            }
            FinanceInterval.YEAR -> {
                calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMinimum(Calendar.DAY_OF_YEAR))
                val leftIntervalBorder = calendar.timeInMillis
                calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1)
                calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMinimum(Calendar.DAY_OF_YEAR))
                val rightIntervalBorder = calendar.timeInMillis
                _financeIntervalUnix.value = Pair(leftIntervalBorder, rightIntervalBorder)
                _financeIntervalString.value = DateFormat.format("yyyy", leftIntervalBorder).toString()
            }
            null -> {}
        }
        getAllCategoriesWithFinances()
    }

    fun getNextFinanceInterval() {
        val calendar = Calendar.getInstance()
        when (financeInterval.value) {
            FinanceInterval.DAY -> {
                calendar.timeInMillis = financeIntervalUnix.value!!.second
                val leftIntervalBorder = financeIntervalUnix.value!!.second
                calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 1)
                val rightIntervalBorder = calendar.timeInMillis
                _financeIntervalUnix.value = Pair(leftIntervalBorder, rightIntervalBorder)
                _financeIntervalString.value = DateFormat.format("dd MMM", leftIntervalBorder).toString()
            }
            FinanceInterval.WEEK -> {
                calendar.timeInMillis = financeIntervalUnix.value!!.second
                val leftIntervalBorder = financeIntervalUnix.value!!.second
                calendar.set(Calendar.WEEK_OF_YEAR, calendar.get(Calendar.WEEK_OF_YEAR) + 1)
                val rightIntervalBorder = calendar.timeInMillis
                _financeIntervalUnix.value = Pair(leftIntervalBorder, rightIntervalBorder)
                _financeIntervalString.value = "${DateFormat.format("dd MMM", leftIntervalBorder)} - ${DateFormat.format("dd MMM", rightIntervalBorder)}"
            }
            FinanceInterval.MONTH -> {
                calendar.timeInMillis = financeIntervalUnix.value!!.first
                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1)
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH))
                val leftIntervalBorder = calendar.timeInMillis
                calendar.timeInMillis = financeIntervalUnix.value!!.second
                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1)
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH))
                val rightIntervalBorder = calendar.timeInMillis
                _financeIntervalUnix.value = Pair(leftIntervalBorder, rightIntervalBorder)
                _financeIntervalString.value = DateFormat.format("MMM yyyy", leftIntervalBorder).toString()
            }
            FinanceInterval.YEAR -> {
                calendar.timeInMillis = financeIntervalUnix.value!!.first
                calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1)
                calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMinimum(Calendar.DAY_OF_YEAR))
                val leftIntervalBorder = calendar.timeInMillis
                calendar.timeInMillis = financeIntervalUnix.value!!.second
                calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1)
                calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMinimum(Calendar.DAY_OF_YEAR))
                val rightIntervalBorder = calendar.timeInMillis
                _financeIntervalUnix.value = Pair(leftIntervalBorder, rightIntervalBorder)
                _financeIntervalString.value = DateFormat.format("yyyy", leftIntervalBorder).toString()
            }
            null -> {}
        }
        getAllCategoriesWithFinances()
    }

    fun getPastFinanceInterval() {
        val calendar = Calendar.getInstance()
        when (financeInterval.value) {
            FinanceInterval.DAY -> {
                calendar.timeInMillis = financeIntervalUnix.value!!.first
                val rightIntervalBorder = financeIntervalUnix.value!!.first
                calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 1)
                val leftIntervalBorder = calendar.timeInMillis
                _financeIntervalUnix.value = Pair(leftIntervalBorder, rightIntervalBorder)
                _financeIntervalString.value = DateFormat.format("dd MMM", leftIntervalBorder).toString()
            }
            FinanceInterval.WEEK -> {
                calendar.timeInMillis = financeIntervalUnix.value!!.first
                val rightIntervalBorder = financeIntervalUnix.value!!.first
                calendar.set(Calendar.WEEK_OF_YEAR, calendar.get(Calendar.WEEK_OF_YEAR) - 1)
                val leftIntervalBorder = calendar.timeInMillis
                _financeIntervalUnix.value = Pair(leftIntervalBorder, rightIntervalBorder)
                _financeIntervalString.value = "${DateFormat.format("dd MMM", leftIntervalBorder)} - ${DateFormat.format("dd MMM", rightIntervalBorder)}"
            }
            FinanceInterval.MONTH -> {
                calendar.timeInMillis = financeIntervalUnix.value!!.first
                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1)
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH))
                val leftIntervalBorder = calendar.timeInMillis
                calendar.timeInMillis = financeIntervalUnix.value!!.second
                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1)
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH))
                val rightIntervalBorder = calendar.timeInMillis
                _financeIntervalUnix.value = Pair(leftIntervalBorder, rightIntervalBorder)
                _financeIntervalString.value = DateFormat.format("MMM yyyy", leftIntervalBorder).toString()
            }
            FinanceInterval.YEAR -> {
                calendar.timeInMillis = financeIntervalUnix.value!!.first
                calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1)
                calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMinimum(Calendar.DAY_OF_YEAR))
                val leftIntervalBorder = calendar.timeInMillis
                calendar.timeInMillis = financeIntervalUnix.value!!.second
                calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1)
                calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMinimum(Calendar.DAY_OF_YEAR))
                val rightIntervalBorder = calendar.timeInMillis
                _financeIntervalUnix.value = Pair(leftIntervalBorder, rightIntervalBorder)
                _financeIntervalString.value = DateFormat.format("yyyy", leftIntervalBorder).toString()
            }
            null -> {}
        }
        getAllCategoriesWithFinances()
    }

    fun getAllCategoriesWithFinances() {
        val result = Single.create {
            var overallSum = 0.0
            val categoriesWithFinancesList = getAllCategoriesWithFinances.execute()
            val deleteFinancesList: MutableList<Finance> = mutableListOf()
            val deleteCategoriesList: MutableList<FinanceCategoryWithFinances> = mutableListOf()
            for (categoriesWithFinances in categoriesWithFinancesList) {
                if (categoriesWithFinances.financeCategory.state == financeCategoryState.value) {
                    if (categoriesWithFinances.financeList.isEmpty())
                        deleteCategoriesList.add(categoriesWithFinances)
                    else {
                        for (finance in categoriesWithFinances.financeList) {
                            if ((finance.date.time < financeIntervalUnix.value!!.first) || (finance.date.time > financeIntervalUnix.value!!.second))
                                deleteFinancesList.add(finance)
                            else {
                                overallSum += finance.sum
                                categoriesWithFinances.categorySum += finance.sum
                            }
                        }
                        categoriesWithFinances.financeList.removeAll(deleteFinancesList)
                        if (categoriesWithFinances.financeList.isEmpty())
                            deleteCategoriesList.add(categoriesWithFinances)
                        deleteFinancesList.clear()
                    }
                } else {
                    deleteCategoriesList.add(categoriesWithFinances)
                }
            }
            categoriesWithFinancesList.removeAll(deleteCategoriesList)

            for (categoriesWithFinances in categoriesWithFinancesList) {
                categoriesWithFinances.categoryPercentage = ((categoriesWithFinances.categorySum * 100) / overallSum).roundToInt()
            }
            categoriesWithFinancesList.sortBy { financeCategoryWithFinances -> financeCategoryWithFinances.categoryPercentage }

            it.onSuccess(Pair(categoriesWithFinancesList, overallSum))
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                _financeListState.value = ListState.LOADING
            }
            .subscribe({
                _categoriesWithFinancesList.value = it.first!!
                _overallSum.value = it.second!!
                if (_categoriesWithFinancesList.value!!.isEmpty())
                    _financeListState.value = ListState.EMPTY
                else
                    _financeListState.value = ListState.FILLED
                Log.e(TAG, "Categories with finances load success")
            }, {
                Log.e(TAG, it.message.toString())
            })
        disposeBag.add(result)
    }

//    fun getAllFinances() {
//        val result = Single.create {
//            it.onSuccess(getAllFinances.execute())
//        }
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({
//                _financeList.value = it
//                Log.e(TAG, "finances loaded in VM")
//            }, {
//                Log.e(TAG, it.message.toString())
//            })
//        disposeBag.add(result)
//    }

    fun onCurrencyDialogOpen(selectedCurrencyPosition: Int) {
        _isCurrencyDialogOpened.value = true
        _selectedCurrencyPosition.value = selectedCurrencyPosition
    }

    fun onCurrencyDialogClose() {
        _isCurrencyDialogOpened.value = false
    }

    fun onIntervalDialogOpen(selectedIntervalPosition: Int) {
        _isFinanceIntervalDialogOpened.value = true
        _selectedIntervalPosition.value = selectedIntervalPosition
    }

    fun onIntervalDialogClose() {
        _isFinanceIntervalDialogOpened.value = false
    }

    fun setCurrency(currency: String) {
        _currency.value = currency
        setFinanceCurrency.execute(currency = currency)
        getAllCategoriesWithFinances()
    }

    private fun getFinanceCurrency() {
        _currency.value = getFinanceCurrency.execute()
    }

    fun onChangeFinanceCategoryState() {
        _financeCategoryState.value = when (_financeCategoryState.value!!) {
            FinanceCategoryState.EXPENSE -> FinanceCategoryState.INCOME
            FinanceCategoryState.INCOME -> FinanceCategoryState.EXPENSE
        }
    }

    fun setInterval(interval: FinanceInterval) {
        _financeInterval.value = interval
        getFinanceInterval()
    }

//    fun onChangeFinanceListStateSwitch() {
//        when (_financeListState.value) {
//            FinanceListState.CATEGORIES -> {
//                _financeListState.value = FinanceListState.HISTORY
//                getAllFinances()
//            }
//            FinanceListState.HISTORY -> {
//                _financeListState.value = FinanceListState.CATEGORIES
//                getAllCategoriesWithFinances()
//            }
//            null -> {}
//        }
//    }
}
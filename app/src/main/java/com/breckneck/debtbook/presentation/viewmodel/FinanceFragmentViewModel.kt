package com.breckneck.debtbook.presentation.viewmodel

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
import com.breckneck.deptbook.domain.util.FinanceInterval
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.Calendar
import kotlin.math.roundToInt

class FinanceFragmentViewModel(
    private val getAllFinances: GetAllFinances,
    private val getAllFinanceCategories: GetAllFinanceCategories,
    private val getFinanceCurrency: GetFinanceCurrency,
    private val setFinanceCurrency: SetFinanceCurrency,
    private val getAllCategoriesWithFinances: GetAllCategoriesWithFinances
): ViewModel() {

    private val TAG = "FinanceFragmentVM"

//    private val _financeList = MutableLiveData<List<Finance>>()
//    val financeList: LiveData<List<Finance>>
//        get() = _financeList
//    private val _financeCategoryList = MutableLiveData<List<FinanceCategory>>()
//    val financeCategoryList: LiveData<List<FinanceCategory>>
//        get() = _financeCategoryList
    private val _categoriesWithFinancesList = MutableLiveData<List<FinanceCategoryWithFinances>>()
    val categoriesWithFinancesList: LiveData<List<FinanceCategoryWithFinances>>
        get() = _categoriesWithFinancesList
    private val _isRevenueSwitch = MutableLiveData<Boolean>(true)
    val isRevenueSwitch: LiveData<Boolean>
        get() = _isRevenueSwitch
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
    private val _financeInterval = MutableLiveData<FinanceInterval>(FinanceInterval.DAY)
    val financeInterval: LiveData<FinanceInterval>
        get() = _financeInterval
    private val _financeIntervalUnix = MutableLiveData<Pair<Long, Long>>() // 1 - left border in millis, 2 - right border in millis
    val financeIntervalUnix: LiveData<Pair<Long, Long>>
        get() = _financeIntervalUnix
    private val _overallSum = MutableLiveData<Double>(0.0)
    val overallSum: LiveData<Double>
        get() = _overallSum
//    private val _financeListState= MutableLiveData<FinanceListState>(FinanceListState.CATEGORIES)
//    val financeListState: LiveData<FinanceListState>
//        get() = _financeListState

    private val disposeBag = CompositeDisposable()

    init {
        getFinanceCurrency()
        getFinanceInterval()
//        getAllCategoriesWithFinances()
    }

    override fun onCleared() {
        super.onCleared()
        disposeBag.clear()
        Log.e(TAG, "Cleared")
    }

    fun getFinanceInterval() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.HOUR, 0)
        when (financeInterval.value) {
            FinanceInterval.DAY -> {
                val leftIntervalBorder = calendar.timeInMillis
                calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 1)
                val rightIntervalBorder = calendar.timeInMillis
                _financeIntervalUnix.value = Pair(leftIntervalBorder, rightIntervalBorder)
            }
            FinanceInterval.WEEK -> {
                calendar.set(Calendar.DAY_OF_YEAR, 0)
                calendar.set(Calendar.DAY_OF_MONTH, 0)
                val leftIntervalBorder = calendar.timeInMillis
                calendar.set(Calendar.WEEK_OF_YEAR, calendar.get(Calendar.WEEK_OF_YEAR) + 1)
                val rightIntervalBorder = calendar.timeInMillis
                _financeIntervalUnix.value = Pair(leftIntervalBorder, rightIntervalBorder)
            }
            FinanceInterval.MONTH ->  {
                val leftIntervalBorder = calendar.timeInMillis
                calendar.set(Calendar.WEEK_OF_YEAR, calendar.get(Calendar.WEEK_OF_YEAR) + 1)
                val rightIntervalBorder = calendar.timeInMillis
                _financeIntervalUnix.value = Pair(leftIntervalBorder, rightIntervalBorder)
            }
            FinanceInterval.YEAR -> {

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
                if (categoriesWithFinances.financeList.isEmpty())
                    deleteCategoriesList.add(categoriesWithFinances)
                else {
                    for (finance in categoriesWithFinances.financeList) {
                        if (isRevenueSwitch.value != finance.isRevenue)
                            deleteFinancesList.add(finance)
                        else if ((finance.date.time < financeIntervalUnix.value!!.first) || (finance.date.time > financeIntervalUnix.value!!.second))
                            deleteFinancesList.add(finance)
                        else {
                            overallSum += finance.sum
                            categoriesWithFinances.categorySum += categoriesWithFinances.categorySum + finance.sum
                        }
                    }
                    categoriesWithFinances.financeList.removeAll(deleteFinancesList)
                    if (categoriesWithFinances.financeList.isEmpty())
                        deleteCategoriesList.add(categoriesWithFinances)
                    deleteFinancesList.clear()
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
            .subscribe({
                _categoriesWithFinancesList.value = it.first!!
                _overallSum.value = it.second!!
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
    }

    private fun getFinanceCurrency() {
        _currency.value = getFinanceCurrency.execute()
    }

    fun onChangeIsRevenueSwitch() {
        _isRevenueSwitch.value = !(_isRevenueSwitch.value)!!
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
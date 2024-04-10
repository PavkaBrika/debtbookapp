package com.breckneck.debtbook.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.breckneck.deptbook.domain.model.Finance
import com.breckneck.deptbook.domain.model.FinanceCategory
import com.breckneck.deptbook.domain.usecase.Finance.SetFinance
import com.breckneck.deptbook.domain.usecase.FinanceCategory.GetAllFinanceCategories
import com.breckneck.deptbook.domain.usecase.Settings.GetFinanceCurrency
import com.breckneck.deptbook.domain.util.CreateFinanceState
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.Date

class CreateFinanceViewModel(
    private val setFinance: SetFinance,
    private val getAllFinanceCategories: GetAllFinanceCategories,
    private val getFinanceCurrency: GetFinanceCurrency
    ): ViewModel() {

    private val TAG = "CreateFinanceFragmentVM"

    private val disposeBag = CompositeDisposable()

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
    private val _isRevenue = MutableLiveData<Boolean>()
    val isRevenue: LiveData<Boolean>
        get() = _isRevenue
    private val _dayInMillis = MutableLiveData<Long>()
    val dayInMillis: LiveData<Long>
        get() = _dayInMillis
    private val _financeEdit = MutableLiveData<Finance>()
    val financeEdit: LiveData<Finance>
        get() = _financeEdit
    private val _createFinanceState = MutableLiveData<CreateFinanceState>()
    val createFinanceState: LiveData<CreateFinanceState>
        get() = _createFinanceState

    init {
        Log.e(TAG, "Initialized")
//        getAllFinanceCategories()
//        getCurrentDate()
        getFinanceCurrency()
    }

    override fun onCleared() {
        super.onCleared()
        disposeBag.clear()
        Log.e(TAG, "Cleared")
    }

    fun getAllFinanceCategories() {
        val result = Single.create {
            it.onSuccess(getAllFinanceCategories.execute())
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e(TAG, "Finance categories loaded")
                _financeCategoryList.value = it
            }, {
                Log.e(TAG, it.message.toString())
            })
        disposeBag.add(result)
    }

    fun setFinance(finance: Finance) {
        val result = Completable.create {
            setFinance.execute(finance = finance)
            it.onComplete()
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e(TAG, "finance added")
            }, {
                Log.e(TAG, it.message.toString())
            })
        disposeBag.add(result)
    }

    fun getFinanceCurrency() {
        _currency.value = getFinanceCurrency.execute()
    }

    fun setFinanceEdit(finance: Finance) {
        _financeEdit.value = finance
    }

    fun setIsRevenue(isRevenue: Boolean) {
        _isRevenue.value = isRevenue
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

    fun setCreateFinanceState(createFinanceState: CreateFinanceState) {
        _createFinanceState.value = createFinanceState
    }
}
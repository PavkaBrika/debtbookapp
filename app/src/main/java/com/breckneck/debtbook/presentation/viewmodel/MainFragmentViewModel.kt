package com.breckneck.debtbook.presentation.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.breckneck.deptbook.domain.model.HumanDomain
import com.breckneck.deptbook.domain.usecase.Debt.GetDebtQuantity
import com.breckneck.deptbook.domain.usecase.Human.GetAllDebtsSumUseCase
import com.breckneck.deptbook.domain.usecase.Human.GetAllHumansUseCase
import com.breckneck.deptbook.domain.usecase.Human.GetNegativeHumansUseCase
import com.breckneck.deptbook.domain.usecase.Human.GetPositiveHumansUseCase
import com.breckneck.deptbook.domain.usecase.Settings.GetFirstMainCurrency
import com.breckneck.deptbook.domain.usecase.Settings.GetSecondMainCurrency
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class MainFragmentViewModel(
    private val getAllHumansUseCase: GetAllHumansUseCase,
    private val getAllDebtsSumUseCase: GetAllDebtsSumUseCase,
    private val getPositiveHumansUseCase: GetPositiveHumansUseCase,
    private val getNegativeHumansUseCase: GetNegativeHumansUseCase,
    private val getFirstMainCurrency: GetFirstMainCurrency,
    private val getSecondMainCurrency: GetSecondMainCurrency
) : ViewModel() {

    var resultPos = MutableLiveData<String>()
    var resultNeg = MutableLiveData<String>()
    var resultHumanList = MutableLiveData<List<HumanDomain>>()
    var resultIsFilterDialogShown = MutableLiveData<Boolean>()
    var resultHumansFilter = MutableLiveData<Int>()

    init {
        Log.e("TAG", "MainFragment VM created")
    }

    override fun onCleared() {
        Log.e("TAG", "MainFragment VM cleared")
        super.onCleared()
    }

    @SuppressLint("CheckResult")
    fun getAllHumans() {
        Single.just("1")
            .map {
                return@map getAllHumansUseCase.execute()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                resultHumanList.value = it
                Log.e("TAG", "humans loaded in VM")
            }, {})

    }

    @SuppressLint("CheckResult")
    fun getPositiveHumans() {
        Single.just("1")
            .map {
                return@map getPositiveHumansUseCase.execute()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                resultHumanList.value = it
                Log.e("TAG", "humans loaded in VM")
            }, {})

    }

    fun getNegativeHumans() {
        Single.just("1")
            .map {
                return@map getNegativeHumansUseCase.execute()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                resultHumanList.value = it
                Log.e("TAG", "humans loaded in VM")
            }, {})

    }

    fun getPositiveSum() {
        Single.just("1")
            .map {
                return@map getAllDebtsSumUseCase.execute(
                    "positive",
                    getFirstMainCurrency.execute(),
                    getSecondMainCurrency.execute())
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                resultPos.value = it
            },{})
    }

    fun getNegativeSum() {
        Single.just("1")
            .map {
                return@map getAllDebtsSumUseCase.execute(
                    "negative",
                    getFirstMainCurrency.execute(),
                    getSecondMainCurrency.execute())
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                resultNeg.value = it
            },{})
    }

    fun setFilterDialogShown(shown: Boolean) {
        resultIsFilterDialogShown.value = shown
    }

    fun setHumansFilter(filter: Int) {
        resultHumansFilter.value = filter
    }
}
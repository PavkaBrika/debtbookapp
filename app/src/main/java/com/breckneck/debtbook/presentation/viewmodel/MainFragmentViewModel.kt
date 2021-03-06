package com.breckneck.debtbook.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.breckneck.deptbook.domain.model.HumanDomain
import com.breckneck.deptbook.domain.usecase.Human.GetAllDebtsSumUseCase
import com.breckneck.deptbook.domain.usecase.Human.GetAllHumansUseCase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class MainFragmentViewModel(
    private val getAllHumansUseCase: GetAllHumansUseCase,
    private val getAllDebtsSumUseCase: GetAllDebtsSumUseCase
) : ViewModel() {

    var resultPos = MutableLiveData<String>()
    var resultNeg = MutableLiveData<String>()
    var resultHumanList = MutableLiveData<List<HumanDomain>>()

    init {
        Log.e("TAG", "MainFragment VM created")
    }

    override fun onCleared() {
        Log.e("TAG", "MainFragment VM cleared")
        super.onCleared()
    }

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

    fun getPositiveSum() {
        Single.just("1")
            .map {
                return@map getAllDebtsSumUseCase.execute("positive")
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
                return@map getAllDebtsSumUseCase.execute("negative")
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                resultNeg.value = it
            },{})
    }


}
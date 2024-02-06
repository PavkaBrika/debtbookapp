package com.breckneck.debtbook.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.breckneck.deptbook.domain.usecase.Debt.GetDebtQuantity
import com.breckneck.deptbook.domain.usecase.Settings.GetAppIsRated
import com.breckneck.deptbook.domain.usecase.Settings.GetDebtQuantityForAppRateDialogShow
import com.breckneck.deptbook.domain.usecase.Settings.SetAppIsRated
import com.breckneck.deptbook.domain.usecase.Settings.SetDebtQuantityForAppRateDialogShow
import com.breckneck.deptbook.domain.util.DEBT_QUANTITY_FOR_NEXT_SHOW
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MainActivityViewModel(
    private val getDebtQuantity: GetDebtQuantity,
    private val getAppIsRated: GetAppIsRated,
    private val setAppIsRated: SetAppIsRated,
    private val getDebtQuantityForAppRateDialogShow: GetDebtQuantityForAppRateDialogShow,
    private val setDebtQuantityForAppRateDialogShow: SetDebtQuantityForAppRateDialogShow
) : ViewModel() {

    private val TAG = "MainActivityViewModel"

    private val _isAppRateDialogShow = MutableLiveData<Boolean>()
    val isAppRateDialogShow: LiveData<Boolean>
        get() = _isAppRateDialogShow
    private val _isAppReviewDialogShow = MutableLiveData<Boolean>()
    val isAppReviewDialogShow: LiveData<Boolean>
        get() = _isAppReviewDialogShow
    private val _isAppReviewDialogFromSettings = MutableLiveData<Boolean>()
    val isAppReviewDialogFromSettings: LiveData<Boolean>
        get() = _isAppReviewDialogFromSettings
    private val _debtQuantity = MutableLiveData<Int>()
    val debtQuantity: LiveData<Int>
        get() = _debtQuantity
    private val _appRate = MutableLiveData<Int>()
    val appRate: LiveData<Int>
        get() = _appRate
    private val _appReviewText = MutableLiveData<String>()
    val appReviewText: LiveData<String>
        get() = _appReviewText
    private val _isAppRated = MutableLiveData<Boolean>()
    val isAppRated: LiveData<Boolean>
        get() = _isAppRated
    private val _debtQuantityForAppRateDialogShow = MutableLiveData<Int>()
    val debtQuantityForAppRateDialogShow: LiveData<Int>
        get() = _debtQuantityForAppRateDialogShow

    private val disposeBag = CompositeDisposable()

    init {
        Log.e(TAG, "Main Activity View Model Started")
        getDebtQuantityForAppRateDialogShow()
        getDebtQuantity()
        getIsAppRated()
    }

    override fun onCleared() {
        super.onCleared()
        disposeBag.clear()
        Log.e(TAG, "Main Activity View Model cleared")
    }

    private fun getDebtQuantity() {
        val result = Single.create {
            it.onSuccess(getDebtQuantity.execute())
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _debtQuantity.value = it
            }, {
                Log.e(TAG, it.stackTrace.toString())
            })
        disposeBag.add(result)
    }

    fun setAppRateDialogShown(shown: Boolean) {
        _isAppRateDialogShow.value = shown
    }

    fun setAppReviewDialogShown(shown: Boolean) {
        _isAppReviewDialogShow.value = shown
    }

    fun setAppReviewFromSettings(fromSettings: Boolean) {
        _isAppReviewDialogFromSettings.value = fromSettings
    }

    fun setAppRate(rate: Int) {
        _appRate.value = rate
    }

    fun setAppReviewText(text: String) {
        _appReviewText.value = text
    }

    private fun getIsAppRated() {
        _isAppRated.value = getAppIsRated.execute()
    }

    fun onAppRate() {
        _isAppRated.value = true
        setAppIsRated.execute(isRated = true)
    }

    fun onAppRateDismiss() {
        _debtQuantityForAppRateDialogShow.value = _debtQuantityForAppRateDialogShow.value!! + DEBT_QUANTITY_FOR_NEXT_SHOW
        setDebtQuantityForAppRateDialogShow.execute(debtQuantityForAppRateDialogShow.value!!)
    }

    fun onAddDebt() {
        _debtQuantity.value = _debtQuantity.value!! + 1
    }

    private fun getDebtQuantityForAppRateDialogShow() {
        _debtQuantityForAppRateDialogShow.value = getDebtQuantityForAppRateDialogShow.execute()
    }
}
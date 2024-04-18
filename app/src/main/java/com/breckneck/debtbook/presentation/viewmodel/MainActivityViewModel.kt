package com.breckneck.debtbook.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.breckneck.deptbook.domain.usecase.Ad.SaveClicksUseCase
import com.breckneck.deptbook.domain.usecase.Ad.GetClicksUseCase
import com.breckneck.deptbook.domain.usecase.Debt.GetDebtQuantity
import com.breckneck.deptbook.domain.usecase.Settings.GetAppTheme
import com.breckneck.deptbook.domain.usecase.Settings.GetDebtQuantityForAppRateDialogShow
import com.breckneck.deptbook.domain.usecase.Settings.SetDebtQuantityForAppRateDialogShow
import com.breckneck.deptbook.domain.util.DEBT_QUANTITY_FOR_NEXT_SHOW_APP_RATE_DIALOG
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MainActivityViewModel(
    private val getDebtQuantity: GetDebtQuantity,
    private val getDebtQuantityForAppRateDialogShow: GetDebtQuantityForAppRateDialogShow,
    private val setDebtQuantityForAppRateDialogShow: SetDebtQuantityForAppRateDialogShow,
    private val getClicksUseCase: GetClicksUseCase,
    private val saveClicks: SaveClicksUseCase,
    private val getAppTheme: GetAppTheme
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
    private val _debtQuantity = MutableLiveData<Int>(0)
    val debtQuantity: LiveData<Int>
        get() = _debtQuantity
    private val _appRate = MutableLiveData<Int>()
    val appRate: LiveData<Int>
        get() = _appRate
    private val _appReviewText = MutableLiveData<String>()
    val appReviewText: LiveData<String>
        get() = _appReviewText
    private val _debtQuantityForAppRateDialogShow = MutableLiveData<Int>()
    val debtQuantityForAppRateDialogShow: LiveData<Int>
        get() = _debtQuantityForAppRateDialogShow
    private val _adClicksCounter = MutableLiveData<Int>()
    val adClicksCounter: LiveData<Int>
        get() = _adClicksCounter
    private val _appTheme = MutableLiveData<String>()
    val appTheme: LiveData<String>
        get() = _appTheme
    private val _isNeedUpdateDebtData = MutableLiveData<Boolean>(false)
    val isNeedDebtDataUpdate: LiveData<Boolean>
        get() = _isNeedUpdateDebtData
    private val _isNeedUpdateDebtSums = MutableLiveData<Boolean>(false)
    val isNeedUpdateDebtSums: LiveData<Boolean>
        get() = _isNeedUpdateDebtSums
    private val _isBottomNavViewVisible = MutableLiveData<Boolean>(true)
    val isBottomNavViewVisible: LiveData<Boolean>
        get() = _isBottomNavViewVisible

    private val disposeBag = CompositeDisposable()

    init {
        Log.e(TAG, "Main Activity View Model Started")
        getDebtQuantityForAppRateDialogShow()
        getDebtQuantity()
        getAdClicksCounter()
        getAppTheme()
    }

    override fun onCleared() {
        super.onCleared()
        saveClicks.execute(adClicksCounter.value!!)
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
                Log.e(TAG, it.message.toString())
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

    fun onAppRateDismiss() {
        _debtQuantityForAppRateDialogShow.value = _debtQuantityForAppRateDialogShow.value!! + DEBT_QUANTITY_FOR_NEXT_SHOW_APP_RATE_DIALOG
        setDebtQuantityForAppRateDialogShow.execute(debtQuantityForAppRateDialogShow.value!!)
    }

    fun onAddDebt() {
        _debtQuantity.value = _debtQuantity.value!! + 1
    }

    private fun getDebtQuantityForAppRateDialogShow() {
        _debtQuantityForAppRateDialogShow.value = getDebtQuantityForAppRateDialogShow.execute()
    }

    private fun getAdClicksCounter() {
        _adClicksCounter.value = getClicksUseCase.execute()
    }

    fun onActionClick() {
        _adClicksCounter.value = _adClicksCounter.value!! + 1
    }

    fun onAdShow() {
        _adClicksCounter.value = 0
    }

    fun getAppTheme() {
        _appTheme.value = getAppTheme.execute()
    }

    fun setIsNeedUpdateDebtData(isNeedUpdate: Boolean) {
        _isNeedUpdateDebtData.value = isNeedUpdate
    }

    fun setIsNeedUpdateDebtSums(isNeedUpdate: Boolean) {
        _isNeedUpdateDebtSums.value = isNeedUpdate
    }

    fun setIsBottomNavBarVisible(isVisible: Boolean) {
        _isBottomNavViewVisible.value = isVisible
    }

}